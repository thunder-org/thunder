package org.conqueror.lion.system;

import org.conqueror.lion.cluster.node.ClusterNodeAgent;


public class NodeSystem {

    public static void start(String configBaseName) {
        ClusterNodeAgent agent = new ClusterNodeAgent();
        agent.startup(configBaseName);
    }

    public static void shutdown(String configBaseName) {
//        ClusterNodeAgent agent = new ClusterNodeAgent();
//        agent.shutdown(configBaseName);
    }

    public static void main(String[] args) {
        NodeSystem.start(args[0]);
    }

}
