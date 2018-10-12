package org.conqueror.lion.message;

import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;

import java.io.DataInput;
import java.io.DataOutput;


public abstract class ScheduleManagerMessage implements LionMessage {

    public static abstract class ScheduleManagerRequest extends ScheduleManagerMessage {

    }

    public static abstract class ScheduleManagerResponse extends ScheduleManagerMessage {

    }

    public static class JobRegisterRequest extends ScheduleManagerRequest {

        private final JobConfig config;

        public JobRegisterRequest(JobConfig config) {
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
        public JobRegisterRequest readObject(DataInput input) throws SerializableException {
            JobConfig config = LionSerializable.readSerializableObject(input);
            return new JobRegisterRequest(config);
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

        public JobRemoveRequest(JobConfig config) {
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
        public JobRemoveRequest readObject(DataInput input) throws SerializableException {
            JobConfig config = LionSerializable.readSerializableObject(input);
            return new JobRemoveRequest(config);
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
