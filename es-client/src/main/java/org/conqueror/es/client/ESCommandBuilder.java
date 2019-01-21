package org.conqueror.es.client;

import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.es.client.index.source.RelationIndexContent;
import org.conqueror.es.client.index.updater.Updaters;
import org.conqueror.es.client.search.Query;

import java.util.List;


public class ESCommandBuilder {

	public static ESIndexCommand buildIndexCreatingCommand(String index, String mapping, String mappingJson) {
		return new ESIndexCommand(index, mapping, mappingJson);
	}

	public static ESIndexCommand buildRelationIndexCreatingCommand(String index, String parentMapping, String childMapping, String mappingJson) {
		return new ESIndexCommand(index, parentMapping, childMapping, mappingJson);
	}

	public static ESIndexCommand buildIndexDeletingCommand(String index) {
		return new ESIndexCommand(index);
	}

	public static ESIndexCommand buildIndexExistingCommand(String index) {
		return new ESIndexCommand(index);
	}

	public static ESIndexCommand buildIndexMappingGettingCommand(String index, String mapping) {
		return new ESIndexCommand(index, mapping);
	}

	public static ESIndexCommand buildIndexNamesGettingCommand(String indexRegex) {
		return new ESIndexCommand(indexRegex);
	}

	public static ESIndexCommand buildIndexCommand(String index, String mapping, IndexContent content, boolean putIfAbsent) {
		return new ESIndexCommand(index, mapping, content, putIfAbsent);
	}

	public static ESIndexCommand buildRelationIndexCommand(String index, String parentMapping, String childMapping, RelationIndexContent content, boolean putIfAbsent) {
		return new ESIndexCommand(index, parentMapping, childMapping, content, putIfAbsent);
	}

	public static ESIndexCommand buildBulkIndexCommand(String index, String mapping, List<IndexContent> contents, boolean putIfAbsent) {
		return new ESIndexCommand(index, mapping, contents, putIfAbsent);
	}

	public static ESIndexCommand buildBulkRelationIndexCommand(String index, String parentMapping, String childMapping, List<RelationIndexContent> contents, boolean putIfAbsent) {
		return new ESIndexCommand(index, parentMapping, childMapping, contents, putIfAbsent);
	}

	public static ESIndexCommand buildOptimizingCommand(String index) {
		return new ESIndexCommand(index);
	}

	public static ESIndexCommand buildOptimizingCommand(String[] indices) {
		return new ESIndexCommand(indices);
	}

	public static ESSearchCommand buildSearchCommand(String index, Query query) {
		return new ESSearchCommand(index, query);
	}

	public static ESSearchCommand buildSearchCommand(String[] indices, Query query) {
		return new ESSearchCommand(indices, query);
	}

	public static ESSearchCommand buildScrollCommand(String index, Query query, int scrollSize, int aliveTimes) {
		return new ESSearchCommand(index, query, scrollSize, aliveTimes);
	}

	public static ESSearchCommand buildScrollCommand(String[] indices, Query query, int scrollSize, int aliveTimes) {
		return new ESSearchCommand(indices, query, scrollSize, aliveTimes);
	}

	public static ESUpdateCommand buildUpdateCommand(String index, String analysisFieldName, Query query, Updaters updaters, int bulkSize, int aliveTimes) {
		return new ESUpdateCommand(index, analysisFieldName, query, updaters, bulkSize, aliveTimes);
	}

}
