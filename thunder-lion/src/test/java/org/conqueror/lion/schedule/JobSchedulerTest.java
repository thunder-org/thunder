package org.conqueror.lion.schedule;

import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import com.typesafe.config.ConfigFactory;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.store.JobScheduleDDataStore;
import org.conqueror.lion.schedule.store.JobScheduleStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JobSchedulerTest {

    private JobScheduler scheduler;

    @Before
    public void setUp() throws Exception {
        ActorSystem system = ActorSystem.create("ClusterSystem", ConfigFactory.parseString(
            "akka {\n" +
                "  actor {\n" +
                "    provider = \"akka.cluster.ClusterActorRefProvider\"\n" +
                "  }\n" +
                "  remote {\n" +
                "    log-remote-lifecycle-events = off\n" +
                "    netty.tcp {\n" +
                "      hostname = \"127.0.0.1\"\n" +
                "      port = 2551\n" +
                "    }\n" +
                "  }\n" +
                "  cluster {\n" +
                "    seed-nodes = [\"akka.tcp://ClusterSystem@127.0.0.1:2551\"]\n" +
                "    distributed-data {\n" +
                "      name = ddataReplicator\n" +
                "      durable {\n" +
                "        keys = [\"*\"]\n" +
                "        store-actor-class = akka.cluster.ddata.LmdbDurableStore\n" +
                "        lmdb.dir = \"ddata\"\n" +
                "        lmdb.map-size = 100 kB\n" +
                "        lmdb.write-behind-interval = 200 ms\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"));
        Cluster cluster = Cluster.get(system);
        long timeoutSec = 10L;
        JobScheduleStore store = new JobScheduleDDataStore("test-scheduler-store", cluster, timeoutSec);
        scheduler = new JobScheduler("test-scheduler", store);
        scheduler.startup();
    }

    @Test
    public void test() throws JobScheduleException, InterruptedException {
        JobID jobID = new JobID(1);
        scheduler.registerJob(TestJob.class, "5", jobID, "test-group", "test scheduled job");
        System.out.println(scheduler.getJobIDs());

        for (int num = 0; num < 10; num++) {
            System.out.println("state = " + scheduler.getJobState(jobID) + ", running = " + scheduler.isJobRunning(jobID));
            Thread.sleep(500L);
        }

//        System.out.println(scheduler.getJobDesc(jobID));
    }

    @After
    public void tearDown() throws Exception {
        scheduler.shutdown();
    }
}