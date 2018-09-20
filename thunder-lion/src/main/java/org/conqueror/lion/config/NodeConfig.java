package org.conqueror.lion.config;

import akka.util.Timeout;
import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class NodeConfig extends LionConfig {

    private final String redisHost;
    private final int redisPort;

    private final Timeout issueIDTimeout;

    private final String clusterName;
    private final List<String> seeds;

    public NodeConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public NodeConfig(Config config) {
        super(config);

        redisHost = getStringFromConfig(config, "node.redis.host", null);
        redisPort = getIntegerFromConfig(config, "node.redis.port", 0);

        issueIDTimeout = new Timeout(getIntegerFromConfig(config, "node.master.id-issuer.timeout", 10), TimeUnit.SECONDS);
        clusterName = getStringFromConfig(config, "node.cluster.name", true);
        seeds = getStringListFromConfig(config, "akka.cluster.seed-nodes", true);
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public Timeout getIssueIDTimeout() {
        return issueIDTimeout;
    }

    public String getClusterName() {
        return clusterName;
    }

    public List<String> getSeeds() {
        return seeds;
    }

}
