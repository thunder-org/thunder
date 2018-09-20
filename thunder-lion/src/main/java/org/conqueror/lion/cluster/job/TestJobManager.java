package org.conqueror.lion.cluster.job;

import akka.actor.ActorRef;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.TestJobConfig;
import org.conqueror.lion.message.JobManagerMessage;

import java.util.*;


public class TestJobManager extends JobManager<TestJobConfig> {

    public static class TestTaskAssignResponse extends JobManagerMessage.TaskAssignResponse {
        private String source;

        public TestTaskAssignResponse(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }

    }

    public Queue<String> sources = new ArrayDeque<>(10);

    public TestJobManager(JobConfig jobConfig, ActorRef master) {
        super(jobConfig, master);
    }

    @Override
    protected void prepareJob() {
        log().info("prepare");
        for (int num=1; num<=100; num++) {
            sources.add(String.valueOf(num));
        }
    }

    @Override
    protected void finishJob() {
        log().info("finish");
    }

    @Override
    protected JobManagerMessage.TaskAssignResponse assignTask(JobManagerMessage.TaskAssignRequest request) throws Exception {
        if (sources.isEmpty()) {
            return new JobManagerMessage.TaskAssignFinishResponse();
        } else {
            return new TestTaskAssignResponse(sources.poll());
        }
    }

}