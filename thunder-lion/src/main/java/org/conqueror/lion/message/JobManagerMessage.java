package org.conqueror.lion.message;

public abstract class JobManagerMessage extends JobMasterMessage {

    public static abstract class JobManagerRequest extends JobManagerMessage {

    }

    public static abstract class JobManagerResponse extends JobManagerMessage {

    }

    public static final class TaskManagerFinishRequest extends JobManagerRequest {

        public final String taskManagerName;

        public TaskManagerFinishRequest(String taskManagerName) {
            this.taskManagerName = taskManagerName;
        }

        public String getTaskManagerName() {
            return taskManagerName;
        }

    }

    public static final class TaskManagerFinishResponse extends JobManagerResponse {

    }

    public static abstract class TaskAssignRequest extends JobManagerRequest {

    }

    public static abstract class TaskAssignResponse extends JobManagerResponse {

    }

    /*
    this message means that the task finished because no assigning task
     */
    public static final class TaskAssignFinishResponse extends TaskAssignResponse {

    }


}
