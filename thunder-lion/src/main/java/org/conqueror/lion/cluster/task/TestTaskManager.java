package org.conqueror.lion.cluster.task;

import akka.actor.ActorRef;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.TestJobConfig;


public class TestTaskManager extends TaskManager<TestJobConfig> {

    public TestTaskManager(JobConfig config, ActorRef master, ActorRef jobManager) {
        super(config, master, jobManager);
    }

    @Override
    protected void prepareJob() throws Exception {
        log().info("prepare");
    }

    @Override
    protected void finishJob() throws Exception {
        log().info("finish");
    }

}
