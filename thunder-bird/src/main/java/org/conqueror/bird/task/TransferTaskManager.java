package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.routing.Broadcast;
import akka.routing.SmallestMailboxPool;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.lion.cluster.actor.ManagerActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.LionException;
import org.conqueror.lion.message.JobManagerMessage;


public abstract class TransferTaskManager<T extends JobConfig> extends ManagerActor {

    private final ActorRef transferTo;

    private ActorRef workerRouter;

    public TransferTaskManager(JobConfig config, ActorRef taskManager, ActorRef transferTo) {
        super(config, taskManager);

        this.transferTo = transferTo;

        log().info("{} is created", getSelf().path().name());
    }

    public Receive createReceive() {
        return receiveBuilder()
            // job-manager
            .match(JobManagerMessage.TaskAssignFinishResponse.class, this::processFinishTask)
            .match(BirdMessage.class, this::processAssignTask)
            .match(Terminated.class, this::processFinishTask)
            .build();
    }

    @Override
    protected T getConfig() {
        return (T) super.getConfig();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        prepareJob();

        createTransferTaskWorkers(getConfig().getMaxNumberOfTaskWorkers());
    }

    @Override
    public void postStop() throws Exception {
        finishJob();

        super.postStop();
    }

    protected ActorRef getTaskManager() {
        return getMaster();
    }

    protected abstract void prepareJob() throws Exception;

    protected abstract void finishJob() throws Exception;

    protected abstract Props createTransferTaskWorkerProps();

    protected abstract String makeTransferTaskWorkerRouterName();

    protected void processFinishTask(Terminated response) {
        if (response.getActor().equals(workerRouter)) {
            getContext().stop(getSelf());
        }
    }

    protected void processFinishTask(JobManagerMessage.TaskAssignFinishResponse response) {
        /*
            This will cause the routee to stop.
            After all routees have stopped the router will itself be stopped automatically.
         */
        workerRouter.tell(new Broadcast(PoisonPill.getInstance()), getSelf());
    }

    protected void processAssignTask(BirdMessage taskSource) {
        workerRouter.tell(taskSource, getSelf());
    }

    private void createTransferTaskWorkers(int numberOfAssignedTaskWorkers) throws LionException {
        // router
        workerRouter = getContext().actorOf(new SmallestMailboxPool(numberOfAssignedTaskWorkers).props(createTransferTaskWorkerProps())
            , makeTransferTaskWorkerRouterName());
        getContext().watch(workerRouter);
    }

    protected ActorRef getTransferTo() {
        return transferTo;
    }

}
