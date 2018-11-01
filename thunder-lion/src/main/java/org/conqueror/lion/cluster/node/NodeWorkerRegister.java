package org.conqueror.lion.cluster.node;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import org.conqueror.lion.message.LionMessage;
import org.conqueror.lion.message.NodeWorkerManagerMessage;
import org.conqueror.lion.message.NodeWorkerMessage;

import java.time.Duration;


public class NodeWorkerRegister extends AbstractLoggingActor {

    private final ActorRef nodeWorker;
    private final ActorRef nodeMasterProxy;
    private final String nodeWorkerID;

    private Cancellable nodeRegisterScheduler;

    public static Props props(ActorRef nodeWorker, ActorRef nodeMasterProxy, String nodeWorkerID) {
        return Props.create(NodeWorkerRegister.class, nodeWorker, nodeMasterProxy, nodeWorkerID);
    }

    public NodeWorkerRegister(ActorRef nodeWorker, ActorRef nodeMasterProxy, String nodeWorkerID) {
        this.nodeWorker = nodeWorker;
        this.nodeMasterProxy = nodeMasterProxy;
        this.nodeWorkerID = nodeWorkerID;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(NodeWorkerManagerMessage.NodeWorkerRegisterResponse.class, this::processRegister)
            .match(NodeWorkerMessage.NodeWorkerReregisterRequest.class, this::processReregister)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        createScheduler();
    }

    private String getNodeWorkerId() {
        return nodeWorkerID;
    }

    private void processRegister(NodeWorkerManagerMessage.NodeWorkerRegisterResponse response) {
        if (response.isSucceeded()) {
            nodeRegisterScheduler.cancel();
            log().info("registered to node-master");

            tellToNode(NodeWorkerMessage.NodeWorkerRegisteredRequest.getInstance());
        }
    }

    private void processReregister(NodeWorkerMessage.NodeWorkerReregisterRequest request) {
        createScheduler();
    }

    private void createScheduler() {
        nodeRegisterScheduler = getContext().getSystem().getScheduler().schedule(Duration.ZERO, Duration.ofSeconds(1)
            , nodeMasterProxy
            , new NodeWorkerManagerMessage.NodeWorkerRegisterRequest(getNodeWorkerId())
            , getContext().getSystem().dispatcher()
            , nodeWorker);
    }

    protected void tellToNode(LionMessage message) {
        nodeWorker.tell(message, getSelf());
    }

}
