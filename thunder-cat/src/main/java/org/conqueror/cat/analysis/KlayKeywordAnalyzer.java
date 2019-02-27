package org.conqueror.cat.analysis;

import da.klay.core.Klay;
import da.klay.core.morphology.analysis.Morph;
import da.klay.core.morphology.analysis.Morphs;
import org.conqueror.cat.config.KeywordAnalyzerConfig;

import java.nio.file.Paths;


public class KlayKeywordAnalyzer extends KeywordAnalyzer {

    private final Klay klay;

    public KlayKeywordAnalyzer(KeywordAnalyzerConfig config) throws Exception {
        super(config);

        klay = new Klay(Paths.get(getConfig().getAnalyzerConfFilePath()));
    }

    @Override
    public Iterable getMorphs(String text) {
        Morphs morphs = klay.doKlay(text);
        return morphs.getMorphs();
    }

    @Override
    protected CharSequence getPos(Object morph) {
        return ((Morph) morph).getPos();
    }

    @Override
    protected String getText(Object morph) {
        return ((Morph) morph).getText().toString();
    }

    @Override
    public void close() {
    }

}
