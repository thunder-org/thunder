package org.conqueror.lion.message;

public abstract class IDIssuerMessage implements LionMessage {

    public static abstract class IDIssuerRequest extends IDIssuerMessage {

    }

    public static abstract class IDIssuerResponse extends IDIssuerMessage {

        public enum Result {SUCCESS, FAIL}

        private final Result result;
        private final String id;

        public IDIssuerResponse(Result result, String id) {
            this.result = result;
            this.id = id;
        }

        public Result getResult() {
            return result;
        }

        public String getId() {
            return id;
        }

    }

    public static final class NodeMasterIssueIDRequest extends IDIssuerRequest {

        @Override
        public String toString() {
            return "[REQUEST] issue node-master id";
        }

    }

    public static final class NodeMasterIssueIDResponse extends IDIssuerResponse {


        public NodeMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-master id (id:" + getId() + ")";
        }

    }

    public static final class NodeWorkerIssueIDRequest extends IDIssuerRequest {

        public NodeWorkerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue node-worker id";
        }

    }

    public static final class NodeWorkerIssueIDResponse extends IDIssuerResponse {

        public NodeWorkerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-worker id (id:" + getId() + ")";
        }

    }

    public static final class NodeWorkerManagerIssueIDRequest extends IDIssuerRequest {

        public NodeWorkerManagerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue node-worker-manager id";
        }

    }

    public static final class NodeWorkerManagerIssueIDResponse extends IDIssuerResponse {

        public NodeWorkerManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-worker-manager id (id:" + getId() + ")";
        }

    }

    public static final class ScheduleManagerIssueIDRequest extends IDIssuerRequest {

        public ScheduleManagerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue schedule-manager id";
        }

    }

    public static final class ScheduleManagerIssueIDResponse extends IDIssuerResponse {

        public ScheduleManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue schedule-manager id (id:" + getId() + ")";
        }

    }

    public static final class JobMasterIssueIDRequest extends IDIssuerRequest {

        public JobMasterIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue job-master id";
        }

    }

    public static final class JobMasterIssueIDResponse extends IDIssuerResponse {

        public JobMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue job-master id (id:" + getId() + ")";
        }

    }

    public static final class JobManagerIssueIDRequest extends IDIssuerRequest {

        private final String jobMasterID;

        public JobManagerIssueIDRequest(String jobMasterID) {
            this.jobMasterID = jobMasterID;
        }

        public String getJobMasterID() {
            return jobMasterID;
        }

        @Override
        public String toString() {
            return "[REQUEST] issue job-manager id";
        }

    }

    public static final class JobManagerIssueIDResponse extends IDIssuerResponse {

        public JobManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue job-manager id (id:" + getId() + ")";
        }

    }

    public static final class TaskMasterIssueIDRequest extends IDIssuerRequest {

        public TaskMasterIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue task-master id";
        }

    }

    public static final class TaskMasterIssueIDResponse extends IDIssuerResponse {

        public TaskMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-master id (id:" + getId() + ")";
        }

    }

    public static final class TaskManagerIssueIDRequest extends IDIssuerRequest {

        private final String taskMasterID;

        public TaskManagerIssueIDRequest(String taskMasterID) {
            this.taskMasterID = taskMasterID;
        }

        public String getTaskMasterID() {
            return taskMasterID;
        }

        @Override
        public String toString() {
            return "[REQUEST] issue task-manager id";
        }

    }

    public static final class TaskManagerIssueIDResponse extends IDIssuerResponse {

        public TaskManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-manager id (id:" + getId() + ")";
        }

    }

    public static final class TaskWorkerIssueIDRequest extends IDIssuerRequest {

        private final String taskManagerID;

        public TaskWorkerIssueIDRequest(String taskManagerID) {
            this.taskManagerID = taskManagerID;
        }

        public String getTaskManagerID() {
            return taskManagerID;
        }

        @Override
        public String toString() {
            return "[REQUEST] issue task-worker id";
        }

    }

    public static final class TaskWorkerIssueIDResponse extends IDIssuerResponse {

        public TaskWorkerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-worker id (id:" + getId() + ")";
        }

    }

    public static final class IssuedIDRequest extends IDIssuerRequest {

        private String id;

        public IssuedIDRequest(String id) {
            this.id = id;
        }

        public String getID() {
            return id;
        }

        @Override
        public String toString() {
            return "[REQUEST] issued id (" + id + ")";
        }

    }

    public static final class IssuedIDResponse extends IDIssuerResponse {

        private final boolean isIssuedID;

        public IssuedIDResponse(Result result, boolean isIssuedID) {
            super(result, null);
            this.isIssuedID = isIssuedID;
        }

        public boolean isIssuedID() {
            return isIssuedID;
        }

        @Override
        public String toString() {
            return "[RESPONSE] issued id (" + isIssuedID() + ")";
        }

    }

}
