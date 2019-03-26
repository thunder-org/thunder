package org.conqueror.lion.config;

import com.typesafe.config.Config;


public class HttpServiceConfig extends LionConfig {

    private static final String MESSAGE_TIMEOUT = "node.api.http.message.timeout";

    private static final String API_SERVICE_HTTP_HOST = "node.api.http.service.host";
    private static final String API_SERVICE_HTTP_PORT = "node.api.http.service.port";

    private static final String API_SERVICE_CLASS = "node.api.http.service.class";

    private long messageTimeout;

    private String httpHost;
    private int httpPort;
    private String apiServiceClass;

    public HttpServiceConfig(Config config) {
        super(config);

        buildConfig(config);
    }

    private void buildConfig(Config config) {
        messageTimeout = getLongFromConfig(config, MESSAGE_TIMEOUT, 60000L);

        httpHost = getStringFromConfig(config, API_SERVICE_HTTP_HOST, "localhost");
        httpPort = getIntegerFromConfig(config, API_SERVICE_HTTP_PORT, 8080);

        apiServiceClass = getStringFromConfig(config, API_SERVICE_CLASS, false);
    }

    public long getMessageTimeout() {
        return messageTimeout;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getApiServiceClass() {
        return apiServiceClass;
    }

}
