package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.routing.Broadcast;
import akka.routing.SmallestMailboxPool;
import org.conqueror.lion.cluster.actor.ManagerActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.LionException;
import org.conqueror.lion.message.JobManagerMessage;


public abstract class DeliveryTaskManager<T extends JobConfig> extends ManagerActor {

    private final ActorRef deliverTo;

    private ActorRef workerRouter;

    public DeliveryTaskManager(JobConfig config, ActorRef taskManager, ActorRef deliverTo) {
        super(config, taskManager);

        this.deliverTo = deliverTo;

        log().info("{} is created", getSelf().path().name());
    }

    public Receive createReceive() {
        return receiveBuilder()
            // job-manager
            .match(JobManagerMessage.TaskAssignFinishResponse.class, this::processFinishTask)
            .match(getAssignTaskMessageClass(), this::processAssignTask)
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

        createDeliveryTaskWorkers(getNumberOfTaskWorkers());
    }

    @Override
    public void postStop() throws Exception {
        finishJob();

        super.postStop();
    }

    protected ActorRef getTaskManager() {
        return getMaster();
    }

    protected abstract int getNumberOfTaskWorkers();

    protected abstract void prepareJob() throws Exception;

    protected abstract void finishJob() throws Exception;

    protected abstract Props createDeliveryTaskWorkerProps();

    protected abstract String makeDeliveryTaskWorkerRouterName();

    protected abstract Class<?> getAssignTaskMessageClass();

    protected void processFinishTask(Terminated response) {
        if (response.getActor().equals(workerRouter)) {
            getContext().stop(getSelf());
        }
    }

    private int remainStopTries = 5;

    protected void processFinishTask(JobManagerMessage.TaskAssignFinishResponse response) {
        if (remainStopTries-- > 0) {
            getSelf().tell(response, getSender());
        } else {
            /*
                This will cause the routee to stop.
                After all routees have stopped the router will itself be stopped automatically.
             */
            workerRouter.tell(new Broadcast(PoisonPill.getInstance()), getSelf());
        }
    }

    protected void processAssignTask(Object taskSource) {
        workerRouter.tell(taskSource, getSelf());
    }

    private void createDeliveryTaskWorkers(int numberOfAssignedTaskWorkers) throws LionException {
        // router
        workerRouter = getContext().actorOf(new SmallestMailboxPool(numberOfAssignedTaskWorkers).props(createDeliveryTaskWorkerProps())
            , makeDeliveryTaskWorkerRouterName());
        getContext().watch(workerRouter);
    }

    protected ActorRef getDeliverTo() {
        return deliverTo;
    }

}
