package org.conqueror.lion.message;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.serialization.Serialization;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class TaskMasterMessage implements LionMessage {


    public static abstract class TaskMasterRequest extends TaskMasterMessage {

    }

    public static abstract class TaskMasterResponse extends TaskMasterMessage {

    }

    public static final class TaskMasterAssignRequest extends TaskMasterRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskMasterAssignRequest readObject(DataInput input) {
            return new TaskMasterAssignRequest();
        }

    }

    public static final class TaskMasterAssignResponse extends TaskMasterResponse {

        private final String id;
        private final String taskMasterIdentifier;

        public TaskMasterAssignResponse(String id, ActorRef taskMaster) {
            this.id = id;
            this.taskMasterIdentifier = Serialization.serializedActorPath(taskMaster);
        }

        public TaskMasterAssignResponse(String id, String taskMasterIdentifier) {
            this.id = id;
            this.taskMasterIdentifier = taskMasterIdentifier;
        }

        public String getId() {
            return id;
        }

        public ActorRef getTaskMaster(ActorSystem system) {
            return system.provider().resolveActorRef(taskMasterIdentifier);
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(id);
                output.writeUTF(taskMasterIdentifier);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public LionSerializable readObject(DataInput input) throws SerializableException {
            try {
                return new TaskMasterAssignResponse(input.readUTF(), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }
    }

    public static final class TaskManagerCreateRequest extends TaskMasterRequest {

        private final String jobName;

        private final JobConfig config;

        public TaskManagerCreateRequest(String jobName, JobConfig config) {
            this.jobName = jobName;
            this.config = config;
        }

        public String getJobName() {
            return jobName;
        }

        public JobConfig getConfig() {
            return config;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(jobName);
                LionSerializable.writeSerializableObject(output, config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public TaskManagerCreateRequest readObject(DataInput input) throws SerializableException {
            try {
                return new TaskManagerCreateRequest(input.readUTF(), LionSerializable.readSerializableObject(input));
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskManagerCreateResponse extends TaskMasterResponse {

        private final String taskManagerName;
        private final String taskManagerIdentifier;

        public TaskManagerCreateResponse(String taskManagerName, ActorRef taskManager) {
            this.taskManagerName = taskManagerName;
            this.taskManagerIdentifier = Serialization.serializedActorPath(taskManager);
        }

        public TaskManagerCreateResponse(String taskManagerName, String taskManagerIdentifier) {
            this.taskManagerName = taskManagerName;
            this.taskManagerIdentifier = taskManagerIdentifier;
        }

        public String getTaskManagerName() {
            return taskManagerName;
        }

        public ActorRef getTaskManager(ActorSystem system) {
            return system.provider().resolveActorRef(taskManagerIdentifier);
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(taskManagerName);
                output.writeUTF(taskManagerIdentifier);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public TaskManagerCreateResponse readObject(DataInput input) throws SerializableException {
            try {
                return new TaskManagerCreateResponse(input.readUTF(), input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static final class TaskManagerRemoveRequest extends TaskMasterRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskManagerRemoveRequest readObject(DataInput input) {
            return new TaskManagerRemoveRequest();
        }

    }

    public static final class TaskManagerRemoveAllRequest extends TaskMasterRequest {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public TaskManagerRemoveAllRequest readObject(DataInput input) {
            return new TaskManagerRemoveAllRequest();
        }

    }

}
