package org.conqueror.lion.config;

import akka.util.Timeout;
import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NodeConfig extends LionConfig {

    private Timeout issueIDTimeout;

    private String clusterName;
    private List<String> seeds;

    public NodeConfig() {
        super(null, null, null);
    }

    public NodeConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public NodeConfig(File configFile) {
        this(ConfigLoader.load(configFile));
    }

    public NodeConfig(Config config) {
        super(config);

        buildConfig(config);
    }

    private void buildConfig(Config config) {
        issueIDTimeout = new Timeout(getIntegerFromConfig(config, "node.master.id-issuer.timeout", 10), TimeUnit.SECONDS);
        clusterName = getStringFromConfig(config, "node.cluster.name", true);
        seeds = getStringListFromConfig(config, "akka.cluster.seed-nodes", true);
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
