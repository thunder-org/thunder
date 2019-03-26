package org.conqueror.bird.analysis;

import akka.event.LoggingAdapter;
import org.conqueror.cat.analysis.KeywordAnalyzer;
import org.conqueror.cat.analysis.KeywordResult;
import org.conqueror.cat.analysis.KlayKeywordAnalyzer;
import org.conqueror.cat.config.KeywordAnalyzerConfig;


public class KoreanAnalyzer extends Analyzer {

    private final KeywordAnalyzer analyzer;

    public KoreanAnalyzer(LoggingAdapter log, KeywordAnalyzerConfig config) throws Exception {
        super(log);

        analyzer = new KlayKeywordAnalyzer(config);
    }

    @Override
    protected KeywordResult analyze(String text) {
        return analyzer.analyze(text);
    }

    @Override
    public void close() {
        analyzer.close();
    }

}
