package org.conqueror.es.client;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.conqueror.es.client.ESIndexCommand.ESCommandType;
import org.conqueror.es.client.index.IndexCommand;
import org.conqueror.es.client.index.UpdateCommand;
import org.conqueror.es.client.index.source.RelationIndexContent;
import org.conqueror.es.client.search.SearchCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ESExecutor {

	private final ESConnector connector;

	public ESExecutor(ESConnector connector) {
		this.connector = connector;
	}

	public ESResult index(ESIndexCommand command) {
		ActionResponse response = null;
		if (command.getCommandType().equals(ESCommandType.INDEX)) {
			response = IndexCommand.index(connector.getClient(), command.getIndex(), command.getMapping(), command.getChildMapping()
					, (RelationIndexContent) command.getContent(), command.isPutIfAbsent(), command.getTimeoutSec());
		} else if (command.getCommandType().equals(ESCommandType.RELATION_INDEX)) {
			response = IndexCommand.index(connector.getClient(), command.getIndex(), command.getMapping()
					, command.getChildMapping(), command.getRelationContent(), command.isPutIfAbsent(), command.getTimeoutSec());
		} else if (command.getCommandType().equals(ESCommandType.BULK_INDEX)) {
			response = IndexCommand.index(connector.getClient(), command.getIndex(), command.getMapping()
					, command.getContents(), command.isPutIfAbsent(), command.getTimeoutSec());
		} else if (command.getCommandType().equals(ESCommandType.BULK_RELATION_INDEX)) {
			response = IndexCommand.index(connector.getClient(), command.getIndex(), command.getMapping()
					, command.getChildMapping(), command.getRelationContents(), command.isPutIfAbsent(), command.getTimeoutSec());
		}
		return new ESResult(response);
	}

	public ESResult getIndexMapping(ESIndexCommand command) {
		String response = IndexCommand.getMappings(connector.getClient(), command.getIndex(), command.getMapping(), command.getTimeoutSec());
		return new ESResult(response);
	}

	public boolean createIndex(ESIndexCommand command) {
		String[] mappings;
		if (command.getCommandType().equals(ESCommandType.CREATE)) {
			mappings = new String[] {command.getMapping()};
		} else if (command.getCommandType().equals(ESCommandType.RELATION_CREATE)) {
			mappings = new String[] {command.getMapping(), command.getChildMapping()};
		} else {
			return false;
		}
		return IndexCommand.createIndex(connector.getClient(), command.getIndex(), mappings, command.getMappingJson(), command.getTimeoutSec(), command.getRequestRetries());
	}

	public boolean existIndex(ESIndexCommand command) {
		return IndexCommand.existIndex(connector.getClient(), command.getIndex(), command.getTimeoutSec());
	}

	public ESResult search(ESSearchCommand command) {
		SearchResponse response = SearchCommand.search(connector.getClient(), command.getIndices(), command.getQuery());
		return new ESResult(response);
	}

	public ESResult scroll(ESSearchCommand command) {
		String scrollId = command.getScrollId();
		if (scrollId == null) {
			scrollId = SearchCommand.prepareScroll(connector.getClient(), command.getIndices(), command.getQuery()
					, command.getScrollSize(), command.getAliveTimes());
		}
		SearchResponse response = SearchCommand.scroll(connector.getClient(), scrollId, command.getAliveTimes());

		return (response != null)? new ESResult(response) : null;
	}

	public ESResult update(ESUpdateCommand command) {
		BulkResponse response = UpdateCommand.update(connector.getClient(), command.getIndices(), command.getAnalysisFieldName(), command.getQuery()
				, command.getUpdaters(), command.getBulkSize(), command.getAliveTimes());
		return new ESResult(response);
	}

	public ESResult searchIndexNames(ESIndexCommand command) {
		String indexRegex = command.getIndex();
		List<String> results = new ArrayList<>();
		Set<String> indexNames = IndexCommand.searchIndexNames(connector.getClient(), command.getTimeoutSec());
		if (indexNames != null) {
			for (String indexName : indexNames) {
				if (indexName.matches(indexRegex)) results.add(indexName);
			}
		}

		return new ESResult(results.toArray(new String[results.size()]));
	}

}
