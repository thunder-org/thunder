package org.conqueror.lion.cluster.task;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.conqueror.lion.message.LionMessage;
import org.conqueror.lion.message.TaskManagerMessage;


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
            .match(JobManagerMessage.TaskAssignResponse.class, this::processTask)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        requestTask();

        super.preStart();
    }

    protected abstract void work(JobManagerMessage.TaskAssignResponse source) throws Exception;

    protected abstract JobManagerMessage.TaskAssignRequest createTaskAssignRequest();

    protected C getConfig() {
        return config;
    }

    protected void sendToTaskManager(LionMessage message) {
        taskManager.tell(message, getSelf());
    }

    private void processFinishTask(JobManagerMessage.TaskAssignFinishResponse response) {
        taskManager.tell(new TaskManagerMessage.TaskWorkerFinishRequest(), getSelf());
    }

    protected void processTask(JobManagerMessage.TaskAssignResponse response) throws Exception {
        work(response);

        // request next task
        requestTask();
    }

    private void requestTask() {
        taskManager.tell(createTaskAssignRequest(), getSelf());
    }

}
