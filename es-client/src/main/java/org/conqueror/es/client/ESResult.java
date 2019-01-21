package org.conqueror.es.client;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;


public class ESResult {

	protected Object result;

	public ESResult(Object result) {
		this.result = result;
	}

	public IndexResponse getIndexResult() {
		return (result instanceof IndexResponse)? (IndexResponse) result : null;
	}

	public GetResponse getGetResult() {
		return (result instanceof GetResponse)? (GetResponse) result : null;
	}

	public DeleteResponse getDeleteResult() {
		return (result instanceof DeleteResponse)? (DeleteResponse) result : null;
	}

	public BulkResponse getBulkResult() {
		return (result instanceof BulkResponse)? (BulkResponse) result : null;
	}

	public SearchResponse getSearchResult() {
		return (result instanceof SearchResponse)? (SearchResponse) result : null;
	}

	public String getScrollId() {
		return (result instanceof String)? (String) result : null;
	}

	public String getStringResult() {
		return (result instanceof String)? (String) result : null;
	}

	public String[] getIndexNames() {
		return (result instanceof String[])? (String[]) result : null;
	}

	public boolean getCreateResult() {
		return (Boolean) result;
	}

}
