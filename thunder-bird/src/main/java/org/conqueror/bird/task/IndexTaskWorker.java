package org.conqueror.bird.task;

import akka.actor.ActorRef;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.analysis.Documents;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignRequest;
import org.conqueror.bird.data.messages.job.manager.IndexSourceAssignResponse;
import org.conqueror.bird.exceptions.scan.ScanException;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.scan.FileGateScanner;
import org.conqueror.bird.gate.scan.GateScanner;
import org.conqueror.bird.gate.source.FileGateSource;
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
    protected void work(JobManagerMessage.TaskAssignResponse response) {
        List<GateSource> sources = ((IndexSourceAssignResponse) response).getSources();
        for (GateSource source : sources) {
            log().info("[START] source : {}", source.toString());

            if (source instanceof FileGateSource) {
                try (GateScanner scanner = new FileGateScanner(getConfig().getIndexNameMaxSize())) {
                    scanner.open(source);
                    List<Document> documents;
                    while (!(documents = scanner.scan()).isEmpty())  {
                        transfer(documents);
                    }
                } catch (ScanException e) {
                    log().error(e, "failed to open scanner : {}", source);
                }
            } else {
                log().error("wrong source type : {}", source.getClass());
            }

            log().info("[END] source : {}", source.toString());
        }
    }

    @Override
    protected JobManagerMessage.TaskAssignRequest createTaskAssignRequest() {
        return new IndexSourceAssignRequest(getConfig().getNumberOfSources());
    }

    private void transfer(List<Document> documents) {
        sendToTaskManager(new Documents(documents));
    }

}
