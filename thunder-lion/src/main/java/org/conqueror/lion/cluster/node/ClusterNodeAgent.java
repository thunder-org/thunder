package org.conqueror.lion.cluster.node;

import akka.actor.ActorSystem;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import org.conqueror.lion.cluster.client.NodeClusterClient;
import org.conqueror.lion.config.ClientConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.NodeMasterMessage.NodeMasterShutdownRequest;
import org.conqueror.lion.message.NodeWorkerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.conqueror.lion.cluster.node.Path.*;
import static org.conqueror.lion.cluster.node.Role.NODE_MASTER_ROLE;


public class ClusterNodeAgent {

    private static final Logger logger = LoggerFactory.getLogger(ClusterNodeAgent.class);

    public void startup(String configFile) {
        NodeConfig config = new NodeConfig(configFile);
        ActorSystem nodeClusterSystem = ActorSystem.create(config.getClusterName(), config.getConfig());

        createClusterListener(nodeClusterSystem);
        createNodeMasterSingletonManager(config, nodeClusterSystem);
        createNodeWorker(config, nodeClusterSystem);
    }

    public void shutdownNodeMaster(String baseName) {
        ClientConfig config = new ClientConfig(baseName);
        NodeClusterClient clusterClient = new NodeClusterClient(config);
        clusterClient.askToMaster(new NodeMasterShutdownRequest())
            .exceptionally(exception -> new Exception("failed to shut down node-master", exception))
            .thenAccept(response -> {
                logger.info("[ClusterNodeAgent] node-master shutdown");
                clusterClient.close();
            });
    }

    public void shutdownNodeWorker(String baseName) {
        ClientConfig config = new ClientConfig(baseName);
        try (NodeClusterClient clusterClient = new NodeClusterClient(config)) {
            clusterClient.askToMaster(new NodeWorkerMessage.NodeWorkerShutdownRequest())
                .exceptionally(exception -> new Exception("failed to shut down node-worker", exception))
                .thenAccept(response -> {
                    logger.info("[ClusterNodeAgent] node-worker shutdown");
                    clusterClient.close();
                });
        }
    }

    private void createClusterListener(final ActorSystem nodeClusterSystem) {
        nodeClusterSystem.actorOf(ClusterListener.props(), CLUSTER_LISTENER_NAME);
    }

    private void createNodeMasterSingletonManager(final NodeConfig config, final ActorSystem nodeClusterSystem) {
        nodeClusterSystem.actorOf(
            ClusterSingletonManager.props(
                NodeMaster.props(config)
                , new NodeMasterShutdownRequest()
                , ClusterSingletonManagerSettings.create(nodeClusterSystem)
                    .withRole(NODE_MASTER_ROLE)
                    .withSingletonName(SINGLETON_NAME))
            , NODE_MASTER_NAME);
    }

    private void createNodeWorker(final NodeConfig config, final ActorSystem nodeClusterSystem) {
        nodeClusterSystem.actorOf(NodeWorker.props(config), NODE_WORKER_NAME);
    }

}
