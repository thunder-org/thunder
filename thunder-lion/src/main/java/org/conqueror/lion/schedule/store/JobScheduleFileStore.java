package org.conqueror.lion.schedule.store;

import org.conqueror.lion.db.JobDatabase;
import org.conqueror.lion.db.table.ScheduledJobTable;
import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.JobScheduler.ScheduleType;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.conqueror.lion.schedule.job.ScheduledJobStatus.*;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;

import java.util.Collection;
import java.util.Objects;

import static org.conqueror.lion.schedule.job.ScheduledJobStatus.*;


public class JobScheduleFileStore implements JobScheduleStore {

    private final JobDatabase db;
    private ScheduledJobTable jobs = null;

    public JobScheduleFileStore(String filePath) {
        this.db = new JobDatabase(filePath, 1024 * 100);
    }

    @Override
    public void open() throws JobScheduleException {
        try {
            this.jobs = (ScheduledJobTable) db.openTable(ScheduledJobTable.class);
        } catch (DBException e) {
            throw new JobScheduleException(e);
        }
    }

    @Override
    public void close() {
        jobs.close();
    }

    @Override
    public void registerJob(JobID id, JobDetail job, Trigger trigger) throws JobScheduleException {
        ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        if (!jobs.insert(id, new ScheduledJobInfo(id, job, trigger, type, expression))) {
            throw new JobScheduleException("failed to register job - " + id);
        }
        db.commit();
    }

    @Override
    public void updateJob(JobID id, JobDetail job, Trigger trigger) throws JobScheduleException {
        ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        if (!jobs.update(id, new ScheduledJobInfo(id, job, trigger, type, expression))) {
            throw new JobScheduleException("failed to update job - " + id);
        }
        db.commit();
    }

    @Override
    public void removeJob(JobID id) throws JobScheduleException {
        if (!jobs.delete(id)) {
            throw new JobScheduleException("not registered job - " + id);
        }
        db.commit();
    }

    @Override
    public void removeAllJobs() throws JobScheduleException {
        for (JobID id : jobs.keys()) {
            if (!jobs.delete(id)) {
                throw new JobScheduleException("not registered job - " + id);
            }
        }
        db.commit();
    }

    @Override
    public Collection<ScheduledJobInfo> getRegisteredJobs() {
        return jobs.values();
    }

    @Override
    public Collection<JobID> getJobIDs() {
        return jobs.keys();
    }

    @Override
    public JobKey getJobKey(JobID id) {
        ScheduledJobInfo info = jobs.find(id);
        return Objects.nonNull(info) ? info.getJobKey() : null;
    }

    @Override
    public ScheduledJobStatus getJobStatus(JobID id) {
        ScheduledJobInfo info = jobs.find(id);
        return Objects.nonNull(info) ? info.getStatus() : null;
    }

    @Override
    public ScheduledJobInfo getJobInfo(JobID id) {
        return jobs.find(id);
    }

    @Override
    public void changeToIdle(JobID jobID) {
        changeStatus(jobID, IdleStatus);
    }

    @Override
    public void changeToRunning(JobID jobID) {
        changeStatus(jobID, RunningStatus);
    }

    @Override
    public void changeToPaused(JobID jobID) {
        changeStatus(jobID, PausedStatus);
    }

    private void changeStatus(JobID id, JobStatus status) {
        ScheduledJobInfo info = jobs.find(id);
        if (Objects.nonNull(info)) {
            info.getStatus().changeJobStatus(status);
            jobs.update(id, info);
        }
        db.commit();
    }

    private void changeStatusAll(JobStatus status) {
        for (JobID id : jobs.keys()) {
            ScheduledJobInfo info = jobs.find(id);
            if (Objects.nonNull(info)) {
                info.getStatus().changeJobStatus(status);
                jobs.update(id, info);
            }
        }
        db.commit();
    }

}
