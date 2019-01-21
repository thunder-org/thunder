package org.conqueror.es.client.index;

import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.es.client.index.source.RelationIndexContent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexAction;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexAction;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class IndexCommand {

    private static final Logger logger = LoggerFactory.getLogger(IndexCommand.class);

    private static final String SCHEMA_SETTINGS = "settings";
    private static final String SCHEMA_MAPPINGS = "mappings";

    public static IndexResponse index(Client client, String index, String mapping, IndexContent content
        , boolean putIfAbsent, int timeoutSec) {
        IndexRequestBuilder request = client.prepareIndex(index, mapping)
            .setSource(content.getContent(), XContentType.JSON)
            .setId(content.getId())
            .setCreate(putIfAbsent);

        return request.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
    }

    public static BulkResponse index(Client client, String index, String parentMapping, String childMapping
        , RelationIndexContent content, boolean putIfAbsent, int timeoutSec) {
        BulkRequestBuilder request = client.prepareBulk();

        // parent
        request.add(client.prepareIndex(index, parentMapping)
            .setSource(content.getContent(), XContentType.JSON)
            .setId(content.getId())
            .setCreate(putIfAbsent));

        // children
        for (IndexContent child : content.getChildren()) {
            request.add(client.prepareIndex(index, childMapping)
                .setSource(child.getContent(), XContentType.JSON)
                .setCreate(putIfAbsent)
                .setId(child.getId())
                .setParent(content.getId()));
        }

        return request.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
    }

    public static BulkResponse index(Client client, String index, String mapping, List<IndexContent> contents
        , boolean putIfAbsent, int timeoutSec) {
        BulkRequestBuilder request = client.prepareBulk();
        for (IndexContent content : contents) {
            request.add(client.prepareIndex(index, mapping)
                .setSource(content.getContent(), XContentType.JSON)
                .setId(content.getId())
                .setCreate(putIfAbsent));
        }
        return request.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
    }

    public static BulkResponse index(Client client, String index, String parentMapping, String childMapping
        , List<RelationIndexContent> contents, boolean putIfAbsent, int timeoutSec) {
        BulkRequestBuilder parentRequest = client.prepareBulk();
        BulkRequestBuilder childRequest = client.prepareBulk();
        for (RelationIndexContent content : contents) {
            // parent
            parentRequest.add(client.prepareIndex(index, parentMapping)
                .setSource(content.getContent(), XContentType.JSON)
                .setId(content.getId())
                .setCreate(putIfAbsent)
            );

            // children
            for (IndexContent child : content.getChildren()) {
                childRequest.add(client.prepareIndex(index, childMapping)
                    .setSource(child.getContent(), XContentType.JSON)
                    .setId(child.getId())
                    .setCreate(putIfAbsent)
                    .setParent(content.getId())
                );
            }
        }
        BulkResponse parentResponse = parentRequest.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
        BulkResponse childResponse = childRequest.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
        return new BulkResponse(merge(parentResponse.getItems(), childResponse.getItems())
            , parentResponse.getTook().getMillis(), parentResponse.getIngestTookInMillis());
    }

    public static String getMappings(Client client, String index, String mapping, int timeoutSec) {
        String structure = null;

        try {
            ClusterState clusterState = client.admin()
                .cluster()
                .prepareState()
                .setIndices(index)
                .execute()
                .actionGet(timeoutSec, TimeUnit.SECONDS)
                .getState();

            IndexMetaData inMetaData = clusterState.getMetaData().index(index);
            MappingMetaData metad = inMetaData.mapping(mapping);

            if (metad != null) {
                structure = metad.getSourceAsMap().toString();
            }
        } catch (Exception e) {
            logger.error("failed to get mapping");
            e.printStackTrace();
        }

        return structure;
    }

    public static boolean createIndex(Client client, String index, String mapping, String schemaJson, int timeoutSec, int requestRetries) {
        return createIndex(client, index, new String[]{mapping}, schemaJson, timeoutSec, requestRetries);
    }

    public static boolean createIndex(Client client, String index, String[] mappings, String schemaJson, int timeoutSec, int requestRetries) {
        try {
            for (int retry = requestRetries + 1; retry > 0; retry--) {
                try {
                    CreateIndexRequestBuilder builder = new CreateIndexRequestBuilder(client, CreateIndexAction.INSTANCE, index);
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(schemaJson);
                    if (jsonObject != null) {
                        builder.setSettings(jsonObject.get(SCHEMA_SETTINGS).toString(), XContentType.JSON);
                        for (String mapping : mappings) {
                            if (((JSONObject) jsonObject.get(SCHEMA_MAPPINGS)).get(mapping) != null) {
                                builder.addMapping(mapping
                                    , ((JSONObject) jsonObject.get(SCHEMA_MAPPINGS)).get(mapping).toString(), XContentType.JSON);
                            }
                        }
                    }
                    builder.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
                } catch (ResourceAlreadyExistsException e) {
                    logger.warn("index already exists - {}", index);
                    break;
                } catch (ElasticsearchException e) {
                    if (retry > 1) {
                        logger.warn("elasticsearch failure - create index ({}) (remain retry : {})", index, retry - 1);
                        Thread.sleep(5000);
                    } else {
                        logger.error("elasticsearch failure - create index");
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("failed to create index");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Set<String> searchIndexNames(Client client, int timeoutSec) {
        try {
            ActionFuture<IndicesStatsResponse> stats = client.admin().indices().stats(new IndicesStatsRequest());
            IndicesStatsResponse response = stats.actionGet(timeoutSec, TimeUnit.SECONDS);
            Map<String, IndexStats> indices = response.getIndices();

            return indices.keySet();
        } catch (Exception e) {
            logger.error("failed to search index names");
            e.printStackTrace();
        }

        return null;
    }

    public static boolean deleteIndex(Client client, String index, int timeoutSec) {
        try {
            DeleteIndexRequestBuilder builder = new DeleteIndexRequestBuilder(client, DeleteIndexAction.INSTANCE, index);
            builder.execute().actionGet(timeoutSec, TimeUnit.SECONDS);
        } catch (ElasticsearchException e) {
            logger.error("elasticsearch failure - delete index");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.error("failed to delete index");
            e.printStackTrace();
        }

        return true;
    }

    public static boolean existIndex(Client client, String index, int timeoutSec) {
        IndicesExistsResponse actionGet = null;

        try {
            ActionFuture<IndicesExistsResponse> exists = client.admin()
                .indices()
                .exists(new IndicesExistsRequest(index));

            actionGet = exists.actionGet(timeoutSec, TimeUnit.SECONDS);
        } catch (ElasticsearchException e) {
            logger.error("elasticsearch failure - is index exist");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("failed to create index");
            e.printStackTrace();
        }

        return actionGet != null && actionGet.isExists();
    }


    private static BulkItemResponse[] merge(BulkItemResponse[] arr1, BulkItemResponse[] arr2) {
        BulkItemResponse[] mergedArr = new BulkItemResponse[arr1.length + arr2.length];
        int arrNum = 0;
        for (BulkItemResponse value : arr1) mergedArr[arrNum++] = value;
        for (BulkItemResponse value : arr2) mergedArr[arrNum++] = value;
        return mergedArr;
    }

}
