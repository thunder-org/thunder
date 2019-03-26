package org.conqueror.lion.cluster.node;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import org.conqueror.lion.cluster.actor.NodeActor;
import org.conqueror.lion.cluster.task.TaskMaster;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.*;

import static org.conqueror.lion.cluster.node.Path.*;
import static org.conqueror.lion.cluster.node.PubSubTopic.NODE_WORKER_TOPIC;


public class NodeWorker extends NodeActor {

    /*
     * child modules of node-worker
     */
    private ActorRef taskMaster;

    private ActorRef nodeWorkerRegister;

    /*
     * proxy of node-master for communicating to node-master
     */
    private ActorRef nodeMasterProxy = createChildActor(
        ClusterSingletonProxy.props(NODE_MASTER_PATH
            , ClusterSingletonProxySettings.create(getContext().system()))
        , NODE_MASTER_PROXY);

    public static Props props(NodeConfig config) {
        return Props.create(NodeWorker.class, config);
    }

    public NodeWorker(NodeConfig config) {
        super(config);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            .match(NodeWorkerManagerMessage.NodeWorkerRegisterResponse.class, this::processRegister)
            .match(NodeWorkerManagerMessage.NodeWorkerUnregisterRequest.class, this::processUnregister)
            .match(NodeWorkerMessage.NodeWorkerReregisterRequest.class, this::processReregister)
            .match(NodeWorkerMessage.NodeWorkerRegisteredRequest.class, this::processRegistered)
            .match(IDIssuerMessage.IDIssuerRequest.class, this::processIssueID)
            .match(TaskMasterMessage.TaskMasterAssignRequest.class, this::processAssignTaskMaster)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        subscribe(NODE_WORKER_TOPIC);
    }

    @Override
    protected void postWorking() {
        log().info("[NODE-WORKER] started - {}", getId());

        nodeWorkerRegister = createChildActor(NodeWorkerRegister.props(getSelf(), nodeMasterProxy, getId()), "node-worker-register");
    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.NodeWorkerIssueIDRequest();
    }

    @Override
    protected void tellToIDIssuer(IDIssuerMessage.IDIssuerRequest request) {
        tellToMaster(new IDIssuerMessage.NodeWorkerIssueIDRequest());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        unsubscribe(NODE_WORKER_TOPIC);

        log().info("[NODE-WORKER] stopped");
    }

    private void processRegister(NodeWorkerManagerMessage.NodeWorkerRegisterResponse response) {
        nodeWorkerRegister.forward(response, getContext());
    }

    private void processUnregister(NodeWorkerManagerMessage.NodeWorkerUnregisterRequest response) {
        nodeWorkerRegister.forward(response, getContext());
    }

    private void processRegistered(NodeWorkerMessage.NodeWorkerRegisteredRequest request) {
        if (!getContext().findChild(TASK_MASTER_NAME).isPresent()) {
            taskMaster = createComponentActor(TaskMaster.class, TASK_MASTER_NAME);
        }
    }

    /*
        request for re-register, node-master changed
     */
    private void processReregister(NodeWorkerMessage.NodeWorkerReregisterRequest request) {
        log().info("re-register");
        taskMaster.tell(new TaskMasterMessage.TaskManagerRemoveAllRequest(), getSelf());

        nodeWorkerRegister.tell(request, getSelf());
    }

    private void processIssueID(IDIssuerMessage.IDIssuerRequest request) {
        forwardToMaster(request);
    }

    private void processAssignTaskMaster(TaskMasterMessage.TaskMasterAssignRequest request) {
        taskMaster.forward(request, getContext());
    }

    /*
     * shutdown
     */
    private void processShutdown(NodeWorkerMessage.NodeWorkerShutdownRequest request) {
        getSender().tell(new NodeWorkerMessage.NodeWorkerShutdownResponse(), getSelf());
        getContext().stop(getSelf());
    }

    protected void tellToMaster(ThunderMessage message) {
        nodeMasterProxy.tell(message, getSelf());
    }

    protected void forwardToMaster(ThunderMessage message) {
        nodeMasterProxy.forward(message, getContext());
    }

}
