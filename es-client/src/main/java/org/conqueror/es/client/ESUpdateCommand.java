package org.conqueror.es.client;

import org.conqueror.es.client.index.updater.Updaters;
import org.conqueror.es.client.search.Query;


public class ESUpdateCommand extends ESCommand {

	private final Query query;
	private final String analysisFieldName;
	private final Updaters updaters;
	private final int bulkSize;
	private final int aliveTimes;

	public ESUpdateCommand(String index, String analysisFieldName, Query query, Updaters updaters) {
		this(index, analysisFieldName, query, updaters, 1000, 180000);
	}

	public ESUpdateCommand(String[] indices, String analysisFieldName, Query query, Updaters updaters) {
		this(indices, analysisFieldName, query, updaters, 1000, 180000);
	}

	public ESUpdateCommand(String index, String analysisFieldName, Query query, Updaters updaters, int bulkSize, int aliveTimes) {
		this(new String[]{index}, analysisFieldName, query, updaters, bulkSize, aliveTimes);
	}

	public ESUpdateCommand(String[] indices, String analysisFieldName, Query query, Updaters updaters, int bulkSize, int aliveTimes) {
		super(indices);
		this.query = query;
		this.analysisFieldName = analysisFieldName;
		this.updaters = updaters;
		this.bulkSize = bulkSize;
		this.aliveTimes = aliveTimes;
	}

	public String getAnalysisFieldName() {
		return analysisFieldName;
	}

	public Query getQuery() {
		return query;
	}

	public Updaters getUpdaters() {
		return updaters;
	}

	public int getBulkSize() {
		return bulkSize;
	}

	public int getAliveTimes() {
		return aliveTimes;
	}
}
