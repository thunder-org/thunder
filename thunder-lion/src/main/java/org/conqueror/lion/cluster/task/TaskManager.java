package org.conqueror.lion.cluster.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.ManagerActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;
import org.conqueror.lion.message.TaskManagerMessage;
import org.conqueror.lion.message.TaskMasterMessage;

import java.util.HashMap;
import java.util.Map;


public abstract class TaskManager<T extends JobConfig> extends ManagerActor {

    private final String jobName;

    private final ActorRef jobManager;

    private final Map<String, ActorRef> taskWorkers;

    public static Props props(Class taskManagerClass, JobConfig jobConfig, ActorRef master, ActorRef jobManager) {
        return Props.create(taskManagerClass, jobConfig, master, jobManager);
    }

    public TaskManager(JobConfig config, ActorRef master, ActorRef jobManager) {
        super(config, master);

        jobName = config.getName();
        this.jobManager = jobManager;
        taskWorkers = new HashMap<>(config.getMaxNumberOfTaskWorkers());
    }

    public Receive createReceive() {
        return receiveBuilder()
            // job-manager
            .match(JobManagerMessage.TaskAssignRequest.class, this::processAssignTask)

            .match(TaskManagerMessage.TaskWorkerFinishRequest.class, this::processRemoveTaskWorker)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        prepareJob();

        createTaskWorkers(getConfig().getMaxNumberOfTaskWorkers());
    }

    @Override
    public void postStop() throws Exception {
        finishJob();

        stopAllWorkers();

        jobManager.tell(new JobManagerMessage.TaskManagerFinishRequest(getName()), getSelf());

        super.postStop();
    }

    protected abstract void prepareJob() throws Exception;

    protected abstract void finishJob() throws Exception;

    protected String getJobName() {
        return jobName;
    }

    protected String getName() {
        return getSelf().path().name();
    }

    private Props createTaskWorkerProps(JobConfig config) throws ClassNotFoundException {
        return TaskWorker.props(Class.forName(config.getTaskWorkerClass()), config, getSelf());
    }

    private void processAssignTask(JobManagerMessage.TaskAssignRequest request) {
        jobManager.forward(request, getContext());
    }

    private void createTaskWorkers(int numberOfAssignedTaskWorkers) {
        for (int number = 0; number < numberOfAssignedTaskWorkers; number++) {
            createTaskWorker(number);
        }
    }

    private void createTaskWorker(int number) {
        String taskWorkerName = makeTaskWorkerName(number);

        try {
            ActorRef taskWorker = getContext().actorOf(createTaskWorkerProps(getConfig()), taskWorkerName);
            taskWorkers.put(taskWorkerName, taskWorker);
            getContext().watch(taskWorker);

            log().info("task-worker was created ({})", taskWorkerName);
        } catch (ClassNotFoundException e) {
            log().error(e, "failed to create the task-worker (class:{}, name:{})"
                , getConfig().getTaskWorkerClass(), taskWorkerName);
        }
    }

    private void stopAllWorkers() {
        for (ActorRef worker : taskWorkers.values()) {
            getContext().unwatch(worker);
            getContext().stop(worker);
        }

        taskWorkers.clear();
    }

    private void processRemoveTaskWorker(TaskManagerMessage.TaskWorkerFinishRequest request) {
        ActorRef worker = getSender();
        taskWorkers.remove(worker.path().name());
        getContext().unwatch(worker);
        getContext().stop(worker);

        if (taskWorkers.isEmpty()) {
            tellToMaster(new TaskMasterMessage.TaskManagerRemoveRequest());
        }
    }

    private String makeTaskWorkerName(int number) {
        return getJobName() + "-" + number;
    }

}
