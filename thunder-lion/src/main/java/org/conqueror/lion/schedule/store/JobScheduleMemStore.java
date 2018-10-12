package org.conqueror.lion.schedule.store;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.schedule.JobScheduler;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class JobScheduleMemStore implements JobScheduleStore {

    private Map<JobID, ScheduledJobInfo> jobs = null;

    @Override
    public void open() {
        jobs = new HashMap<>();
    }

    @Override
    public void close() {
        jobs.clear();
    }

    @Override
    public void registerJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException {
        JobScheduler.ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        jobs.put(jobID, new ScheduledJobInfo(jobID, job, trigger, type, expression));
    }

    @Override
    public void updateJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException {
        JobScheduler.ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        jobs.put(jobID, new ScheduledJobInfo(jobID, job, trigger, type, expression));
    }

    @Override
    public void removeJob(JobID jobID) {
        jobs.remove(jobID);
    }

    @Override
    public void removeAllJobs() {
        jobs.clear();
    }

    @Override
    public Collection<ScheduledJobInfo> getRegisteredJobs() {
        return jobs.values();
    }

    @Override
    public Collection<JobID> getJobIDs() {
        return jobs.keySet();
    }

    @Override
    public JobKey getJobKey(JobID jobID) {
        return jobs.get(jobID).getJobKey();
    }

    @Override
    public ScheduledJobStatus getJobStatus(JobID jobID) {
        return jobs.get(jobID).getStatus();
    }

    @Override
    public ScheduledJobInfo getJobInfo(JobID jobID) {
        return jobs.get(jobID);
    }

    @Override
    public void changeToIdle(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.IdleStatus);
        jobs.put(jobID, info);
    }

    @Override
    public void changeToRunning(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.RunningStatus);
        jobs.put(jobID, info);
    }

    @Override
    public void changeToPaused(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.PausedStatus);
        jobs.put(jobID, info);
    }

}
