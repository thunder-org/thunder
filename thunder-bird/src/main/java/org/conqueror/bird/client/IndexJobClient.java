package org.conqueror.bird.client;

import org.conqueror.bird.config.IndexConfig;
import org.conqueror.lion.cluster.client.NodeClusterClient;
import org.conqueror.lion.message.ScheduleManagerMessage;


public class IndexJobClient {

    public static void main(String[] args) {
        String clientConfigName = "G:\\workspace\\thunder\\data\\conf\\job\\client.conf";
        String jobConfigName = "G:\\workspace\\thunder\\data\\conf\\job\\index.conf";
        NodeClusterClient client = new NodeClusterClient(clientConfigName);
        client.askToMaster(new ScheduleManagerMessage.JobRegisterRequest(new IndexConfig(jobConfigName)))
//        client.askToMaster(new ScheduleManagerMessage.JobRemoveRequest(new IndexConfig(jobConfigName)))
        .thenAccept(response -> client.close());
    }

}
