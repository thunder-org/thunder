package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.bird.data.messages.index.IndexContents;
import org.conqueror.es.index.source.IndexContent;
import org.conqueror.lion.config.JobConfig;


public class IndexContentTaskWorker extends TransferTaskWorker<IndexConfig> {

    public static Props props(JobConfig jobConfig, ActorRef transferTaskManager, ActorRef indexTaskManager) {
        return Props.create(IndexContentTaskWorker.class, jobConfig, transferTaskManager, indexTaskManager);
    }

    public IndexContentTaskWorker(JobConfig config, ActorRef transferTaskManager, ActorRef indexTaskManager) {
        super(config, transferTaskManager, indexTaskManager);
    }

    @Override
    protected BirdMessage work(BirdMessage data) {
        if (data instanceof IndexContents) {
            for (IndexContent content : ((IndexContents) data).getIndexContents()) {
                System.out.println(content);
            }
        }

        return null;
    }

}
