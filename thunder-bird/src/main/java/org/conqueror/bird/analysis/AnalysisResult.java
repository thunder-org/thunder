package org.conqueror.bird.analysis;

import org.conqueror.bird.gate.source.schema.FieldSchemaUsingAnalyzer.AnalysisItem;

import java.util.Locale;


public class AnalysisResult {

    private String indexTerms = null;


    public Object getResult(AnalysisItem item) {
        switch (item) {
            case INDEXTERM:
                return indexTerms;
        }
        return null;
    }

    public Object getResult(String item) {
        return getResult(AnalysisItem.valueOf(item.toUpperCase(Locale.ENGLISH)));
    }

    public void setResult(AnalysisItem item, Object value) {
        switch (item) {
            case INDEXTERM:
                indexTerms = (String) value;
                break;
        }
    }

    public void setResult(String item, Object value) {
        try {
            setResult(AnalysisItem.valueOf(item.toUpperCase(Locale.ENGLISH)), value);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public String getIndexTerms() {
        return indexTerms;
    }

    public void setIndexTerms(String indexTerms) {
        this.indexTerms = indexTerms;
    }

}
