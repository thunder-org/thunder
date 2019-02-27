package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.analysis.Analyzer;
import org.conqueror.bird.analysis.KoreanAnalyzer;
import org.conqueror.bird.analysis.SimpleAnalyzer;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.bird.data.messages.analysis.Documents;
import org.conqueror.bird.data.messages.index.IndexContents;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.cat.config.KeywordAnalyzerConfig;
import org.conqueror.cat.config.NGramConfig;
import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.lion.config.JobConfig;

import java.util.ArrayList;
import java.util.List;


public class AnalysisTaskWorker extends DeliveryTaskWorker<IndexConfig> {

    private final Analyzer analyzer;

    public static Props props(JobConfig jobConfig, ActorRef transferTaskManager, ActorRef indexTaskManager) {
        return Props.create(AnalysisTaskWorker.class, jobConfig, transferTaskManager, indexTaskManager);
    }

    public AnalysisTaskWorker(JobConfig config, ActorRef transferTaskManager, ActorRef indexTaskManager) throws Exception {
        super(config, transferTaskManager, indexTaskManager);

//        analyzer = new SimpleAnalyzer(log(), new NGramConfig(getConfig().getAnalyzerConfFilePath()));
        analyzer = new KoreanAnalyzer(log(), new KeywordAnalyzerConfig(getConfig().getAnalyzerConfFilePath()));
    }

    @Override
    protected Class<?> getAssignTaskMessageClass() {
        return Documents.class;
    }

    @Override
    protected BirdMessage work(Object data) {
        List<IndexContent> contents = new ArrayList<>();

        if (data instanceof Documents) {
            for (Document document : ((Documents) data).getDocuments()) {
//                System.out.println(document);
                IndexContent content = analyzer.analyze(document);
//                System.out.println(content);
                contents.add(content);
            }
        }

        return new IndexContents(contents);
    }

    @Override
    protected void finish() {
        analyzer.close();
    }

}
