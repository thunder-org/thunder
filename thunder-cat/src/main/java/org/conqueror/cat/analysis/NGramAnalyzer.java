package org.conqueror.cat.analysis;

import org.conqueror.cat.config.NGramConfig;

import java.util.ArrayList;
import java.util.List;


public class NGramAnalyzer extends ObjectAnalyzer {

    public NGramAnalyzer(NGramConfig config) {
        super(config);
    }

    public List<String> analyze(String text) {
        List<String> grams = new ArrayList<>();
        NGramConfig config = (NGramConfig) getConfig();
        int numberOfGrams = config.getNumberOfGrams();
        for (String token : text.split("\\s")) {
            for (int idx = 0; idx <= token.length() - numberOfGrams; idx++) {
                grams.add(token.substring(idx, idx + numberOfGrams));
            }
            if (token.length() < numberOfGrams) {
                grams.add(token);
            }
        }

        return grams;
    }

    @Override
    public void close() {

    }
}
