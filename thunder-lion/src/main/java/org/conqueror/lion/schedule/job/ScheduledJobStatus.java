package org.conqueror.lion.schedule.job;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class ScheduledJobStatus implements ThunderSerializable<ScheduledJobStatus>, Cloneable {

    private static final ScheduledJobStatus EmptyScheduledJobStatus = new ScheduledJobStatus();

    public enum JobStatus {IDLE, RUNNING, PAUSED}

    public static final JobStatus IdleStatus = JobStatus.IDLE;
    public static final JobStatus RunningStatus = JobStatus.RUNNING;
    public static final JobStatus PausedStatus = JobStatus.PAUSED;

    private JobStatus jobStatus = IdleStatus;

    public ScheduledJobStatus() {
    }

    private ScheduledJobStatus(JobStatus status) {
        this.jobStatus = status;
    }

    public void changeJobStatus(JobStatus status) {
        jobStatus = status;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public boolean isIdle() {
        return getJobStatus().equals(IdleStatus);
    }

    public boolean isRunning() {
        return getJobStatus().equals(RunningStatus);
    }

    public boolean isPaused() {
        return getJobStatus().equals(PausedStatus);
    }

    @Override
    public ScheduledJobStatus clone() throws CloneNotSupportedException {
        super.clone();
        return new ScheduledJobStatus(getJobStatus());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobStatus == null) ? 0 : jobStatus.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getJobStatus().name());
        } catch (IOException e) {
            throw new SerializableException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public ScheduledJobStatus readObject(DataInput input) throws SerializableException {
        try {
            return new ScheduledJobStatus(JobStatus.valueOf(input.readUTF()));
        } catch (IOException e) {
            throw new SerializableException(e.getMessage(), e.getCause());
        }
    }

    public static ScheduledJobStatus getEmptyInstance() {
        return EmptyScheduledJobStatus;
    }

}
