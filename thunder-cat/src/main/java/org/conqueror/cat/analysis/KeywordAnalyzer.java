package org.conqueror.cat.analysis;

import org.conqueror.cat.config.KeywordAnalyzerConfig;
import org.conqueror.cat.settings.AnalysisSettings;
import org.conqueror.cat.settings.RefineSettings;
import org.conqueror.common.utils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class KeywordAnalyzer extends ObjectAnalyzer {

    private final RefineSettings refineSettings;
    private final AnalysisSettings analysisSettings;

    public KeywordAnalyzer(KeywordAnalyzerConfig config) throws Exception {
        super(config);

        refineSettings = RefineSettings.build(config);
        analysisSettings = AnalysisSettings.build(config);
    }

    public KeywordResult analyze(String text) {
        text = refineText(text);

        Iterable morphs = getMorphs(text);
        List<String> terms = extractTerms(morphs);

        return new KeywordResult(text, terms);
    }

    @Override
    public KeywordAnalyzerConfig getConfig() {
        return (KeywordAnalyzerConfig) super.getConfig();
    }

    public abstract Iterable getMorphs(String text);

    protected abstract CharSequence getPos(Object morph);

    protected abstract String getText(Object morph);

    private String refineText(String text) {
        text = StringUtils.refineAllWhiteSpace(text);

        if (refineSettings.isUrlRemove()) {
            text = StringUtils.removeUrl(text);
        }

        if (refineSettings.isEmailRemove()) {
            text = StringUtils.removeEmail(text);
        }

        return text;
    }

    private List<String> extractTerms(Iterable morphs) {
        List<String> terms = new ArrayList<>();

        for (Object morph : morphs) {
            if (morph == null) break;

            if (analysisSettings.isWantedType(getPos(morph))) terms.add(getText(morph));
        }

        return terms;
    }

}
