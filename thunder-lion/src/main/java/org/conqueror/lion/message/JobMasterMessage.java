package org.conqueror.lion.message;

import org.conqueror.lion.config.JobConfig;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class JobMasterMessage implements ThunderMessage {


    public static abstract class JobMasterRequest extends JobMasterMessage {

    }

    public static abstract class JobMasterResponse extends JobMasterMessage {

    }

    public static class JobManagerCreateRequest extends JobMasterRequest {

        private final JobConfig config;

        public JobManagerCreateRequest() {
            this(null);
        }

        public JobManagerCreateRequest(JobConfig config) {
            this.config = config;
        }

        public JobConfig getConfig() {
            return config;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(config.getClass().getName());
                config.writeObject(output);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public ThunderSerializable readObject(DataInput input) throws SerializableException {
            String className = null;
            try {
                className = input.readUTF();
                JobConfig config = (JobConfig) Class.forName(className).newInstance();
                return new JobManagerCreateRequest((JobConfig) config.readObject(input));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
                throw new SerializableException("class : " + className, e);
            }
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

        public JobManagerFinishRequest() {
            this(null);
        }

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
