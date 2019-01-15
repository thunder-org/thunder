package org.conqueror.cat.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Configuration;


public class NGramConfig extends Configuration {

    private int numberOfGrams = 0;

    public NGramConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public NGramConfig(Config config) {
        buildConfig(config);
    }

    private void buildConfig(Config config) {
        setNumberOfGrams(getIntegerFromConfig(config, "ngrams.number", true));
    }

    public void setNumberOfGrams(int numberOfGrams) {
        this.numberOfGrams = numberOfGrams;
    }

    public int getNumberOfGrams() {
        return numberOfGrams;
    }

}
