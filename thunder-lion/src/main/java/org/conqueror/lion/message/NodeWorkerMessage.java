package org.conqueror.lion.message;

public abstract class NodeWorkerMessage implements LionMessage {

    public static abstract class NodeWorkerRequest extends NodeWorkerMessage {

    }

    public static abstract class NodeWorkerResponse extends NodeWorkerMessage {

    }

    public static final class NodeWorkerShutdownRequest extends NodeWorkerRequest {

    }

    public static final class NodeWorkerShutdownResponse extends NodeWorkerResponse {

    }

}
