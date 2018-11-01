package org.conqueror.lion.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.IOException;


public class TestJobConfig extends JobConfig<TestJobConfig> {

    public TestJobConfig() {
        super();
    }

    public TestJobConfig(String configFile) {
        super(configFile);
    }

    public TestJobConfig(Config config) {
        super(config);
    }

    @Override
    public TestJobConfig readObject(DataInput input) throws SerializableException {
        try {
            return new TestJobConfig(ConfigFactory.parseString(input.readUTF()));
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
