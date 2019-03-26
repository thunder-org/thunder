package org.conqueror.lion.cluster.node;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.conqueror.lion.cluster.actor.NodeActor;
import org.conqueror.lion.cluster.api.rest.HttpService;
import org.conqueror.lion.cluster.api.rest.router.message.MessageServiceRouter;
import org.conqueror.lion.cluster.id.IDIssuer;
import org.conqueror.lion.cluster.job.JobMaster;
import org.conqueror.lion.cluster.schedule.ScheduleManager;
import org.conqueror.lion.config.HttpServiceConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.*;

import java.lang.reflect.InvocationTargetException;

import static org.conqueror.lion.cluster.node.Path.*;
import static org.conqueror.lion.cluster.node.PubSubTopic.NODE_WORKER_TOPIC;


public class NodeMaster extends NodeActor {

    /*
     * child modules of node-master
     */
    private final ActorRef idIssuer;
    private ActorRef nodeWorkerManager;
    private ActorRef scheduleManager;
    private ActorRef jobMaster;
    private HttpService httpService;

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
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
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
        scheduleManager = createComponentActor(ScheduleManager.class, SCHEDULER_MANAGER_NAME);
        jobMaster = createComponentActor(JobMaster.class, JOB_MASTER_NAME);
        httpService = createHttpService();

        publish(NODE_WORKER_TOPIC, new NodeWorkerMessage.NodeWorkerReregisterRequest());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        super.postRestart(reason);
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
        if (httpService != null) httpService.close();
        super.postStop();

        log().info("[NODE-MASTER] stopped");
    }

    private HttpService createHttpService() {
        HttpServiceConfig config = new HttpServiceConfig(getConfig().getConfig());
        httpService = new HttpService(getSystem(), getSelf(), config);
        String apiServiceClass = config.getApiServiceClass();
        if (apiServiceClass != null) {
            try {
                httpService.open((MessageServiceRouter) Class.forName(apiServiceClass)
                    .getConstructor(ActorSystem.class, ActorRef.class, NodeConfig.class, long.class)
                    .newInstance(getSystem(), getSelf(), getConfig(), config.getMessageTimeout()));
                return httpService;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                log().error("http service class not found : {}", apiServiceClass);
            }
        }
        return null;
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
        log().info("node-worker-manager request : {}", request);
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
