package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.analysis.AnalysisTaskFinishRequest;
import org.conqueror.bird.data.messages.analysis.Documents;
import org.conqueror.common.utils.date.DateTimeUtils;
import org.conqueror.lion.config.JobConfig;
import org.joda.time.DateTime;


public class AnalysisTaskManager extends DeliveryTaskManager<IndexConfig> {

    private DateTime startTime;

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
        startTime = DateTime.now();
    }

    @Override
    protected void finishJob() throws Exception {
        log().info("elapsed time : {}", DateTimeUtils.getDurationString((DateTime.now().getMillis() - startTime.getMillis()) / 1000));

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
