package org.conqueror.es.client.search;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.io.IOException;
import java.io.Serializable;


public class InnerBoolQueryBuilder extends BoolQueryBuilder implements Serializable {

	public XContentBuilder toInnerXContent(XContentBuilder builder, Params params) throws IOException {
		doXContent(builder, params);
		return builder;
	}

}
