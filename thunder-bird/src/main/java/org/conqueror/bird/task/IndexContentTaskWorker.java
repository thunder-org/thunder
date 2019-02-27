package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.bird.index.IndexInformation;
import org.conqueror.bird.index.source.IndexContentQueue;
import org.conqueror.es.client.ESCommandBuilder;
import org.conqueror.es.client.ESExecutor;
import org.conqueror.es.client.ESResult;
import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.lion.config.JobConfig;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

import java.util.List;


public class IndexContentTaskWorker extends DeliveryTaskWorker<IndexConfig> {

    private final ESExecutor executor;
    private final IndexInformation indexInfo;

    public static Props props(JobConfig jobConfig, ActorRef transferTaskManager, ActorRef indexTaskManager, ESExecutor executor) {
        return Props.create(IndexContentTaskWorker.class, jobConfig, transferTaskManager, indexTaskManager, executor);
    }

    public IndexContentTaskWorker(JobConfig config, ActorRef transferTaskManager, ActorRef indexTaskManager, ESExecutor executor) {
        super(config, transferTaskManager, indexTaskManager);
        this.executor = executor;
        this.indexInfo = getConfig().getIndexInfo();
    }

    @Override
    protected Class<?> getAssignTaskMessageClass() {
        return IndexContentQueue.class;
    }

    @Override
    protected BirdMessage work(Object data) {
        if (data instanceof IndexContentQueue) {
            IndexContentQueue queue = (IndexContentQueue) data;
            while (queue.remainElementsSize() > 0) {
                IndexContent content = queue.take();
                System.out.println(content);
            }
//            int failures = index(queue.getIndexName(), indexInfo.getMapping(queue.getMappingName()), queue.getQueue());
        }

        return null;
    }

    @Override
    protected void finish() {

    }

    private int index(String indexName, String mappingJson, List<? extends IndexContent> contents) {
        String parentMappingName = indexInfo.getParentMappingName();
        String childMappingName = indexInfo.getChildMappingName();
        if (!executor.existIndex(ESCommandBuilder.buildIndexExistingCommand(indexName))) {
            executor.createIndex(ESCommandBuilder.buildIndexCreatingCommand(indexName, parentMappingName, mappingJson));
        }

        ESResult result = executor.index(ESCommandBuilder.buildBulkIndexCommand(indexName, parentMappingName, (List<IndexContent>) contents, true));

        int numberOfFailures = 0;
        if (result != null) {
            BulkResponse response = result.getBulkResult();
            if (response.hasFailures()) {
                for (BulkItemResponse item : response) {
                    if (item.isFailed() && item.getType().equals(parentMappingName)) numberOfFailures++;
                }
            }
        }

        return numberOfFailures;
    }

}
