package org.conqueror.bird.analysis;

import org.conqueror.bird.gate.source.schema.FieldSchemaUsingAnalyzer.AnalysisItem;

import java.util.Locale;
import java.util.Map;


public class AnalysisResult {

	private String keywords = null;
	private String srchwords = null;
	private Map<String,Object>[] features = null;


	public Object getResult(AnalysisItem item) {
		switch (item) {
			case KEYWORD: return keywords;
			case SRCHWORD: return srchwords;
			case FEATURE: return features;
		}
		return null;
	}

	public Object getResult(String item) {
		return getResult(AnalysisItem.valueOf(item.toUpperCase(Locale.ENGLISH)));
	}

	public void setResult(AnalysisItem item, Object value) {
		switch (item) {
			case KEYWORD: keywords = (String) value; break;
			case SRCHWORD: srchwords = (String) value; break;
			case FEATURE: features = (Map<String, Object>[]) value; break;
		}
	}

	public void setResult(String item, Object value) {
		try {
			setResult(AnalysisItem.valueOf(item.toUpperCase(Locale.ENGLISH)), value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getSrchwords() {
		return srchwords;
	}

	public void setSrchwords(String srchwords) {
		this.srchwords = srchwords;
	}

	public Map<String,Object>[] getFeatures() {
		return features;
	}

	public void setFeatures(Map<String,Object>[] features) {
		this.features = features;
	}

}
