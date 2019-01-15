package org.conqueror.bird.analysis;

import akka.event.LoggingAdapter;
import org.conqueror.cat.analysis.NGramAnalyzer;
import org.conqueror.cat.config.NGramConfig;


public class SimpleAnalyzer extends Analyzer {

    private final NGramAnalyzer analyzer;

    public SimpleAnalyzer(LoggingAdapter log, NGramConfig config) {
        super(log);

        analyzer = new NGramAnalyzer(config);
    }

    @Override
    protected KeywordResult analyze(String text) {
        return new KeywordResult(text, analyzer.analyze(text));
    }

    @Override
    protected void close() {
    }

}
