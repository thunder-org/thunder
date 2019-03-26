package org.conqueror.lion.cluster.job;

import akka.actor.ActorRef;
import org.conqueror.lion.config.TestJobConfig;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;
import org.conqueror.common.serialize.ThunderSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;


public class TestJobManager extends JobManager<TestJobConfig> {

    public static class TestTaskAssignResponse extends JobManagerMessage.TaskAssignResponse {

        private String source;

        public TestTaskAssignResponse() {
            this(null);
        }

        public TestTaskAssignResponse(String source) {
            this.source = source;
        }

        public String getSource() {
            return source;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(source);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public ThunderSerializable readObject(DataInput input) throws SerializableException {
            try {
                return new TestTaskAssignResponse(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public String toString() {
            return "TestTaskAssignResponse{" +
                "source='" + source + '\'' +
                '}';
        }

    }

    public Queue<String> sources = new ArrayDeque<>(10);

    public TestJobManager(TestJobConfig jobConfig, ActorRef master) {
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
