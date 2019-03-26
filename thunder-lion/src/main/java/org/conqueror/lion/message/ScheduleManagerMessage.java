package org.conqueror.lion.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.lion.config.JobConfig;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class ScheduleManagerMessage implements ThunderMessage {

    public static abstract class ScheduleManagerRequest extends ScheduleManagerMessage {

    }

    public static abstract class ScheduleManagerResponse extends ScheduleManagerMessage {

        private boolean succ;

        public ScheduleManagerResponse(boolean succ) {
            this.succ = succ;
        }

        public boolean isSucc() {
            return succ;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeBoolean(isSucc());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

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

        public JobRegisterResponse() {
            this(false);
        }

        @JsonCreator
        public JobRegisterResponse(@JsonProperty("succ") boolean succ) {
            super(succ);
        }

        @Override
        public JobRegisterResponse readObject(DataInput input) throws SerializableException {
            try {
                return new JobRegisterResponse(input.readBoolean());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
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

        public JobRemoveResponse() {
            this(false);
        }

        @JsonCreator
        public JobRemoveResponse(@JsonProperty("succ") boolean succ) {
            super(succ);
        }

        @Override
        public JobRemoveResponse readObject(DataInput input) throws SerializableException {
            try {
                return new JobRemoveResponse(input.readBoolean());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

}
