package org.conqueror.lion.message;

import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class ScheduleManagerMessage implements LionMessage {

    public static abstract class ScheduleManagerRequest extends ScheduleManagerMessage {

    }

    public static abstract class ScheduleManagerResponse extends ScheduleManagerMessage {

    }

    public static class JobRegisterRequest extends ScheduleManagerRequest {

        private final JobConfig config;

        public JobRegisterRequest() {
            this(null);
        }

        public JobRegisterRequest(JobConfig config) {
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
        public JobRegisterRequest readObject(DataInput input) throws SerializableException {
            String className = null;
            try {
                className = input.readUTF();
                JobConfig config = (JobConfig) Class.forName(className).newInstance();
                return new JobRegisterRequest((JobConfig) config.readObject(input));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
                throw new SerializableException("class : " + className, e.getCause());
            }
        }
    }

    public static class JobRegisterResponse extends ScheduleManagerResponse {

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public JobRegisterResponse readObject(DataInput input) throws SerializableException {
            return new JobRegisterResponse();
        }

    }

    public static class JobRemoveRequest extends ScheduleManagerRequest {

        private final JobConfig config;

        public JobRemoveRequest() {
            this(null);
        }

        public JobRemoveRequest(JobConfig config) {
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
        public JobRemoveRequest readObject(DataInput input) throws SerializableException {
            String className = null;
            try {
                className = input.readUTF();
                JobConfig config = (JobConfig) Class.forName(className).newInstance();
                return new JobRemoveRequest((JobConfig) config.readObject(input));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
                throw new SerializableException("class : " + className, e);
            }
        }
    }

    public static class JobRemoveResponse extends ScheduleManagerResponse {

        @Override
        public void writeObject(DataOutput output) throws SerializableException {

        }

        @Override
        public JobRemoveResponse readObject(DataInput input) throws SerializableException {
            return new JobRemoveResponse();
        }

    }

}
