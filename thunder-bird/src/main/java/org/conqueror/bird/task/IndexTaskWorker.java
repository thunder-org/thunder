package org.conqueror.bird.task;

import akka.actor.ActorRef;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignRequest;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignResponse;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.lion.cluster.task.TaskWorker;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;

import java.util.List;


public class IndexTaskWorker extends TaskWorker<IndexConfig, IndexSourceAssignResponse> {

    public IndexTaskWorker(JobConfig config, ActorRef taskManager) {
        super(config, taskManager);
    }

    @Override
    protected void work(IndexSourceAssignResponse response) {
        List<GateSource> sources = response.getSources();
        for (GateSource source : sources) {
            log().info("{}", source.toString());
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JobManagerMessage.TaskAssignRequest createTaskAssignRequest() {
        return new IndexSourceAssignRequest(getConfig().getNumberOfSources());
    }
}
