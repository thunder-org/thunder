package org.conqueror.lion.config;

import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.conqueror.common.utils.config.Configuration;

import java.util.concurrent.TimeUnit;


public abstract class LionConfig extends Configuration {

    private final Config config;
    private final String host;
    private final Integer port;
    private final Timeout askTimeout;

    public LionConfig(Config config) {
        this.config = config;
        String thost = getStringFromConfig(config, "akka.remote.artery.canonical.hostname", false);
        Integer tport = getIntegerFromConfig(config, "akka.remote.artery.canonical.port", false);
        if (thost == null || thost.startsWith("<")) {
            thost = getStringFromConfig(config, "akka.remote.netty.tcp.hostname", false);
            tport = getIntegerFromConfig(config, "akka.remote.netty.tcp.port", false);
        }
        host = thost;
        port = tport;
        askTimeout = new Timeout(getIntegerFromConfig(config, "akka.ask.timeout", 10), TimeUnit.SECONDS);
    }

    protected LionConfig(String host, Integer port, Timeout askTimeout) {
        this.config = ConfigFactory.parseString(
            "akka.remote.artery.canonical.hostname = " + host
                + "akka.remote.artery.canonical.port = " + port
                + "akka.remote.artery.enabled = on"
                + "akka.ask.timeout = 10"
        );
        this.host = host;
        this.port = port;
        this.askTimeout = askTimeout;
    }

    public Config getConfig() {
        return config;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Timeout getAskTimeout() {
        return askTimeout;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((askTimeout == null) ? 0 : askTimeout.duration().hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());

        return result;
    }
}
