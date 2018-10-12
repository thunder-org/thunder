package org.conqueror.lion.cluster.id;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.Cluster;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.id.IDMaker;
import org.conqueror.lion.message.IDIssuerMessage.*;
import org.conqueror.lion.message.IDIssuerMessage.IDIssuerResponse.Result;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.HashSet;
import java.util.Set;


public class IDIssuer extends AbstractLoggingActor {

    private final IDMaker idMaker;
    private final Set<String> issuedIDs = new HashSet<>();

    public static Props props(NodeConfig config) {
        return Props.create(IDIssuer.class, config);
    }

    public IDIssuer(NodeConfig config) {
        idMaker = new IDMaker(getSelf().path().name(), Cluster.get(getContext().getSystem()), config.getAskTimeout());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(IssuedIDRequest.class, this::processIssuedID)
            .match(NodeMasterIssueIDRequest.class, this::processIssueNodeMasterID)
            .match(NodeWorkerIssueIDRequest.class, this::processIssueNodeWorkerID)
            .match(NodeWorkerManagerIssueIDRequest.class, this::processIssueNodeWorkerManagerID)
            .match(ScheduleManagerIssueIDRequest.class, this::processIssueScheduleManagerID)
            .match(JobMasterIssueIDRequest.class, this::processIssueJobMasterID)
            .match(JobManagerIssueIDRequest.class, this::processIssueJobManagerID)
            .match(TaskMasterIssueIDRequest.class, this::processIssueTaskMasterID)
            .match(TaskManagerIssueIDRequest.class, this::processIssueTaskManagerID)
            .build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receive() {
        return super.receive();
    }

    private String issueID(Object request) {
        String id = null;
        if (request instanceof NodeMasterIssueIDRequest) {
            id = idMaker.makeNodeMasterID();

        } else if (request instanceof NodeWorkerManagerIssueIDRequest) {
            id = idMaker.makeNodeWorkerManagerID();

        } else if (request instanceof NodeWorkerIssueIDRequest) {
            id = idMaker.makeNodeWorkerID();

        } else if (request instanceof ScheduleManagerIssueIDRequest) {
            id = idMaker.makeScheduleManagerID();

        } else if (request instanceof JobMasterIssueIDRequest) {
            id = idMaker.makeJobMasterID();

        } else if (request instanceof JobManagerIssueIDRequest) {
            id = idMaker.makeJobManagerID(((JobManagerIssueIDRequest) request).getJobMasterID());

        } else if (request instanceof TaskMasterIssueIDRequest) {
            id = idMaker.makeTaskMasterID();

        } else if (request instanceof TaskManagerIssueIDRequest) {
            id = idMaker.makeTaskManagerID(((TaskManagerIssueIDRequest) request).getTaskMasterID());

        }
        return issueID(id);
    }

    private String issueID(String id) {
        return issuedIDs.add(id) ? id : null;
    }

    private boolean isIssuedID(String id) {
        return (id != null) && issuedIDs.contains(id);
    }

    private void processIssuedID(IssuedIDRequest request) {
        Result result = request.getID() != null ?
            Result.SUCCESS : Result.FAIL;
        getSender().tell(new IssuedIDResponse(result, isIssuedID(request.getID())), getSelf());
    }

    private void processIssueNodeMasterID(NodeMasterIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing node-master ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue node-master ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new NodeMasterIssueIDResponse(result, id), getSelf());
    }

    private void processIssueNodeWorkerID(NodeWorkerIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing node-worker ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue node-worker ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new NodeWorkerIssueIDResponse(result, id), getSelf());
    }

    private void processIssueNodeWorkerManagerID(NodeWorkerManagerIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing node-worker-manager ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue node-worker-manager ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new NodeWorkerManagerIssueIDResponse(result, id), getSelf());
    }

    private void processIssueScheduleManagerID(ScheduleManagerIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing schedule-manager ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue schedule-manager ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new ScheduleManagerIssueIDResponse(result, id), getSelf());
    }

    private void processIssueJobMasterID(JobMasterIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing job-master ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue job-master ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new JobMasterIssueIDResponse(result, id), getSelf());
    }

    private void processIssueJobManagerID(JobManagerIssueIDRequest request) {
        ActorRef nodeWorker = getSender();
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing job-manager ID ({}), sent to node-worker ({})"
                , getClass().getSimpleName(), id, nodeWorker);
        } else {
            log().info("[{}] : failed to issue job-manager ID, sent to node-worker ({})"
                , getClass().getSimpleName(), nodeWorker);
        }

        nodeWorker.tell(new JobManagerIssueIDResponse(result, id), getSelf());
    }

    private void processIssueTaskMasterID(TaskMasterIssueIDRequest request) {
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing task-master ID ({}), sent to ({})"
                , getClass().getSimpleName(), id, getSender());
        } else {
            log().info("[{}] : failed to issue task-master ID, sent to ({})"
                , getClass().getSimpleName(), getSender());
        }

        getSender().tell(new TaskMasterIssueIDResponse(result, id), getSelf());
    }

    private void processIssueTaskManagerID(TaskManagerIssueIDRequest request) {
        ActorRef nodeWorker = getSender();
        String id = issueID(request);
        Result result = (id != null) ?
            Result.SUCCESS : Result.FAIL;

        if (result == Result.SUCCESS) {
            log().info("[{}] : succeeded in issuing task-manager ID ({}), sent to node-worker ({})"
                , getClass().getSimpleName(), id, nodeWorker);
        } else {
            log().info("[{}] : failed to issue task-manager ID, sent to node-worker ({})"
                , getClass().getSimpleName(), nodeWorker);
        }

        nodeWorker.tell(new TaskManagerIssueIDResponse(result, id), getSelf());
    }

}
