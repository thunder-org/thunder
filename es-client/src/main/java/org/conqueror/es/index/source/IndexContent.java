package org.conqueror.es.index.source;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;


public class IndexContent {

	private final String indexName;
	private final String type;
	private final String id;
	private final XContentBuilder content;

	private final boolean putIfAbsent;


	private IndexContent() {
		this(null, null, null, null, false);
	}

	public IndexContent(String indexName, String type, String id, XContentBuilder content, boolean putIfAbsent) {
		this.indexName = indexName;
		this.type = type;
		this.id = id;
		this.content = content;
		this.putIfAbsent = putIfAbsent;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public XContentBuilder getContent() {
		return content;
	}

	public boolean isPutIfAbsent() {
		return putIfAbsent;
	}

	public String toString() {
		return indexName + ":" + Strings.toString(content);
	}

}
