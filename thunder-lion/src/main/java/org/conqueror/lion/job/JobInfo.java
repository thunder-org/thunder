package org.conqueror.lion.job;


import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class JobInfo implements LionSerializable<JobInfo> {

    private static final JobInfo EmptyJobInfo = new JobInfo();

    private String jobMasterID;
    private Class<? extends Job> jobClass;
    private JobConfig config;

    private JobInfo() {
        this(null, null, null);
    }

    public JobInfo(String jobMasterID, Class<? extends Job> jobClass, JobConfig config) {
        this.jobMasterID = jobMasterID;
        this.jobClass = jobClass;
        this.config = config;
    }

    public String getJobMasterID() {
        return jobMasterID;
    }

    private void setJobMasterID(String jobMasterID) {
        this.jobMasterID = jobMasterID;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    private void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    public JobConfig getConfig() {
        return config;
    }

    private void setConfig(JobConfig config) {
        this.config = config;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobMasterID == null) ? 0 : jobMasterID.hashCode());
        result = prime * result + ((jobClass == null) ? 0 : jobClass.getName().hashCode());
        result = prime * result + ((config == null) ? 0 : config.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getJobMasterID());
            output.writeUTF(getJobClass().getCanonicalName());
            output.writeUTF(config.getClass().getName());
//            config.writeObject(output);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public JobInfo readObject(DataInput input) throws SerializableException {
        /*
        try {
            String masterID = input.readUTF();
            Class jobClass = Class.forName(input.readUTF());
            Class<JobConfig> jobConfigClass = (Class<JobConfig>) Class.forName(input.readUTF());
            JobConfig jobConfig = (JobConfig) jobConfigClass.newInstance().readObject(input);

            return new JobInfo(masterID, jobClass, jobConfig);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new SerializableException(e);
        }
        */
        return null;
    }

    @Override
    public String toString() {
        return "JobInfo{" +
            "jobMasterID='" + jobMasterID + '\'' +
            ", jobClass=" + jobClass +
            ", config=" + config +
            '}';
    }

    public static Serializer<JobInfo> getSerializer() {
        return LionSerializable.getSerializer(JobInfo.class);
    }

    public static JobInfo getEmptyJobInfo() {
        return EmptyJobInfo;
    }

}
