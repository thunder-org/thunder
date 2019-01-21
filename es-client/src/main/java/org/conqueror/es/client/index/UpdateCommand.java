package org.conqueror.es.client.index;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.conqueror.es.client.index.updater.Updaters;
import org.conqueror.es.client.search.Query;
import org.conqueror.es.client.search.SearchCommand;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class UpdateCommand {

	private static final Logger logger = LoggerFactory.getLogger(UpdateCommand.class);

	private static final int defaultBulkSize = 1000;
	private static final int defaultAliveTimes = 1800000;

	public static BulkResponse update(Client client, String index, String analysisFieldName, Query query, Updaters updaters) {
		return update(client, new String[]{index}, analysisFieldName, query, updaters, defaultBulkSize, defaultAliveTimes);
	}

	public static BulkResponse update(Client client, String[] indices, String analysisFieldName, Query query, Updaters updaters) {
		return update(client, indices, analysisFieldName, query, updaters, defaultBulkSize, defaultAliveTimes);
	}

	public static BulkResponse update(Client client, String index, String analysisFieldName, Query query, Updaters updaters, int bulkSize, int aliveTimes) {
		return update(client, new String[]{index}, analysisFieldName, query, updaters, bulkSize, aliveTimes);
	}

	public static BulkResponse update(Client client, String[] indices, String analysisFieldName, Query query, Updaters updaters, int bulkSize, int aliveTimes) {
		SearchResponse srchResponse;
		BulkResponse response = null;
		BulkRequestBuilder request = null;
		int totCount = 0, hitCount = 0;

		String scrollID = SearchCommand.prepareScroll(client, indices, query, bulkSize, aliveTimes);
		while ( SearchCommand.remainScroll( (srchResponse = SearchCommand.scroll(client, scrollID, aliveTimes)) ) ) {
			scrollID = srchResponse.getScrollId();
			for (SearchHit hit : srchResponse.getHits()) {
				if (request == null) request = client.prepareBulk();

				try {
					Map<String, Object> source = hit.getSourceAsMap();
					Object preprocessed;
					if (updaters.hasPreProcessor()) {
						preprocessed = updaters.getPreProcessor().process(source, analysisFieldName);
					} else {
						preprocessed = null;
					}

					XContentBuilder content = XContentFactory.jsonBuilder();
					content.startObject();
					for (Updaters.FieldUpdaterPair pair : updaters) {
						switch (pair.getContentType()) {
						case ARRAY:
							content.array(pair.getFieldName(), pair.getUpdater().update(source, preprocessed));
							break;
						case FIELD:
							content.field(pair.getFieldName(), pair.getUpdater().update(source, preprocessed));
							break;
						}
					}
					content.endObject();
					UpdateRequestBuilder update = client.prepareUpdate(hit.getIndex(), hit.getType(), hit.getId());
					update.setDoc(content);
					if (hasParent(hit)) {
						update.setParent( (String) hit.getFields().get("_parent").getValue());
					}

					request.add(update);

				} catch (IOException e) {
					logger.error("io failure - update");
					e.printStackTrace();
				} catch (Exception e) {
					logger.error("failed to update");
					e.printStackTrace();
				}

				if (bulkSize > 0 && ++hitCount >= bulkSize) {
					totCount += hitCount;
					hitCount = 0;
					response = request.execute().actionGet();
					request = null;
					logger.info("{} - updated count : {}", Arrays.toString(indices), totCount);
				}
			}
		}

		if (hitCount > 0) {
			totCount += hitCount;
			response = request.execute().actionGet();
		}
		logger.info("{} - total updated count : {}", Arrays.toString(indices), totCount);

		return response;
	}

	private static boolean hasParent(SearchHit hit) {
		Map<String, DocumentField> fields = hit.getFields();
		return fields != null && fields.containsKey("_parent");
	}

}
