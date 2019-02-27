package org.conqueror.cat.analysis;

import java.util.List;


public class KeywordResult {

    private String text;
    private List<String> terms;

    public KeywordResult(String text, List<String> terms) {
        this.text = text;
        this.terms = terms;
    }

    public String getText() {
        return text;
    }

    public List<String> getTerms() {
        return terms;
    }

    @Override
    public String toString() {
        return "KeywordResult{" +
            "text='" + text + '\'' +
            ", terms=" + terms +
            '}';
    }

}
