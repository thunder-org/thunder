package org.conqueror.lion.cluster.job;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.ManagerActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.conqueror.lion.message.JobMasterMessage.*;
import org.conqueror.lion.message.TaskMasterMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public abstract class JobManager<T extends JobConfig> extends ManagerActor {

    private final String jobName;

    private final Map<String, ActorRef> taskMasters;
    private final Map<String, ActorRef> taskManagers;

    public static Props props(Class jobManagerClass, JobConfig config, ActorRef master) {
        return Props.create(jobManagerClass, config, master);
    }

    public JobManager(T config, ActorRef master) {
        super(config, master);

        jobName = config.getName();
        taskMasters = new HashMap<>(config.getMaxNumberOfTaskManagers());
        taskManagers = new HashMap<>(config.getMaxNumberOfTaskManagers());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

            // task-master
            .match(TaskMasterMessage.TaskMasterAssignResponse.class, this::processAssignTaskManagers)
            .match(TaskMasterMessage.TaskManagerCreateResponse.class, this::processAssignTaskManagers)

            // task-manager
            .match(JobManagerMessage.TaskAssignRequest.class, this::processAssignTask)
            .match(JobManagerMessage.TaskManagerFinishRequest.class, this::processFinishTask)

            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        prepareJob();

        // task master 할당 요청
        tellToMaster(new TaskMasterMessage.TaskMasterAssignRequest());
    }

    @Override
    public void postStop() throws Exception {
        finishJob();

        super.postStop();
    }


    protected abstract void prepareJob() throws Exception;

    protected abstract void finishJob() throws Exception;

    protected abstract JobManagerMessage.TaskAssignResponse assignTask(JobManagerMessage.TaskAssignRequest request) throws Exception;

    protected void processAssignTaskManagers(TaskMasterMessage.TaskMasterAssignResponse response) {
        if (getConfig().getMaxNumberOfTaskManagers() > taskMasters.size()) {
            ActorRef taskMaster = response.getTaskMaster(getContext().getSystem());
            taskMasters.put(response.getId(), taskMaster);

            taskMaster.tell(new TaskMasterMessage.TaskManagerCreateRequest(getJobName(), getConfig()), getSelf());

            log().info("TASK-MANAGERS : {} / {}", getConfig().getMaxNumberOfTaskManagers(), taskMasters.size());
        }
    }

    protected void processAssignTaskManagers(TaskMasterMessage.TaskManagerCreateResponse response) {
        ActorRef taskManager = response.getTaskManager(getContext().getSystem());
        if (Objects.nonNull(taskManager)) {
            taskManagers.put(response.getTaskManagerName(), taskManager);

            log().info("TASK-MANAGER : {}", taskManager);
        }
    }

    protected void processAssignTask(JobManagerMessage.TaskAssignRequest request) throws Exception {
        JobManagerMessage.TaskAssignResponse response = assignTask(request);
        getSender().tell(response, getSelf());
//        log().info("task is assigned to {} : {}", getSender().path(), response);
    }

    protected void processFinishTask(JobManagerMessage.TaskManagerFinishRequest request) {
        log().info("finish task-manager - {}", request.taskManagerName);

        taskManagers.remove(request.getTaskManagerName());
        if (taskManagers.isEmpty()) {
            tellToMaster(new JobManagerFinishRequest(getJobName()));
        }
    }

    protected String getJobName() {
        return jobName;
    }

    @Override
    protected T getConfig() {
        //noinspection unchecked
        return (T) super.getConfig();
    }

}
