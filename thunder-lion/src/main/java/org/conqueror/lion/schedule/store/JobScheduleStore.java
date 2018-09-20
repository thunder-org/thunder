package org.conqueror.lion.schedule.store;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.conqueror.lion.schedule.job.ScheduledJobStatus.JobStatus;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;

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

	void changeStatus(JobID jobID, JobStatus status);
	void changeStatusAll(JobStatus status);

}
