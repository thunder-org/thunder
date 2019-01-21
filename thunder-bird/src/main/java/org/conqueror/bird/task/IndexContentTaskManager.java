package org.conqueror.bird.task;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.data.messages.index.IndexContents;
import org.conqueror.bird.data.messages.index.IndexContentTaskFinishRequest;
import org.conqueror.bird.index.IndexInformation;
import org.conqueror.bird.index.source.IndexContentQueue;
import org.conqueror.bird.index.source.IndexContentQueueMap;
import org.conqueror.es.client.ESConnector;
import org.conqueror.es.client.ESExecutor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.JobManagerMessage;


public class IndexContentTaskManager extends DeliveryTaskManager<IndexConfig> {

    private final IndexContentQueueMap indexContentQueueMap;

    private final ESConnector connector;
    private final ESExecutor executor;

    public static Props props(JobConfig jobConfig, ActorRef taskManager, ActorRef indexTaskManager) {
        return Props.create(IndexContentTaskManager.class, jobConfig, taskManager, indexTaskManager);
    }

    public IndexContentTaskManager(JobConfig config, ActorRef taskManager, ActorRef indexTaskManager) {
        super(config, taskManager, indexTaskManager);
        IndexConfig indexConfig = getConfig();

        indexContentQueueMap = new IndexContentQueueMap(indexConfig.getBulkSize());

        IndexInformation indexInfo = indexConfig.getIndexInfo();

        connector = new ESConnector(indexInfo.getPingTimeOutSec(), indexInfo.getNodeSamplerIntervalSec());
        connector.open(indexInfo.getAddresses(), indexInfo.getCluster());

        executor = new ESExecutor(connector);
    }

    @Override
    protected int getNumberOfTaskWorkers() {
        return getConfig().getNumberOfIndexers();
    }

    @Override
    protected void prepareJob() throws Exception {

    }

    @Override
    protected void finishJob() throws Exception {
        connector.close();
        getTaskManager().tell(new IndexContentTaskFinishRequest(), getSelf());
    }

    @Override
    protected Props createDeliveryTaskWorkerProps() {
        return IndexContentTaskWorker.props(getConfig(), getSelf(), getDeliverTo(), executor);
    }

    @Override
    protected String makeDeliveryTaskWorkerRouterName() {
        return "index-content-task-worker-router";
    }

    @Override
    protected Class getAssignTaskMessageClass() {
        return IndexContents.class;
    }

    @Override
    protected void processAssignTask(Object taskSource) {
        IndexContents contents = (IndexContents) taskSource;
        if (!contents.getIndexContents().isEmpty()) {
            String indexName = contents.getIndexContents().get(0).getIndexName();
            String mappingName = contents.getIndexContents().get(0).getMappingName();
            IndexContentQueue contentQueue = indexContentQueueMap.getOrCreateQueue(indexName, mappingName);
            contentQueue.put(contents.getIndexContents());

            if (contentQueue.isFull()) {
                indexContentQueueMap.remove(indexName);
                super.processAssignTask(contentQueue);
            }
        }
    }

    @Override
    protected void processFinishTask(JobManagerMessage.TaskAssignFinishResponse response) {
        for (String index : indexContentQueueMap.getIndexNames()) {
            super.processAssignTask(indexContentQueueMap.remove(index));
        }

        super.processFinishTask(response);
    }
}
