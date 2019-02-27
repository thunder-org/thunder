package org.conqueror.cat.settings;

import org.conqueror.cat.config.KeywordAnalyzerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.conqueror.cat.utils.MorphUtils.*;


public class AnalysisSettings {

    private List<CharSequence> poses = new ArrayList<>();

    public static final String NOUN = "noun";
    public static final String NUMBER = "number";
    public static final String ENGLISH = "english";

    private boolean useNoun = false;
    private boolean useNumber = false;
    private boolean useEnglish = false;

    protected AnalysisSettings() {
    }

    public static AnalysisSettings build(KeywordAnalyzerConfig config) {
        AnalysisSettings settings = new AnalysisSettings();

        for (String type : config.getAnalysisTypes()) {
            settings.setType(type);
        }

        return settings;
    }

    public void setType(String type) {
        switch (type.toLowerCase(Locale.ENGLISH)) {
            case NOUN:
                useNoun();
                break;
            case NUMBER:
                useNumber();
                break;
            case ENGLISH:
                useEnglish();
                break;
        }
    }

    public boolean isNounUsed() {
        return useNoun;
    }

    public AnalysisSettings useNoun() {
        useNoun = true;
		poses.add(NNG);
		poses.add(NNP);
        return this;
    }

    public boolean isNumberUsed() {
        return useNumber;
    }

    public AnalysisSettings useNumber() {
        useNumber = true;
		poses.add(SN);
        return this;
    }

    public boolean isEnglishUsed() {
        return useEnglish;
    }

    public AnalysisSettings useEnglish() {
        useEnglish = true;
		poses.add(SL);
        return this;
    }

	public boolean isWantedType(CharSequence pos) {
		return equalsIn(pos, poses);
	}

}
