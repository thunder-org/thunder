package org.conqueror.lion.job;

import com.typesafe.config.Config;
import org.conqueror.lion.config.JobConfig;


public final class TestJobConfig extends JobConfig {

    private String value;

    public TestJobConfig(Config config) {
        super(config);
        this.value = getStringFromConfig(config, "job.value", "test-value");
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());

        return result;
    }

}
