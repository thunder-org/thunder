package org.conqueror.es.client.search;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;

import java.util.HashSet;
import java.util.Set;


public class Aggregation {

	private Set<AbstractAggregationBuilder> aggregations = new HashSet<>();
	private static BucketOrder TERMS_COUNT_ORDER = BucketOrder.count(false);
	private static final int SHARD_EXTRACT_TERM_MULTIPLE = 1;
	
	public Aggregation addTermAggregation(String name, String field, int top) {
		return addAggregation(
				AggregationBuilders.terms(name).field(field)
					.size(top).shardSize(top * SHARD_EXTRACT_TERM_MULTIPLE).order(TERMS_COUNT_ORDER));
	}
	
	public Aggregation addAggregation(AbstractAggregationBuilder aggregation) {
		this.aggregations.add(aggregation);
		return this;
	}
	
	public Set<AbstractAggregationBuilder> getAggregations() {
		return aggregations;
	}
	
}
