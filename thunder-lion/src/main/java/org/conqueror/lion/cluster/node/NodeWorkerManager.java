package org.conqueror.lion.cluster.node;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.NodeWorkerManagerMessage.NodeWorkerManagerResponse;
import org.conqueror.lion.message.NodeWorkerManagerMessage.NodeWorkerRegisterRequest;
import org.conqueror.lion.message.NodeWorkerManagerMessage.NodeWorkerRegisterResponse;
import org.conqueror.lion.message.NodeWorkerManagerMessage.NodeWorkerUnregisterRequest;
import org.conqueror.lion.message.TaskMasterMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NodeWorkerManager extends NodeComponentActor {

    private final Map<String, ActorRef> registeredNodeWorkers = new HashMap<>();

    public NodeWorkerManager(NodeConfig config, ActorRef nodeMaster) {
        super(config, nodeMaster);
    }

    @Override
    protected Receive buildReadyReceive() {
        return super.buildReadyReceive().orElse(
            receiveBuilder()
                .match(NodeWorkerRegisterRequest.class, request -> getSender().tell(new NodeWorkerRegisterResponse(NodeWorkerManagerResponse.Result.FAIL), getSelf()))
                .build()
        );
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            .match(NodeWorkerRegisterRequest.class, this::processRegisterNodeWorker)
            .match(NodeWorkerUnregisterRequest.class, this::processUnregisterNodeWorker)
            .match(TaskMasterMessage.TaskMasterAssignRequest.class, this::processAssignTaskMasters)
            .match(Terminated.class, this::processUnregisterNodeWorker)
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build();
    }

    @Override
    protected void postWorking() {

    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.NodeWorkerManagerIssueIDRequest();
    }

    public Set<String> getRegisteredNodeWorkerIDs() {
        return registeredNodeWorkers.keySet();
    }

    private void processRegisterNodeWorker(NodeWorkerRegisterRequest request) {
        String nodeWorkerID = request.getNodeWorkerID();
        ActorRef nodeWorker = registeredNodeWorkers.get(nodeWorkerID);
        if (nodeWorker != null) {
            if (nodeWorker.equals(getSender())) {
                nodeWorker = null;
            } else {
                getContext().unwatch(nodeWorker);
                nodeWorker = getSender();
            }
        } else {
            nodeWorker = getSender();
        }

        if (nodeWorker != null) {
            getContext().watch(nodeWorker);
            registeredNodeWorkers.put(nodeWorkerID, getSender());
            nodeWorker.tell(new NodeWorkerRegisterResponse(NodeWorkerManagerResponse.Result.SUCCESS), getSelf());

            log().info("node-worker is registered - {}, {}", nodeWorkerID, nodeWorker);
        } else {
            log().info("node-worker is already registered - {}, {}", nodeWorkerID, nodeWorker);
        }
    }

    /*
     * node-worker가 stop 되었을 경우 unregister 요청을 받았을 경우 node-worker 리스트에서 제거
     */
    private void processUnregisterNodeWorker(NodeWorkerUnregisterRequest request) {
        getContext().unwatch(getSender());
        registeredNodeWorkers.remove(request.getNodeWorkerID());
    }

    /*
     * node-worker가 stop 되었을 경우 Terminated 메시지를 받았을 경우 node-worker 리스트에서 제거
     */
    private void processUnregisterNodeWorker(Terminated request) {
        for (Map.Entry<String, ActorRef> entry : registeredNodeWorkers.entrySet()) {
            if (entry.getValue().equals(request.getActor())) {
                getContext().unwatch(getSender());
                registeredNodeWorkers.remove(entry.getKey());
                break;
            }
        }
    }

    private void processAssignTaskMasters(TaskMasterMessage.TaskMasterAssignRequest request) {
        for (ActorRef nodeWorker : registeredNodeWorkers.values()) {
            nodeWorker.tell(request, getSender());
        }
    }

}
