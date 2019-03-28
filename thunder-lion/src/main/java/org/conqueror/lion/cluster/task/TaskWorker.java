package org.conqueror.lion.cluster.task;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.conqueror.lion.message.ThunderMessage;
import org.conqueror.lion.message.TaskManagerMessage;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public abstract class TaskWorker<C extends JobConfig, T extends JobManagerMessage.TaskAssignResponse> extends AbstractLoggingActor {

    private final C config;
    private final ActorRef taskManager;

    public static Props props(Class taskWorkerClass, JobConfig jobConfig, ActorRef taskManager) {
        return Props.create(taskWorkerClass, jobConfig, taskManager);
    }

    public static Props props(Class taskWorkerClass, JobConfig jobConfig, ActorRef taskManager, ActorRef transferTo) {
        return Props.create(taskWorkerClass, jobConfig, taskManager, transferTo);
    }

    public TaskWorker(JobConfig config, ActorRef taskManager) {
        this.config = (C) config;
        this.taskManager = taskManager;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(JobManagerMessage.TaskAssignFinishResponse.class, this::processFinishTask)
            .match(JobManagerMessage.TaskAssignWaitingResponse.class, this::processWaitingTask)
            .match(JobManagerMessage.TaskAssignResponse.class, this::processAssignedTask)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        requestTask();

        super.preStart();
    }

    protected abstract void work(T source) throws Exception;

    protected abstract JobManagerMessage.TaskAssignRequest createTaskAssignRequest();

    protected C getConfig() {
        return config;
    }

    protected void sendToTaskManager(ThunderMessage message) {
        taskManager.tell(message, getSelf());
    }

    protected ActorRef getTaskManager() {
        return taskManager;
    }

    // task-manager에 자신(task-worker)를 종료해달라고 요청
    protected void processFinishTask(JobManagerMessage.TaskAssignFinishResponse response) {
        taskManager.tell(new TaskManagerMessage.TaskWorkerFinishRequest(), getSelf());
    }

    protected void processWaitingTask(JobManagerMessage.TaskAssignWaitingResponse response) {
        getContext().getSystem().scheduler().scheduleOnce(Duration.create(response.getWaitingSec(), TimeUnit.SECONDS),
            this::requestTask, getContext().getSystem().getDispatcher());
    }

    protected void processAssignedTask(JobManagerMessage.TaskAssignResponse response) throws Exception {
        //noinspection unchecked
        work((T) response);

        // request next task
        requestTask();
    }

    protected void requestTask() {
        taskManager.tell(createTaskAssignRequest(), getSelf());
    }

}
