package org.conqueror.lion.message;


import akka.actor.ActorRef;

import java.util.Map;


public abstract class NodeWorkerManagerMessage implements LionMessage {

    public static abstract class NodeWorkerManagerRequest extends NodeWorkerManagerMessage {

    }

    public static abstract class NodeWorkerManagerResponse extends NodeWorkerManagerMessage {

        public enum Result {SUCCESS, FAIL}

        private final Result result;

        public NodeWorkerManagerResponse(Result result) {
            this.result = result;
        }

        public Result getResult() {
            return result;
        }

        public boolean isSucceeded() {
            return getResult().equals(Result.SUCCESS);
        }

    }

    public static final class NodeWorkerRegisterRequest extends NodeWorkerManagerRequest {

        private final String nodeWorkerID;

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

    }

    public static final class NodeWorkerRegisterResponse extends NodeWorkerManagerResponse {

        public NodeWorkerRegisterResponse(Result result) {
            super(result);
        }

        @Override
        public String toString() {
            return "[RESPONSE] register node-worker id";
        }

    }

    public static final class NodeWorkerUnregisterRequest extends NodeWorkerManagerRequest {

        private final String nodeWorkerID;

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

    }

    public static final class NodeWorkerUnregisterResponse extends NodeWorkerManagerResponse {

        public NodeWorkerUnregisterResponse(Result result) {
            super(result);
        }

        @Override
        public String toString() {
            return "[RESPONSE] unregister node-worker id";
        }

    }

}
