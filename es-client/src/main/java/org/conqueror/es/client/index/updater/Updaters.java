package org.conqueror.es.client.index.updater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Updaters implements Iterable<Updaters.FieldUpdaterPair> {
	
	private PreProcessor preProcessor = null;
	
	public enum ContentType { FIELD, ARRAY }

	public static class FieldUpdaterPair {

		private String fieldName;
		private Updater updater;
		private ContentType type;
		
		public FieldUpdaterPair(String fieldName, Updater updater, ContentType type) {
			this.fieldName = fieldName;
			this.updater = updater;
			this.type = type;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public Updater getUpdater() {
			return updater;
		}
		public void setUpdater(Updater updater) {
			this.updater = updater;
		}
		public ContentType getContentType() {
			return type;
		}
		public void setContentType(ContentType type) {
			this.type = type;
		}

	}
	
	private List<FieldUpdaterPair> updaters = new ArrayList<>();
	
	public void setPreProcessor(PreProcessor processor) {
		this.preProcessor = processor;
	}
	
	public PreProcessor getPreProcessor() {
		return preProcessor;
	}
	
	public boolean hasPreProcessor() {
		return preProcessor != null;
	}
	
	public void addUpdater(String fieldName, Updater updater, ContentType type) {
		updaters.add(new FieldUpdaterPair(fieldName, updater, type));
	}

	public Iterator<FieldUpdaterPair> iterator() {
		return updaters.iterator();
	}
	
}
