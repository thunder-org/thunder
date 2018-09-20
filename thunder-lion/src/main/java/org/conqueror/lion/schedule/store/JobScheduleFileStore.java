package org.conqueror.lion.schedule.store;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.job.JobID;
import org.conqueror.lion.db.JobDatabase;
import org.conqueror.lion.db.table.ScheduledJobTable;
import org.conqueror.lion.schedule.JobScheduler.ScheduleType;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.conqueror.lion.schedule.job.ScheduledJobStatus.JobStatus;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.util.Collection;
import java.util.Objects;

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
	public void changeStatus(JobID id, JobStatus status) {
		ScheduledJobInfo info = jobs.find(id);
		if (Objects.nonNull(info)) {
			info.getStatus().changeJobStatus(status);
			jobs.update(id, info);
		}
		db.commit();
	}

	@Override
	public void changeStatusAll(JobStatus status) {
		for (JobID id : jobs.keys()) {
			ScheduledJobInfo info = jobs.find(id);
			if (Objects.nonNull(info)) {
				info.getStatus().changeJobStatus(status);
				jobs.update(id, info);
			}
		}
		db.commit();
	}

	private ScheduleType getType(Trigger trigger) throws JobScheduleException {
		if (trigger instanceof CronTrigger) {
			return ScheduleType.CRON;
		} else if (trigger instanceof SimpleTrigger) {
			if (((SimpleTrigger) trigger).getRepeatCount() == 0) {
				return ScheduleType.ONE;
			} else {
				return ScheduleType.INTERVAL;
			}
		} else {
			throw new JobScheduleException("wrong cron expression");
		}
	}

	private String getExpression(Trigger trigger, ScheduleType type) throws JobScheduleException {
		if (type.equals(ScheduleType.CRON)) {
			return ((CronTrigger) trigger).getCronExpression();
		} else if (type.equals(ScheduleType.ONE) || type.equals(ScheduleType.INTERVAL)) {
			return String.valueOf(((SimpleTrigger) trigger).getRepeatInterval());
		} else {
			throw new JobScheduleException("wrong cron expression");
		}
	}

}
