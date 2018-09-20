package org.conqueror.lion.cluster.client;

import akka.actor.ActorRef;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.TestJobConfig;
import org.conqueror.lion.message.JobMasterMessage;


public class TestJobClient {

    public static void main(String[] args) {
        String clientConfigName = "G:\\workspace\\thunder\\thunder-lion\\src\\main\\resources\\client.conf";
        String jobConfigName = "G:\\workspace\\thunder\\thunder-lion\\src\\main\\resources\\test-job.conf";
        NodeClusterClient client = new NodeClusterClient(clientConfigName);
        client.askToMaster(new JobMasterMessage.JobManagerCreateRequest(new TestJobConfig(jobConfigName)))
        .thenAccept(response -> client.close());
    }

}
