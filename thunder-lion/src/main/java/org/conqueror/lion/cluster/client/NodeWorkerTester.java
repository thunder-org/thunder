package org.conqueror.lion.cluster.client;

import org.conqueror.lion.config.ClientConfig;
import org.conqueror.lion.message.LionMessage;

import java.io.DataInput;
import java.io.DataOutput;


public class NodeWorkerTester {

    private static final class TestNodeWorkerMessage implements LionMessage {

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TestNodeWorkerMessage;
        }

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TestNodeWorkerMessage readObject(DataInput input) {
            return new TestNodeWorkerMessage();
        }

        @Override
        public String toString() {
            return "hi";
        }
    }

    private final ClientConfig config;

    private NodeWorkerTester(ClientConfig config) {
        this.config = config;
    }

    private void sendToMaster(LionMessage message) {
        try (NodeClusterClient clusterClient = new NodeClusterClient(new ClientConfig(config.getConfig()))) {
            clusterClient.askToMaster(message);
        }
    }

    private static void start(String configBaseName) {
        ClientConfig config = new ClientConfig(configBaseName);

        System.out.printf("client : %s:%d\n", config.getHost(), config.getPort());

        NodeWorkerTester tester = new NodeWorkerTester(config);
        tester.sendToMaster(new TestNodeWorkerMessage());
    }

    public static void main(String[] args) {
        NodeWorkerTester.start("G:\\workspace\\thunder\\thunder-lion\\src\\main\\resources\\client.conf");
    }

}
