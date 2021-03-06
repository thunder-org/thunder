package org.conqueror.lion.message;


import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class NodeWorkerManagerMessage implements ThunderMessage {

    public static abstract class NodeWorkerManagerRequest extends NodeWorkerManagerMessage {

    }

    public static abstract class NodeWorkerManagerResponse extends NodeWorkerManagerMessage {

        public enum Result {SUCCESS, FAIL}

        private final Result result;

        public NodeWorkerManagerResponse() {
            this(null);
        }

        public NodeWorkerManagerResponse(Result result) {
            this.result = result;
        }

        public Result getResult() {
            return result;
        }

        public boolean isSucceeded() {
            return getResult().equals(Result.SUCCESS);
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(result.name());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class NodeWorkerRegisterRequest extends NodeWorkerManagerRequest {

        private final String nodeWorkerID;

        public NodeWorkerRegisterRequest() {
            this(null);
        }

        public NodeWorkerRegisterRequest(String nodeWorkerID) {
            this.nodeWorkerID = nodeWorkerID;
        }

        public String getNodeWorkerID() {
            return nodeWorkerID;
        }

        @Override
        public String toString() {
            return "[REQUEST] register node-worker (id:" + nodeWorkerID + ")";
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(nodeWorkerID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public NodeWorkerRegisterRequest readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerRegisterRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class NodeWorkerRegisterResponse extends NodeWorkerManagerResponse {

        public NodeWorkerRegisterResponse() {
            this(null);
        }

        public NodeWorkerRegisterResponse(Result result) {
            super(result);
        }

        @Override
        public String toString() {
            return "[RESPONSE] register node-worker id";
        }

        @Override
        public NodeWorkerRegisterResponse readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerRegisterResponse(Result.valueOf(input.readUTF()));
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }
    }

    public static final class NodeWorkerUnregisterRequest extends NodeWorkerManagerRequest {

        private final String nodeWorkerID;

        public NodeWorkerUnregisterRequest() {
            this(null);
        }

        public NodeWorkerUnregisterRequest(String nodeWorkerID) {
            this.nodeWorkerID = nodeWorkerID;
        }

        public String getNodeWorkerID() {
            return nodeWorkerID;
        }

        @Override
        public String toString() {
            return "[REQUEST] unregister node-worker (id:" + nodeWorkerID + ")";
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(nodeWorkerID);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public NodeWorkerUnregisterRequest readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerUnregisterRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }
    }

    public static final class NodeWorkerUnregisterResponse extends NodeWorkerManagerResponse {

        public NodeWorkerUnregisterResponse() {
            this(null);
        }

        public NodeWorkerUnregisterResponse(Result result) {
            super(result);
        }

        @Override
        public String toString() {
            return "[RESPONSE] unregister node-worker id";
        }

        @Override
        public ThunderSerializable readObject(DataInput input) throws SerializableException {
            try {
                return new NodeWorkerUnregisterResponse(Result.valueOf(input.readUTF()));
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }
    }

}
