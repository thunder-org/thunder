package org.conqueror.es.client;


import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.es.client.index.source.RelationIndexContent;

import java.util.List;


public class ESIndexCommand extends ESCommand {

	public enum ESCommandType { INDEX, RELATION_INDEX, BULK_INDEX, BULK_RELATION_INDEX, CREATE, RELATION_CREATE }

	private ESCommandType commandType = null;
	private String mapping = null;
	private String childMapping = null;
	private String mappingJson = null;
	private IndexContent content = null;
	private List<? extends IndexContent> contents = null;
	private boolean putIfAbsent = false;
	private int timeoutSec = 600;
	private int requestRetries = 0;

	protected ESIndexCommand(String index) {
		super(index);
	}

	protected ESIndexCommand(String[] indices) {
		super(indices);
	}

	protected ESIndexCommand(String index, String mapping) {
		super(index);
		this.mapping = mapping;
	}

	protected ESIndexCommand(String index, String mapping, String mappingJson) {
		super(index);
		this.mapping = mapping;
		this.mappingJson = mappingJson;
		this.commandType = ESCommandType.CREATE;
	}

	protected ESIndexCommand(String index, String parentMapping, String childMapping, String mappingJson) {
		super(index);
		this.mapping = parentMapping;
		this.childMapping = childMapping;
		this.mappingJson = mappingJson;
		this.commandType = ESCommandType.RELATION_CREATE;
	}

	protected ESIndexCommand(String index, String mapping, IndexContent content, boolean putIfAbsent) {
		super(index);
		this.mapping = mapping;
		this.content = content;
		this.putIfAbsent = putIfAbsent;
		this.commandType = ESCommandType.INDEX;
	}

	protected ESIndexCommand(String index, String parentMapping, String childMapping, RelationIndexContent content, boolean putIfAbsent) {
		super(index);
		this.mapping = parentMapping;
		this.childMapping = childMapping;
		this.content = content;
		this.putIfAbsent = putIfAbsent;
		this.commandType = ESCommandType.RELATION_INDEX;
	}

	protected ESIndexCommand(String index, String mapping, List<IndexContent> contents, boolean putIfAbsent) {
		super(index);
		this.mapping = mapping;
		this.contents = contents;
		this.putIfAbsent = putIfAbsent;
		this.commandType = ESCommandType.BULK_INDEX;
	}

	protected ESIndexCommand(String index, String parentMapping, String childMapping, List<RelationIndexContent> contents, boolean putIfAbsent) {
		super(index);
		this.mapping = parentMapping;
		this.childMapping = childMapping;
		this.contents = contents;
		this.putIfAbsent = putIfAbsent;
		this.commandType = ESCommandType.BULK_RELATION_INDEX;
	}

	public String getMapping() {
		return mapping;
	}

	public String getChildMapping() {
		return childMapping;
	}

	public String getMappingJson() {
		return mappingJson;
	}

	public IndexContent getContent() {
		return content;
	}

	public RelationIndexContent getRelationContent() {
		try {
			return (RelationIndexContent) content;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public void setCommandType(ESCommandType commandType) {
		this.commandType = commandType;
	}

	public ESCommandType getCommandType() {
		return commandType;
	}

	public List<IndexContent> getContents() {
		try {
			//noinspection unchecked
			return (List<IndexContent>) contents;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public List<RelationIndexContent> getRelationContents() {
		try {
			//noinspection unchecked
			return (List<RelationIndexContent>) contents;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public boolean isPutIfAbsent() {
		return putIfAbsent;
	}

	public ESIndexCommand setPutIfAbsent(boolean putIfAbsent) {
		this.putIfAbsent = putIfAbsent;
		return this;
	}

	public int getTimeoutSec() {
		return timeoutSec;
	}

	public ESIndexCommand setTimeoutSec(int timeoutSec) {
		this.timeoutSec = timeoutSec;
		return this;
	}

	public int getRequestRetries() {
		return requestRetries;
	}

	public ESIndexCommand setRequestRetries(int requestRetries) {
		this.requestRetries = requestRetries;
		return this;
	}
}
