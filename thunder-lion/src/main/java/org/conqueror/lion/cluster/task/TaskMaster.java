package org.conqueror.lion.cluster.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.TaskMasterMessage;
import org.conqueror.lion.message.TaskMasterMessage.TaskManagerCreateRequest;
import org.conqueror.lion.message.TaskMasterMessage.TaskManagerRemoveRequest;

import java.util.HashMap;
import java.util.Map;


public class TaskMaster extends NodeComponentActor {

    private final Map<String, ActorRef> registeredTaskManagers = new HashMap<>();

    public TaskMaster(NodeConfig config, ActorRef nodeMaster) {
        super(config, nodeMaster);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            // task-master
            .match(TaskMasterMessage.TaskMasterAssignRequest.class, this::processAssignTaskMaster)

            // task-manager
            .match(TaskManagerCreateRequest.class, this::processCreateTaskManager)
            .match(TaskManagerRemoveRequest.class, this::processRemoveTaskManager)
            .build();
    }

    @Override
    protected void postWorking() {

    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.TaskMasterIssueIDRequest();
    }

    protected Props createTaskManagerProps(JobConfig config, ActorRef jobManager) throws ClassNotFoundException {
        return TaskManager.props(Class.forName(config.getTaskManagerClass()), config, getSelf(), jobManager);
    }

    private void processAssignTaskMaster(TaskMasterMessage.TaskMasterAssignRequest request) {
        getSender().tell(new TaskMasterMessage.TaskMasterAssignResponse(getId(), getSelf()), getSelf());
    }

    private void processCreateTaskManager(TaskManagerCreateRequest request) {
        try {
            String jobName = request.getConfig().getName();
            String taskManagerName = makeTaskManagerName(jobName);
            ActorRef manager = getContext().actorOf(createTaskManagerProps(request.getConfig(), getSender()), taskManagerName);

            getContext().watch(manager);
            registeredTaskManagers.put(taskManagerName, manager);

            getSender().tell(new TaskMasterMessage.TaskManagerCreateResponse(taskManagerName, manager), getSender());

            log().info("task-manager was created ({})", jobName);
        } catch (ClassNotFoundException e) {
            log().error(e, "failed to create the task-manager ({})"
                , request.getConfig().getJobManagerClass());

            getSender().tell(new TaskMasterMessage.TaskManagerCreateResponse(getId(), null), getSender());
        }
    }

    private void processRemoveTaskManager(TaskManagerRemoveRequest request) {
        String taskManagerName = getSender().path().name();
        registeredTaskManagers.remove(taskManagerName);
        getContext().unwatch(getSender());
        getContext().stop(getSender());

        log().info("task-manager was removed ({})", taskManagerName);
    }

    private String makeTaskManagerName(String jobName) {
        return getId() + "-" + jobName;
    }

}
