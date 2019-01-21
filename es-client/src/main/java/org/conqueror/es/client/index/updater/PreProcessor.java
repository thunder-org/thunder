package org.conqueror.es.client.index.updater;

import java.util.Map;


public abstract class PreProcessor {

	private Object tool = null;

	public void setTool(Object tool) {
		this.tool = tool;
	}

	public Object getTool() {
		return tool;
	}

	public abstract Object process(Map<String, Object> source, String fieldName);

}
