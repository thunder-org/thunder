package org.conqueror.bird.job;

import akka.actor.ActorRef;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignRequest;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignResponse;
import org.conqueror.bird.gate.FileGateSourceDistributor;
import org.conqueror.bird.gate.GateSourceDistributor;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.lion.cluster.job.JobManager;
import org.conqueror.lion.message.JobManagerMessage;

import java.util.List;


public class IndexJobManager extends JobManager<IndexConfig> {

    private final GateSourceDistributor gateSourceDistributor;

    public IndexJobManager(IndexConfig config, ActorRef master) {
        super(config, master);

        gateSourceDistributor = new FileGateSourceDistributor(getContext().getSystem(), getSelf(), config);
    }

    @Override
    protected void prepareJob() throws Exception {
        gateSourceDistributor.loadGateSources();
    }

    @Override
    protected void finishJob() throws Exception {

    }

    @Override
    protected JobManagerMessage.TaskAssignResponse assignTask(JobManagerMessage.TaskAssignRequest request) throws Exception {
        int size = ((IndexSourceAssignRequest) request).getSize();
        List<GateSource> sources = gateSourceDistributor.takeGateSources(size);
        if (sources.isEmpty()) return new JobManagerMessage.TaskAssignFinishResponse();

        return new IndexSourceAssignResponse(sources);
    }

}
