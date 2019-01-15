package org.conqueror.lion.cluster.task;

import akka.actor.ActorRef;
import org.conqueror.lion.cluster.job.TestJobManager;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.TestJobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;

import java.io.DataInput;
import java.io.DataOutput;


public class TestTaskWorker extends TaskWorker<TestJobConfig, TestJobManager.TestTaskAssignResponse> {

    public static class TestTaskAssignRequest extends JobManagerMessage.TaskAssignRequest {

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public TestTaskAssignRequest readObject(DataInput input) throws SerializableException {
            return new TestTaskAssignRequest();
        }

    }

    public TestTaskWorker(JobConfig config, ActorRef taskManager) {
        super(config, taskManager);
    }

    @Override
    protected void work(JobManagerMessage.TaskAssignResponse source) throws Exception {
        System.out.println(source);
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected JobManagerMessage.TaskAssignRequest createTaskAssignRequest() {
        return new TestTaskAssignRequest();
    }

}
