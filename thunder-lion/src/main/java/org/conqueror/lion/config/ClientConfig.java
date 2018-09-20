package org.conqueror.lion.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;

import java.util.List;


public class ClientConfig extends LionConfig {

    private final List<String> seeds;

    private final String clientName;

    public ClientConfig(String baseName) {
        this(ConfigLoader.load(baseName));
    }

    public ClientConfig(Config config) {
        super(config);
        seeds = getStringListFromConfig(config, "akka.cluster.client.initial-contacts", true);
        clientName = "Client-" + getHost().replaceAll("\\.", "") + "-" + getPort();
    }

    public List<String> getSeeds() {
        return seeds;
    }

    public String getClientName() {
        return clientName;
    }

}
