package org.conqueror.cat.analysis;

import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.conqueror.cat.config.KeywordAnalyzerConfig;
import org.conqueror.cat.config.KomoranConfig;

import java.util.ArrayList;


public class KomoranKeywordAnalyzer extends KeywordAnalyzer {

    private Komoran komoran;

    public KomoranKeywordAnalyzer(KeywordAnalyzerConfig config) throws Exception {
        super(config);

        KomoranConfig komoranConfig = new KomoranConfig(config.getAnalyzerConfFilePath());

        komoran = new Komoran(komoranConfig.getModelDirPath());
        komoran.setFWDic(komoranConfig.getFwDicPath());
        komoran.setUserDic(komoranConfig.getUserDicPath());
    }

    @Override
    public Iterable getMorphs(String text) {
        try {
            KomoranResult analyzeResultList = komoran.analyze(text);
            return analyzeResultList.getTokenList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    protected CharSequence getPos(Object morph) {
        return ((Token) morph).getPos();
    }

    @Override
    protected String getText(Object morph) {
        return ((Token) morph).getMorph();
    }

    @Override
    public void close() {
    }

}
