package org.conqueror.bird.task;

import akka.actor.ActorRef;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.analysis.AnalysisTaskFinishRequest;
import org.conqueror.bird.data.messages.analysis.Documents;
import org.conqueror.bird.data.messages.index.IndexContents;
import org.conqueror.bird.data.messages.index.IndexContentTaskFinishRequest;
import org.conqueror.lion.cluster.task.TaskManager;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;


public class IndexTaskManager extends TaskManager<IndexConfig> {

    private ActorRef analysisManager;
    private ActorRef indexContentManager;

    private static final String AnalysisManagerName = "analysis-manager";
    private static final String IndexContentManagerName = "index-content-manager";

    public IndexTaskManager(JobConfig config, ActorRef master, ActorRef jobManager) {
        super(config, master, jobManager);
    }

    public Receive createReceive() {
        return super.createReceive().orElse(receiveBuilder()
            .match(Documents.class, this::processDocuments)
            .match(AnalysisTaskFinishRequest.class, this::processFinishAnalysisManager)
            .match(IndexContentTaskFinishRequest.class, this::processFinishIndexContentManager)
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build());
    }

    @Override
    protected void prepareJob() throws Exception {
        indexContentManager = getContext().actorOf(IndexContentTaskManager.props(getConfig(), getSelf(), null), IndexContentManagerName);
        analysisManager = getContext().actorOf(AnalysisTaskManager.props(getConfig(), getSelf(), indexContentManager), AnalysisManagerName);
    }

    @Override
    protected void finishJob() throws Exception {

    }

    @Override
    // task-worker들이 모두 종료되어도 바로 task-manager를 졸요하지 않고 두 manager 부터 종료 시킴
    protected void finishTaskManager() {
        // stop index and analysis managers
        analysisManager.tell(JobManagerMessage.TaskAssignFinishResponse.getInstance(), getSelf());
    }

    private void processFinishAnalysisManager(AnalysisTaskFinishRequest request) {
        log().info("analysisManager is finished");

        // index-content-task-manager 종료시킴
        indexContentManager.tell(JobManagerMessage.TaskAssignFinishResponse.getInstance(), getSelf());
    }

    private void processFinishIndexContentManager(IndexContentTaskFinishRequest request) {
        log().info("indexContentManager is finished");

        // task-manager 종료
        super.finishTaskManager();
    }

    private void processDocuments(Documents documents) {
        analysisManager.forward(documents, getContext());
    }

    private void processIndexContents(IndexContents contents) {
        indexContentManager.forward(contents, getContext());
    }

}
