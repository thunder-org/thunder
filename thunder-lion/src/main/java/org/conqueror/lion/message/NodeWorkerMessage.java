package org.conqueror.lion.message;

import java.io.DataInput;
import java.io.DataOutput;


public abstract class NodeWorkerMessage implements LionMessage {

    public static abstract class NodeWorkerRequest extends NodeWorkerMessage {

    }

    public static abstract class NodeWorkerResponse extends NodeWorkerMessage {

    }

    public static final class NodeWorkerReregisterRequest extends NodeWorkerRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public NodeWorkerReregisterRequest readObject(DataInput input) {
            return new NodeWorkerReregisterRequest();
        }

    }

    public static final class NodeWorkerShutdownRequest extends NodeWorkerRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public NodeWorkerShutdownRequest readObject(DataInput input) {
            return new NodeWorkerShutdownRequest();
        }

    }

    public static final class NodeWorkerShutdownResponse extends NodeWorkerResponse {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public NodeWorkerShutdownResponse readObject(DataInput input) {
            return new NodeWorkerShutdownResponse();
        }

    }

}
