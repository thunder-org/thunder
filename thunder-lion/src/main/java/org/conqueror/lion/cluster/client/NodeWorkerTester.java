package org.conqueror.lion.cluster.client;

import org.conqueror.lion.config.ClientConfig;
import org.conqueror.lion.message.LionMessage;


public class NodeWorkerTester {

    private final ClientConfig config;

    public NodeWorkerTester(ClientConfig config) {
        this.config = config;
    }

    private void sendToMaster(LionMessage message) {
        try (NodeClusterClient clusterClient = new NodeClusterClient(new ClientConfig(config.getConfig()))) {
            clusterClient.askToMaster(message);
        }
    }

    public static void start(String configBaseName) {
        ClientConfig config = new ClientConfig(configBaseName);

        System.out.printf("client : %s:%d\n", config.getHost(), config.getPort());

        NodeWorkerTester tester = new NodeWorkerTester(config);
        tester.sendToMaster(new LionMessage() {
            @Override
            public String toString() {
                return "hi";
            }
        });
    }

    public static void main(String[] args) {
        NodeWorkerTester.start("G:\\workspace\\thunder\\thunder-lion\\src\\main\\resources\\client.conf");
    }

}
