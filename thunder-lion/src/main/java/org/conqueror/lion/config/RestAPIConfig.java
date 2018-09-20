package org.conqueror.lion.config;

import com.typesafe.config.Config;


public class RestAPIConfig extends NodeConfig {

    private final long messageTimeout;

    private final String restAPIHost;
    private final int restAPIPort;

    public RestAPIConfig(Config config) {
        super(config);

        messageTimeout = getLongFromConfig(config, "akka.message.timeout", 60000L);
        restAPIHost = getStringFromConfig(config, "service.rest.api.host", "localhost");
        restAPIPort = getIntegerFromConfig(config, "service.rest.api.port", 8131);
    }

    public long getMessageTimeout() {
        return messageTimeout;
    }

    public String getRestAPIHost() {
        return restAPIHost;
    }

    public int getRestAPIPort() {
        return restAPIPort;
    }

}
