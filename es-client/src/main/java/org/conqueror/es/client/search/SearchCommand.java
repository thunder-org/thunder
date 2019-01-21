package org.conqueror.es.client.search;

import org.conqueror.es.client.search.Query;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;


public class SearchCommand {

	public static SearchResponse search(Client client, String[] indices, Query query) {
		SearchResponse response = null;
		try {
			response = client.prepareSearch()
				.setIndices(indices)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(query.getQueryBuilder())
				.addStoredField("_source")
				.addStoredField("_parent")
				.execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static SearchResponse search(Client client, String[] indices, Query query, Aggregation aggregation) {
		SearchResponse response = null;
		try {
			SearchRequestBuilder request = client.prepareSearch()
				.setIndices(indices)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.addStoredField("_source")
				.addStoredField("_parent")
				.setQuery(query.getQueryBuilder());

			for (AbstractAggregationBuilder builder : aggregation.getAggregations()) {
				request.addAggregation(builder);
			}

			response = request.execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static String prepareScroll(Client client, String[] indices, Query query, int sizePerScroll, int aliveTimes) {
		SearchResponse response = null;
		try {
			response = client.prepareSearch()
				.setIndices(indices)
				.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
				.setScroll(new TimeValue(aliveTimes))
				.setSize(sizePerScroll)
				.addStoredField("_source")
				.addStoredField("_parent")
				.setQuery(query.getQueryBuilder())
				.execute().actionGet();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response != null ? response.getScrollId() : null;
	}

	public static boolean remainScroll(SearchResponse response) {
		return response != null && (response.getHits() != null && response.getHits().getHits().length > 0);

	}

	public static SearchResponse scroll(Client client, String id, int aliveTimes) {
		SearchResponse response = null;
		try {
			response = client.prepareSearchScroll(id)
				.setScroll(new TimeValue(aliveTimes))
				.execute().actionGet();

			if (!remainScroll(response)) {
				List<String> scrollIds = new ArrayList<>();
				scrollIds.add(id);
				client.prepareClearScroll().setScrollIds(scrollIds).execute().actionGet();
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}