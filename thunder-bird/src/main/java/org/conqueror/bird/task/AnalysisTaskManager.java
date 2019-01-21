package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.analysis.AnalysisTaskFinishRequest;
import org.conqueror.bird.data.messages.analysis.Documents;
import org.conqueror.lion.config.JobConfig;


public class AnalysisTaskManager extends DeliveryTaskManager<IndexConfig> {

    public static Props props(JobConfig jobConfig, ActorRef taskManager, ActorRef indexTaskManager) {
        return Props.create(AnalysisTaskManager.class, jobConfig, taskManager, indexTaskManager);
    }

    public AnalysisTaskManager(JobConfig config, ActorRef taskManager, ActorRef indexTaskManager) {
        super(config, taskManager, indexTaskManager);
    }

    @Override
    protected int getNumberOfTaskWorkers() {
        return getConfig().getNumberOfAnalyzers();
    }

    @Override
    protected void prepareJob() throws Exception {

    }

    @Override
    protected void finishJob() throws Exception {
        getTaskManager().tell(new AnalysisTaskFinishRequest(), getSelf());
    }

    @Override
    protected Props createDeliveryTaskWorkerProps() {
        return AnalysisTaskWorker.props(getConfig(), getSelf(), getDeliverTo());
    }

    @Override
    protected String makeDeliveryTaskWorkerRouterName() {
        return "analysis-task-worker-router";
    }

    @Override
    protected Class getAssignTaskMessageClass() {
        return Documents.class;
    }

}
