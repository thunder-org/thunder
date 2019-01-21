package org.conqueror.es.client;

public class ESCommand {

	private final String[] indices;

	public ESCommand(String index) {
		this.indices = new String[] {index};
	}

	public ESCommand(String[] indices) {
		this.indices = indices;
	}

	public String getIndex() {
		return indices[0];
	}

	public String[] getIndices() {
		return indices;
	}

}
