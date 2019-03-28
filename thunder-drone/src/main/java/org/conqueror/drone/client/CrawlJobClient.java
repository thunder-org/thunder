package org.conqueror.drone.client;

import org.conqueror.drone.config.CrawlConfig;
import org.conqueror.lion.cluster.client.NodeClusterClient;
import org.conqueror.lion.message.ScheduleManagerMessage;


public class CrawlJobClient {

    public static void main(String[] args) {
        String clientConfigName = "G:\\workspace\\thunder\\data\\conf\\job\\client.conf";
        String jobConfigName = "G:\\workspace\\thunder\\data\\conf\\drone\\crawl.conf";
        if (args.length == 2) {
           clientConfigName = args[0];
           jobConfigName = args[1];
        }
        NodeClusterClient client = new NodeClusterClient(clientConfigName);
        client.askToMaster(new ScheduleManagerMessage.JobRegisterRequest(new CrawlConfig(jobConfigName)))
        .thenAccept(response -> client.close());
    }

}
