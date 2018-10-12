package org.conqueror.lion.message;


import java.io.DataInput;
import java.io.DataOutput;


public abstract class NodeMasterMessage {

    public static abstract class NodeMasterRequest implements LionMessage {

    }

    public static abstract class NodeMasterResponse implements LionMessage {

    }

    public static final class NodeMasterShutdownRequest extends NodeMasterRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public NodeMasterShutdownRequest readObject(DataInput input) {
            return new NodeMasterShutdownRequest();
        }

    }

    public static final class NodeMasterShutdownResponse extends NodeMasterResponse {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public NodeMasterShutdownResponse readObject(DataInput input) {
            return new NodeMasterShutdownResponse();
        }

    }

}
