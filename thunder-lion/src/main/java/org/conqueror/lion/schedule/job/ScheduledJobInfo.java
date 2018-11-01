package org.conqueror.lion.schedule.job;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.schedule.JobScheduler.ScheduleType;
import org.conqueror.lion.serialize.LionSerializable;
import org.mapdb.Serializer;
import org.quartz.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static org.conqueror.lion.serialize.LionSerializable.readSerializableObject;
import static org.conqueror.lion.serialize.LionSerializable.writeSerializableObject;


public class ScheduledJobInfo implements LionSerializable<ScheduledJobInfo> {

    private static final ScheduledJobInfo EmptyScheduledJobInfo = new ScheduledJobInfo();

    private JobID jobID;
    private JobDetail jobDetail;
    private Trigger trigger;
    private ScheduledJobStatus status = new ScheduledJobStatus();
    private ScheduleType scheduleType;
    private String scheduleExpr;

    public ScheduledJobInfo() {
        this(null, null, null, null, null);
    }

    public ScheduledJobInfo(JobID jobID, JobDetail jobDetail, Trigger trigger, ScheduleType scheduleType, String scheduleExpr) {
        this.jobID = jobID;
        this.jobDetail = jobDetail;
        this.trigger = trigger;
        this.scheduleType = scheduleType;
        this.scheduleExpr = scheduleExpr;
    }

    public JobID getJobID() {
        return jobID;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public ScheduledJobStatus getStatus() {
        return status;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getScheduleExpr() {
        return scheduleExpr;
    }

    public String getName() {
        return jobDetail.getKey().getName();
    }

    public String getGroup() {
        return jobDetail.getKey().getGroup();
    }

    public String getDescription() {
        return jobDetail.getDescription();
    }

    public Class<? extends Job> getJobClass() {
        return jobDetail.getJobClass();
    }

    public JobDataMap getJobDataMap() {
        return jobDetail.getJobDataMap();
    }

    public JobKey getJobKey() {
        return jobDetail.getKey();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobID == null) ? 0 : jobID.hashCode());
        if (jobDetail != null) {
            result = prime * result + ((jobDetail.getKey() == null) ? 0 : jobDetail.getKey().hashCode());
            result = prime * result + ((jobDetail.getDescription() == null) ? 0 : jobDetail.getDescription().hashCode());
            result = prime * result + ((jobDetail.getJobClass() == null) ? 0 : jobDetail.getJobClass().getName().hashCode());
        }
        result = prime * result + ((scheduleType == null) ? 0 : scheduleType.hashCode());
        result = prime * result + ((scheduleExpr == null) ? 0 : scheduleExpr.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            getJobID().writeObject(output);
            writeSerializableObject(output, jobDetail);
            writeSerializableObject(output, trigger);
            output.writeUTF(getScheduleType().name());
            output.writeUTF(getScheduleExpr());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public ScheduledJobInfo readObject(DataInput input) throws SerializableException {
        try {
            return new ScheduledJobInfo(
                JobID.getEmptyInstance().readObject(input)
                , readSerializableObject(input)
                , readSerializableObject(input)
                , ScheduleType.valueOf(input.readUTF())
                , input.readUTF()
            );
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    public static Serializer<ScheduledJobInfo> getSerializer() {
        return LionSerializable.getSerializer(ScheduledJobInfo.class);
    }

    public static ScheduledJobInfo getEmptyScheduledJobInfo() {
        return EmptyScheduledJobInfo;
    }

}
