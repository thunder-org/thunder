package org.conqueror.lion.message;

import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;


public abstract class TaskManagerMessage implements LionMessage {

    public static abstract class TaskManagerRequest extends TaskManagerMessage {

    }

    public static abstract class TaskManagerResponse extends TaskManagerMessage {

    }

    public static final class TaskWorkerFinishRequest extends TaskManagerRequest {

        private static final TaskWorkerFinishRequest instance = new TaskWorkerFinishRequest();

        public static TaskWorkerFinishRequest getInstance() {
            return instance;
        }

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskWorkerFinishRequest readObject(DataInput input) {
            return new TaskWorkerFinishRequest();
        }

    }

    public static final class TransferTaskManagerFinishRequest extends TaskManagerRequest {

        public TransferTaskManagerFinishRequest() {
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
        }

        @Override
        public TransferTaskManagerFinishRequest readObject(DataInput input) throws SerializableException {
            return new TransferTaskManagerFinishRequest();
        }

    }

    public static final class TransferTaskWorkerAssignRequest extends TaskManagerRequest {

        private static final TaskWorkerFinishRequest instance = new TaskWorkerFinishRequest();

        public static TaskWorkerFinishRequest getInstance() {
            return instance;
        }

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskWorkerFinishRequest readObject(DataInput input) {
            return new TaskWorkerFinishRequest();
        }

    }

    public static final class TransferTaskWorkerFinishCheckRequest extends TaskManagerRequest {

        private static final TransferTaskWorkerFinishCheckRequest instance = new TransferTaskWorkerFinishCheckRequest();

        public static TransferTaskWorkerFinishCheckRequest getInstance() {
            return instance;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public TransferTaskWorkerFinishCheckRequest readObject(DataInput input) throws SerializableException {
            return new TransferTaskWorkerFinishCheckRequest();
        }

    }

    public static final class TransferTaskWorkerFinishCheckResponse extends TaskManagerResponse {

        private static final TransferTaskWorkerFinishCheckResponse instance = new TransferTaskWorkerFinishCheckResponse();

        public static TransferTaskWorkerFinishCheckResponse getInstance() {
            return instance;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public TransferTaskWorkerFinishCheckResponse readObject(DataInput input) throws SerializableException {
            return new TransferTaskWorkerFinishCheckResponse();
        }

    }

}
