package org.conqueror.bird.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.lion.config.NodeConfig;


public class BirdNodeConfig extends NodeConfig {

    private String apiHttpServiceClass;
    private String apiHttpServiceHost;
    private String apiHttpServicePort;

    public BirdNodeConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public BirdNodeConfig(Config config) {
        super(config);

        buildConfig(config);
    }

    private void buildConfig(Config config) {
        setApiHttpServiceClass(getStringFromConfig(config, "node.api.http.service.class", true));
        setApiHttpServiceHost(getStringFromConfig(config, "node.api.http.service.host", true));
        setApiHttpServicePort(getStringFromConfig(config, "node.api.http.service.port", true));
    }

    public String getApiHttpServiceClass() {
        return apiHttpServiceClass;
    }

    public void setApiHttpServiceClass(String apiHttpServiceClass) {
        this.apiHttpServiceClass = apiHttpServiceClass;
    }

    public String getApiHttpServiceHost() {
        return apiHttpServiceHost;
    }

    public void setApiHttpServiceHost(String apiHttpServiceHost) {
        this.apiHttpServiceHost = apiHttpServiceHost;
    }

    public String getApiHttpServicePort() {
        return apiHttpServicePort;
    }

    public void setApiHttpServicePort(String apiHttpServicePort) {
        this.apiHttpServicePort = apiHttpServicePort;
    }

}
