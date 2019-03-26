package org.conqueror.lion.message;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class JobManagerMessage extends JobMasterMessage {

    public static abstract class JobManagerRequest extends JobManagerMessage {

    }

    public static abstract class JobManagerResponse extends JobManagerMessage {

    }

    public static final class TaskManagerFinishRequest extends JobManagerRequest {

        public final String taskManagerName;

        public TaskManagerFinishRequest() {
            this(null);
        }

        public TaskManagerFinishRequest(String taskManagerName) {
            this.taskManagerName = taskManagerName;
        }

        public String getTaskManagerName() {
            return taskManagerName;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(taskManagerName);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public TaskManagerFinishRequest readObject(DataInput input) throws SerializableException {
            try {
                return new TaskManagerFinishRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskManagerFinishResponse extends JobManagerResponse {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public ThunderSerializable readObject(DataInput input) {
            return null;
        }

    }

    public static abstract class TaskAssignRequest extends JobManagerRequest {

    }

    public static abstract class TaskAssignResponse extends JobManagerResponse {

    }

    public static class TaskAssignWaitingResponse extends JobManagerMessage.TaskAssignResponse {

        private final int waitingSec;

        public TaskAssignWaitingResponse() {
            this(0);
        }

        public TaskAssignWaitingResponse(int waitingSec) {
            this.waitingSec = waitingSec;
        }

        public int getWaitingSec() {
            return waitingSec;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeInt(waitingSec);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public TaskAssignWaitingResponse readObject(DataInput input) throws SerializableException {
            try {
                return new TaskAssignWaitingResponse(input.readInt());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public String toString() {
            return "TaskAssignWaitingResponse{" +
                "waitingSec=" + waitingSec +
                '}';
        }

    }

    /*
    this message means that the task finished because no assigning task
     */
    public static final class TaskAssignFinishResponse extends TaskAssignResponse {

        private static final TaskAssignFinishResponse instance = new TaskAssignFinishResponse();

        public static TaskAssignFinishResponse getInstance() {
            return instance;
        }

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public ThunderSerializable readObject(DataInput input) {
            return new TaskAssignFinishResponse();
        }

    }


}
