package org.conqueror.cat.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Configuration;

import java.util.List;


public class KeywordAnalyzerConfig extends Configuration {

    private String analyzerConfFilePath = null;

    // dictionary
    private String dateTimeDicPath;
    private String stopwordDicPath;

    // refine option
    private boolean removeUrl;
    private boolean removeEmail;

    // analysis option
    private List<String> analysisTypes;

    public KeywordAnalyzerConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public KeywordAnalyzerConfig(Config config) {
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        setAnalyzerConfFilePath(getStringFromConfig(config, "analyzer.conf-file-path", true));

        removeUrl(getBooleanFromConfig(config, "analyzer.refine.remove-url", true));
        removeEmail(getBooleanFromConfig(config, "analyzer.refine.remove-email", true));

        setAnalysisTypes(getStringListFromConfig(config, "analyzer.analysis.types", true));
    }

    public String getAnalyzerConfFilePath() {
        return analyzerConfFilePath;
    }

    private void setAnalyzerConfFilePath(String analyzerConfFilePath) {
        this.analyzerConfFilePath = analyzerConfFilePath;
    }

    public boolean removeUrl() {
        return removeUrl;
    }

    private void removeUrl(boolean removeUrl) {
        this.removeUrl = removeUrl;
    }

    public boolean removeEmail() {
        return removeEmail;
    }

    private void removeEmail(boolean removeEmail) {
        this.removeEmail = removeEmail;
    }

    public String getDateTimeDicPath() {
        return dateTimeDicPath;
    }

    public void setDateTimeDicPath(String dateTimeDicPath) {
        this.dateTimeDicPath = dateTimeDicPath;
    }

    public String getStopwordDicPath() {
        return stopwordDicPath;
    }

    public void setStopwordDicPath(String stopwordDicPath) {
        this.stopwordDicPath = stopwordDicPath;
    }

    public List<String> getAnalysisTypes() {
        return analysisTypes;
    }

    public void setAnalysisTypes(List<String> analysisTypes) {
        this.analysisTypes = analysisTypes;
    }

}
