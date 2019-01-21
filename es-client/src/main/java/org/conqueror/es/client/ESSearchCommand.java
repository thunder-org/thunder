package org.conqueror.es.client;


import org.conqueror.es.client.search.Query;


public class ESSearchCommand extends ESCommand {

	public enum DataType { QUERY, SCROLL }

	private DataType dataType;

	private Query query;

	private String scrollId = null;
	private int scrollSize = 0;
	private int aliveTimes = 0;

	public ESSearchCommand(String index, Query query) {
		super(index);
		this.query = query;
		this.dataType = DataType.QUERY;
	}

	public ESSearchCommand(String[] indices, Query query) {
		super(indices);
		this.query = query;
		this.dataType = DataType.QUERY;
	}

	public ESSearchCommand(String index, Query query, int scrollSize, int aliveTimes) {
		super(index);
		this.query = query;
		this.scrollSize = scrollSize;
		this.aliveTimes = aliveTimes;
		this.dataType = DataType.SCROLL;
	}

	public ESSearchCommand(String[] indices, Query query, int scrollSize, int aliveTimes) {
		super(indices);
		this.query = query;
		this.scrollSize = scrollSize;
		this.aliveTimes = aliveTimes;
		this.dataType = DataType.SCROLL;
	}

	public Query getQuery() {
		return query;
	}

	public String getScrollId() {
		return scrollId;
	}

	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

	public DataType getDataType() {
		return dataType;
	}

	public int getScrollSize() {
		return scrollSize;
	}

	public int getAliveTimes() {
		return aliveTimes;
	}

}
