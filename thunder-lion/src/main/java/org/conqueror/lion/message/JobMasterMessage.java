package org.conqueror.lion.message;

import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class JobMasterMessage implements LionMessage {


    public static abstract class JobMasterRequest extends JobMasterMessage {

    }

    public static abstract class JobMasterResponse extends JobMasterMessage {

    }

    public static class JobManagerCreateRequest extends JobMasterRequest {

        private final JobConfig config;

        public JobManagerCreateRequest(JobConfig config) {
            this.config = config;
        }

        public JobConfig getConfig() {
            return config;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            LionSerializable.writeSerializableObject(output, config);
        }

        @Override
        public LionSerializable readObject(DataInput input) throws SerializableException {
            return new JobManagerCreateRequest(LionSerializable.readSerializableObject(input));
        }

    }

    public static class JobManagerCreateResponse extends JobMasterResponse {

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public JobManagerCreateResponse readObject(DataInput input) throws SerializableException {
            return new JobManagerCreateResponse();
        }

    }

    public static class JobManagerExistResponse extends JobManagerCreateResponse {

    }

    public static final class JobManagerFinishRequest extends JobMasterRequest {

        private final String jobName;

        public JobManagerFinishRequest(String jobName) {
            this.jobName = jobName;
        }

        public String getJobName() {
            return jobName;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(jobName);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public JobManagerFinishRequest readObject(DataInput input) throws SerializableException {
            try {
                return new JobManagerFinishRequest(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }
    }

}
