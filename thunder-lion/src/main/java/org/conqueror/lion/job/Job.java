package org.conqueror.lion.job;

import org.conqueror.lion.config.JobConfig;

import java.lang.reflect.InvocationTargetException;

public abstract class Job {

	protected final JobConfig config;
//	private JobDataTable table = null;

	public Job(JobConfig config) {
		this.config = config;
	}

	public static Job newInstance(Class<? extends Job> jobClass, JobConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		return jobClass.getDeclaredConstructor(JobConfig.class).newInstance(config);
	}

	public JobConfig getConfig() {
		return config;
	}

	public JobID getJobID() {
		return config.getJobID();
	}

	public String getName() {
		return config.getName();
	}

	public String getSchedule() {
		return config.getSchedule();
	}

	public boolean isScheduled() {
		return config.hasSchedule();
	}

	protected void addJobData(String key, JobData data) {
//		if (table != null) table.addJobData(getName(), key, data);
	}

	protected JobData getJobData(String key) {
//		if (table != null) return table.getJobData(getName(), key);
		return null;
	}

}
