package org.conqueror.lion.cluster.node;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.NodeActor;
import org.conqueror.lion.cluster.id.IDIssuer;
import org.conqueror.lion.cluster.job.JobMaster;
import org.conqueror.lion.cluster.schedule.ScheduleManager;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.*;

import static org.conqueror.lion.cluster.node.Path.*;


public class NodeMaster extends NodeActor {

    /*
     * child modules of node-master
     */
    private final ActorRef idIssuer;
    private ActorRef nodeWorkerManager;
    private ActorRef scheduleManager;
    private ActorRef jobMaster;

    public static Props props(NodeConfig config) {
        return Props.create(NodeMaster.class, config);
    }

    public NodeMaster(NodeConfig config) {
        super(config);

        idIssuer = createChildActor(IDIssuer.props(config), ID_ISSUER_NAME);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            .match(NodeMasterMessage.NodeMasterShutdownRequest.class, this::processShutdown)

            // id-issuer
            .match(IDIssuerMessage.IDIssuerRequest.class, this::processIssueID)

            // node-worker
            .match(NodeWorkerManagerMessage.NodeWorkerManagerRequest.class, this::processNodeWorker)
            .match(TaskMasterMessage.TaskMasterAssignRequest.class, this::processTaskMaster)

            // scheduleManager
            .match(ScheduleManagerMessage.ScheduleManagerRequest.class, this::processScheduling)

            // job-master
            .match(JobMasterMessage.JobMasterRequest.class, this::processJob)
            .matchAny(this::unhandled)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        log().info("[NODE-MASTER] started");
    }

    @Override
    protected void postWorking() {
        nodeWorkerManager = createComponentActor(NodeWorkerManager.class, NODE_WORKER_MANAGER_NAME);
//        scheduleManager = createComponentActor(ScheduleManager.class, SCHEDULER_MANAGER_NAME);
        jobMaster = createComponentActor(JobMaster.class, JOB_MASTER_NAME);
    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.NodeMasterIssueIDRequest();
    }

    @Override
    protected void tellToIDIssuer(IDIssuerMessage.IDIssuerRequest request) {
        idIssuer.tell(request, getSelf());
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        log().info("[NODE-MASTER] stopped");
    }

    /*
     * shutdown
     */
    private void processShutdown(NodeMasterMessage.NodeMasterShutdownRequest request) {
        getSender().tell(new NodeMasterMessage.NodeMasterShutdownResponse(), getSelf());
    }

    /*
     * issue id
     */
    private void processIssueID(IDIssuerMessage.IDIssuerRequest request) {
        idIssuer.forward(request, getContext());
    }

    /*
     * node-worker
     */
    private void processNodeWorker(NodeWorkerManagerMessage.NodeWorkerManagerRequest request) {
        nodeWorkerManager.forward(request, getContext());
    }

    /*
     * node-worker - task-master
     */
    private void processTaskMaster(TaskMasterMessage.TaskMasterAssignRequest request) {
        nodeWorkerManager.forward(request, getContext());
    }

    /*
     * schedule
     */
    private void processScheduling(ScheduleManagerMessage.ScheduleManagerRequest request) {
        scheduleManager.forward(request, getContext());
    }

    /*
     * job
     */
    private void processJob(JobMasterMessage.JobMasterRequest request) {
        jobMaster.forward(request, getContext());
    }


}
