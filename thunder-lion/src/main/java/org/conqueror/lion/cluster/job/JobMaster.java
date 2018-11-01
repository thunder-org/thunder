package org.conqueror.lion.cluster.job;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.JobMasterMessage;
import org.conqueror.lion.message.TaskMasterMessage;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class JobMaster extends NodeComponentActor {

    private final Map<String, ActorRef> registeredJobManagers = new HashMap<>();

    public JobMaster(NodeConfig config, ActorRef nodeMaster) {
        super(config, nodeMaster);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            // task-master
            .match(TaskMasterMessage.TaskMasterAssignRequest.class, this::processAssignTaskMaster)

            .match(JobMasterMessage.JobManagerCreateRequest.class, this::processCreateJobManager)
            .match(JobMasterMessage.JobManagerFinishRequest.class, this::processRemoveJobManager)
            .build();
    }

    @Override
    protected void postWorking() {

    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.JobMasterIssueIDRequest();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(0, Duration.create(30, TimeUnit.SECONDS),
            DeciderBuilder
                .match(Exception.class, e -> SupervisorStrategy.stop())
                .matchAny(o -> SupervisorStrategy.escalate())
                .build()
        );
    }

    private Props createJobManagerProps(JobConfig config) throws ClassNotFoundException {
        return JobManager.props(Class.forName(config.getJobManagerClass()), config, getSelf());
    }

    private void processCreateJobManager(JobMasterMessage.JobManagerCreateRequest request) {
        String jobName = request.getConfig().getName();

        try {
            ActorRef manager = getContext().actorOf(createJobManagerProps(request.getConfig()), jobName);
            getContext().watch(manager);
            registeredJobManagers.put(jobName, manager);
        } catch (ClassNotFoundException e) {
            log().error(e, "failed to create the job-manager, class not found ({})"
                , request.getConfig().getJobManagerClass());
        } catch (InvalidActorNameException e) {
            log().error(e, "failed to create the job-manager, job-manager exist already ({})"
                , jobName);
        }
    }

    private void processRemoveJobManager(JobMasterMessage.JobManagerFinishRequest request) {
        String jobName = request.getJobName();
        ActorRef manager = registeredJobManagers.get(jobName);
        if (manager != null) {
            registeredJobManagers.remove(jobName);
            getContext().unwatch(manager);
            /*
             * stop
             *  - Processing of the current message, if any, will continue before the actor is stopped,
             *    but additional messages in the mailbox will not be processed
             * PoisonPill
             *  - PoisonPill is enqueued as ordinary messages
             *    and will be handled after messages that were already queued in the mailbox.
             */
            getContext().stop(manager);

            log().info("the job manager was removed ({})", jobName);
        }
    }

    private void processAssignTaskMaster(TaskMasterMessage.TaskMasterAssignRequest request) {
        forwardToNode(request);
    }

}
