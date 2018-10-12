package org.conqueror.lion.schedule.store;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.JobScheduler;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.quartz.*;

import java.util.Collection;


public interface JobScheduleStore {

    void open() throws JobScheduleException;

    void close() throws JobScheduleException;

    void registerJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException;

    void updateJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException;

    void removeJob(JobID jobID) throws JobScheduleException;

    void removeAllJobs() throws JobScheduleException;

    Collection<ScheduledJobInfo> getRegisteredJobs();

    Collection<JobID> getJobIDs();

    JobKey getJobKey(JobID id);

    ScheduledJobStatus getJobStatus(JobID jobID);

    ScheduledJobInfo getJobInfo(JobID jobID);

    void changeToIdle(JobID jobID);

    void changeToRunning(JobID jobID);

    void changeToPaused(JobID jobID);

    default JobScheduler.ScheduleType getType(Trigger trigger) throws JobScheduleException {
        if (trigger instanceof CronTrigger) {
            return JobScheduler.ScheduleType.CRON;
        } else if (trigger instanceof SimpleTrigger) {
            if (((SimpleTrigger) trigger).getRepeatCount() == 0) {
                return JobScheduler.ScheduleType.ONE;
            } else {
                return JobScheduler.ScheduleType.INTERVAL;
            }
        } else {
            throw new JobScheduleException("wrong cron expression");
        }
    }

    default String getExpression(Trigger trigger, JobScheduler.ScheduleType type) throws JobScheduleException {
        if (type.equals(JobScheduler.ScheduleType.CRON)) {
            return ((CronTrigger) trigger).getCronExpression();
        } else if (type.equals(JobScheduler.ScheduleType.ONE) || type.equals(JobScheduler.ScheduleType.INTERVAL)) {
            return String.valueOf(((SimpleTrigger) trigger).getRepeatInterval());
        } else {
            throw new JobScheduleException("wrong cron expression");
        }
    }

}
