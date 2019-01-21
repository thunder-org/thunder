package org.conqueror.es.client.index.source;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.ArrayList;
import java.util.List;


public class RelationIndexContent extends IndexContent {

	private final List<IndexContent> children = new ArrayList<>();
	private final String childType;
	private final boolean childPutIfAbsent;

	public RelationIndexContent(String indexName, String mappingName, String parentType, String childType
			, String id, XContentBuilder parent, boolean parentPutIfAbsent, boolean childPutIfAbsent) {
		super(indexName, mappingName, parentType, id, parent, parentPutIfAbsent);
		this.childType = childType;
		this.childPutIfAbsent = childPutIfAbsent;
	}

	public String getChildType() {
		return childType;
	}

	public boolean isChildPutIfAbsent() {
		return childPutIfAbsent;
	}

	public void addChild(IndexContent child) {
		children.add(child);
	}

	public void addChildren(List<IndexContent> children) {
		this.children.addAll(children);
	}

	public List<IndexContent> getChildren() {
		return children;
	}

}
