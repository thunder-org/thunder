package org.conqueror.lion.message;

import java.io.DataInput;
import java.io.DataOutput;


public abstract class TaskManagerMessage implements LionMessage {

    public static abstract class TaskManagerRequest extends TaskManagerMessage {

    }

    public static abstract class TaskManagerResponse extends TaskManagerMessage {

    }

    public static final class TaskWorkerFinishRequest extends TaskManagerRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskWorkerFinishRequest readObject(DataInput input) {
            return new TaskWorkerFinishRequest();
        }

    }

}
