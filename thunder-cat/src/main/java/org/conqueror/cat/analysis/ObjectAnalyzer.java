package org.conqueror.cat.analysis;

import org.conqueror.common.utils.config.Configuration;


public abstract class ObjectAnalyzer {

    protected final Configuration config;

    public ObjectAnalyzer(Configuration config) {
        this.config = config;
    }

    public Configuration getConfig() {
        return config;
    }

}
