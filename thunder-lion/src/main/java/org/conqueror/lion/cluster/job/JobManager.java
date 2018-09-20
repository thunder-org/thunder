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

    public JobManager(JobConfig config, ActorRef master) {
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

            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })

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

    /*
     * task master 할당이 되면
     * TODO : 각 마스터에 task manager 생성 요청
     * TODO : task manager 리스트 얻기
     * TODO : 각 task manager 는 task worker 생성
     * TODO : task manager가 준비되면 JobManagerStartedTaskRequest 요청으로 준비됨을 알림
     * TODO : task worker는 task manager를 통해 task를 요청하고 task manager가 job manager에게 task를 요청함
     */
    protected void processAssignTaskManagers(TaskMasterMessage.TaskMasterAssignResponse response) {
        if (getConfig().getMaxNumberOfTaskManagers() > taskMasters.size()) {
            ActorRef taskMaster = response.getTaskMaster();
            taskMasters.put(response.getId(), taskMaster);

            taskMaster.tell(new TaskMasterMessage.TaskManagerCreateRequest(getJobName(), getConfig()), getSelf());
        }
    }

    protected void processAssignTaskManagers(TaskMasterMessage.TaskManagerCreateResponse response) {
        if (Objects.nonNull(response.getTaskManager())) {
            taskManagers.put(response.getTaskManagerName(), response.getTaskManager());
        }
    }

    protected void processAssignTask(JobManagerMessage.TaskAssignRequest request) throws Exception {
        getSender().tell(assignTask(request), getSelf());
        log().info("task is assigned to {}", getSender());
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
        return (T) super.getConfig();
    }

}
