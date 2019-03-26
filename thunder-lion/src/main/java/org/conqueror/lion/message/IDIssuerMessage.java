package org.conqueror.lion.message;

import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class IDIssuerMessage implements ThunderMessage {

    public static abstract class IDIssuerRequest extends IDIssuerMessage {

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

    }

    public static abstract class IDIssuerResponse extends IDIssuerMessage {

        public enum Result {SUCCESS, FAIL}

        private final Result result;
        private final String id;

        public IDIssuerResponse() {
            this(null, null);
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(result.name());
                output.writeUTF(id);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class NodeMasterIssueIDRequest extends IDIssuerRequest {

        @Override
        public String toString() {
            return "[REQUEST] issue node-master id";
        }

        @Override
        public NodeMasterIssueIDRequest readObject(DataInput input) {
            return new NodeMasterIssueIDRequest();
        }

    }

    public static final class NodeMasterIssueIDResponse extends IDIssuerResponse {

        public NodeMasterIssueIDResponse() {
            super();
        }

        public NodeMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-master id (id:" + getId() + ")";
        }

        @Override
        public NodeMasterIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new NodeMasterIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class NodeWorkerIssueIDRequest extends IDIssuerRequest {

        public NodeWorkerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue node-worker id";
        }

        @Override
        public NodeWorkerIssueIDRequest readObject(DataInput input) {
            return new NodeWorkerIssueIDRequest();
        }

    }

    public static final class NodeWorkerIssueIDResponse extends IDIssuerResponse {

        public NodeWorkerIssueIDResponse() {
            super();
        }

        public NodeWorkerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-worker id (id:" + getId() + ")";
        }

        @Override
        public NodeWorkerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class NodeWorkerManagerIssueIDRequest extends IDIssuerRequest {

        public NodeWorkerManagerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue node-worker-manager id";
        }

        @Override
        public NodeWorkerManagerIssueIDRequest readObject(DataInput input) {
            return new NodeWorkerManagerIssueIDRequest();
        }

    }

    public static final class NodeWorkerManagerIssueIDResponse extends IDIssuerResponse {

        public NodeWorkerManagerIssueIDResponse() {
            super();
        }

        public NodeWorkerManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue node-worker-manager id (id:" + getId() + ")";
        }

        @Override
        public NodeWorkerManagerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerManagerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class ScheduleManagerIssueIDRequest extends IDIssuerRequest {

        public ScheduleManagerIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue schedule-manager id";
        }

        @Override
        public ScheduleManagerIssueIDRequest readObject(DataInput input) {
            return new ScheduleManagerIssueIDRequest();
        }

    }

    public static final class ScheduleManagerIssueIDResponse extends IDIssuerResponse {

        public ScheduleManagerIssueIDResponse() {
            super();
        }

        public ScheduleManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue schedule-manager id (id:" + getId() + ")";
        }

        @Override
        public ScheduleManagerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new ScheduleManagerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class JobMasterIssueIDRequest extends IDIssuerRequest {

        public JobMasterIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue job-master id";
        }

        @Override
        public JobMasterIssueIDRequest readObject(DataInput input) {
            return new JobMasterIssueIDRequest();
        }

    }

    public static final class JobMasterIssueIDResponse extends IDIssuerResponse {

        public JobMasterIssueIDResponse() {
            super();
        }

        public JobMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue job-master id (id:" + getId() + ")";
        }

        @Override
        public JobMasterIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new JobMasterIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class JobManagerIssueIDRequest extends IDIssuerRequest {

        private final String jobMasterID;

        public JobManagerIssueIDRequest() {
            this.jobMasterID = null;
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(jobMasterID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public JobManagerIssueIDRequest readObject(DataInput input) throws SerializableException {
            try {
                return new JobManagerIssueIDRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class JobManagerIssueIDResponse extends IDIssuerResponse {

        public JobManagerIssueIDResponse() {
            super();
        }

        public JobManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue job-manager id (id:" + getId() + ")";
        }

        @Override
        public JobManagerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new JobManagerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskMasterIssueIDRequest extends IDIssuerRequest {

        public TaskMasterIssueIDRequest() {
        }

        @Override
        public String toString() {
            return "[REQUEST] issue task-master id";
        }

        @Override
        public TaskMasterIssueIDRequest readObject(DataInput input) {
            return new TaskMasterIssueIDRequest();
        }

    }

    public static final class TaskMasterIssueIDResponse extends IDIssuerResponse {

        public TaskMasterIssueIDResponse() {
            super();
        }

        public TaskMasterIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-master id (id:" + getId() + ")";
        }

        @Override
        public TaskMasterIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new TaskMasterIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskManagerIssueIDRequest extends IDIssuerRequest {

        private final String taskMasterID;

        public TaskManagerIssueIDRequest() {
            this.taskMasterID = null;
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(taskMasterID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public TaskManagerIssueIDRequest readObject(DataInput input) throws SerializableException {
            try {
                return new TaskManagerIssueIDRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskManagerIssueIDResponse extends IDIssuerResponse {

        public TaskManagerIssueIDResponse() {
            super();
        }

        public TaskManagerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-manager id (id:" + getId() + ")";
        }

        @Override
        public TaskManagerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new TaskManagerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskWorkerIssueIDRequest extends IDIssuerRequest {

        private final String taskManagerID;

        public TaskWorkerIssueIDRequest() {
            this.taskManagerID = null;
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(taskManagerID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public TaskWorkerIssueIDRequest readObject(DataInput input) throws SerializableException {
            try {
                return new TaskWorkerIssueIDRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskWorkerIssueIDResponse extends IDIssuerResponse {

        public TaskWorkerIssueIDResponse() {
            super();
        }

        public TaskWorkerIssueIDResponse(Result result, String id) {
            super(result, id);
        }

        @Override
        public String toString() {
            return "[RESPONSE] issue task-worker id (id:" + getId() + ")";
        }

        @Override
        public TaskWorkerIssueIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new TaskWorkerIssueIDResponse(Result.valueOf(input.readUTF()), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class IssuedIDRequest extends IDIssuerRequest {

        private String id;

        public IssuedIDRequest() {
            this.id = null;
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(id);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public IssuedIDRequest readObject(DataInput input) throws SerializableException {
            try {
                return new IssuedIDRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class IssuedIDResponse extends IDIssuerResponse {

        private final boolean isIssuedID;

        public IssuedIDResponse() {
            super();
            this.isIssuedID = false;
        }

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

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(getResult().name());
                output.writeBoolean(isIssuedID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public IssuedIDResponse readObject(DataInput input) throws SerializableException {
            try {
                return new IssuedIDResponse(Result.valueOf(input.readUTF()), input.readBoolean());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

}
