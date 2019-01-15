package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.index.IndexTaskFinishRequest;
import org.conqueror.lion.config.JobConfig;


public class IndexContentTaskManager extends TransferTaskManager<IndexConfig> {

    public static Props props(JobConfig jobConfig, ActorRef taskManager, ActorRef indexTaskManager) {
        return Props.create(IndexContentTaskManager.class, jobConfig, taskManager, indexTaskManager);
    }

    public IndexContentTaskManager(JobConfig config, ActorRef taskManager, ActorRef indexTaskManager) {
        super(config, taskManager, indexTaskManager);
    }

    @Override
    protected void prepareJob() throws Exception {

    }

    @Override
    protected void finishJob() throws Exception {
        getTaskManager().tell(new IndexTaskFinishRequest(), getSelf());
    }

    @Override
    protected Props createTransferTaskWorkerProps() {
        return IndexContentTaskWorker.props(getConfig(), getSelf(), getTransferTo());
    }

    @Override
    protected String makeTransferTaskWorkerRouterName() {
        return "index-content-task-worker-router";
    }

}
