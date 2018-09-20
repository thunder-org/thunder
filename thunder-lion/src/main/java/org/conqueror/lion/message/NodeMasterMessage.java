package org.conqueror.lion.message;


public abstract class NodeMasterMessage {

    public static abstract class NodeMasterRequest implements LionMessage {

    }

    public static abstract class NodeMasterResponse implements LionMessage {

    }

    public static final class NodeMasterShutdownRequest extends NodeMasterRequest {

    }

    public static final class NodeMasterShutdownResponse extends NodeMasterResponse {

    }

}
