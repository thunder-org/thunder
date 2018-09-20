package org.conqueror.lion.cluster.job;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.JobMasterMessage;
import org.conqueror.lion.message.TaskMasterMessage;

import java.util.HashMap;
import java.util.Map;


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

    private Props createJobManagerProps(JobConfig config) throws ClassNotFoundException {
        return JobManager.props(Class.forName(config.getJobManagerClass()), config, getSelf());
    }

    private void processCreateJobManager(JobMasterMessage.JobManagerCreateRequest request) {
        try {
            String jobName = request.getConfig().getName();
            ActorRef manager = getContext().actorOf(createJobManagerProps(request.getConfig()), jobName);
            getContext().watch(manager);
            registeredJobManagers.put(jobName, manager);
        } catch (ClassNotFoundException e) {
            log().error(e, "failed to create the job-manager ({})"
                , request.getConfig().getJobManagerClass());
        } finally {
            getSender().tell(new JobMasterMessage.JobManagerCreateResponse(), getSender());
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
