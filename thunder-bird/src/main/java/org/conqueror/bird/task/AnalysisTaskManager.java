package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.analysis.AnalysisTaskFinishRequest;
import org.conqueror.lion.config.JobConfig;


public class AnalysisTaskManager extends TransferTaskManager<IndexConfig> {

    public static Props props(JobConfig jobConfig, ActorRef taskManager, ActorRef indexTaskManager) {
        return Props.create(AnalysisTaskManager.class, jobConfig, taskManager, indexTaskManager);
    }

    public AnalysisTaskManager(JobConfig config, ActorRef taskManager, ActorRef indexTaskManager) {
        super(config, taskManager, indexTaskManager);
    }

    @Override
    protected void prepareJob() throws Exception {

    }

    @Override
    protected void finishJob() throws Exception {
        getTaskManager().tell(new AnalysisTaskFinishRequest(), getSelf());
    }

    @Override
    protected Props createTransferTaskWorkerProps() {
        return AnalysisTaskWorker.props(getConfig(), getSelf(), getTransferTo());
    }

    @Override
    protected String makeTransferTaskWorkerRouterName() {
        return "analysis-task-worker-router";
    }

}
