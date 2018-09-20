package org.conqueror.lion.message;

import akka.actor.ActorRef;
import org.conqueror.lion.config.JobConfig;


public abstract class TaskMasterMessage implements LionMessage {


    public static abstract class TaskMasterRequest extends TaskMasterMessage {

    }

    public static abstract class TaskMasterResponse extends TaskMasterMessage {

    }

    public static final class TaskMasterAssignRequest extends TaskMasterRequest {

    }

    public static final class TaskMasterAssignResponse extends TaskMasterResponse {

        private final String id;
        private final ActorRef taskMaster;

        public TaskMasterAssignResponse(String id, ActorRef taskMaster) {
            this.id = id;
            this.taskMaster = taskMaster;
        }

        public String getId() {
            return id;
        }

        public ActorRef getTaskMaster() {
            return taskMaster;
        }

    }

    public static final class TaskManagerCreateRequest extends TaskMasterRequest {

        private final String jobName;

        private final JobConfig config;

        public TaskManagerCreateRequest(String jobName, JobConfig config) {
            this.jobName = jobName;
            this.config = config;
        }

        public String getJobName() {
            return jobName;
        }

        public JobConfig getConfig() {
            return config;
        }

    }

    public static final class TaskManagerCreateResponse extends TaskMasterResponse {

        private final String taskManagerName;
        private final ActorRef taskManager;

        public TaskManagerCreateResponse(String taskManagerName, ActorRef taskManager) {
            this.taskManagerName = taskManagerName;
            this.taskManager = taskManager;
        }

        public String getTaskManagerName() {
            return taskManagerName;
        }

        public ActorRef getTaskManager() {
            return taskManager;
        }

    }

    public static final class TaskManagerRemoveRequest extends TaskMasterRequest {

    }

}
