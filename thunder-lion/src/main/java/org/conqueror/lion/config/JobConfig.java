package org.conqueror.lion.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Configuration;
import org.conqueror.lion.job.JobID;


public abstract class JobConfig extends Configuration {

    public final Config config;
    private final String name;
    private final JobID jobID;
    private final String schedule;
    private final int maxNumberOfTaskManagers;
    private final int maxNumberOfTaskWorkers;

    private final String jobManagerClass;
    private final String taskManagerClass;
    private final String taskWorkerClass;

    public JobConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public JobConfig(Config config) {
        this.config = config;

        name = getStringFromConfig(config, "job.name", true);
        jobID = new JobID(getIntegerFromConfig(config, "job.id", true));
        schedule = getStringFromConfig(config, "job.schedule", false);
        maxNumberOfTaskManagers = getIntegerFromConfig(config, "job.task.manager.number", 3);
        maxNumberOfTaskWorkers = getIntegerFromConfig(config, "job.task.worker.number", 3);

        jobManagerClass = getStringFromConfig(config, "job.job-manager.class", true);
        taskManagerClass = getStringFromConfig(config, "job.task-manager.class", true);
        taskWorkerClass = getStringFromConfig(config, "job.task-worker.class", true);
    }

    public String getName() {
        return name;
    }

    public JobID getJobID() {
        return jobID;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getMaxNumberOfTaskManagers() {
        return maxNumberOfTaskManagers;
    }

    public int getMaxNumberOfTaskWorkers() {
        return maxNumberOfTaskWorkers;
    }

    public String getJobManagerClass() {
        return jobManagerClass;
    }

    public String getTaskManagerClass() {
        return taskManagerClass;
    }

    public String getTaskWorkerClass() {
        return taskWorkerClass;
    }

    public boolean hasSchedule() {
        return schedule != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((jobID == null) ? 0 : jobID.hashCode());
        result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
        result = prime * result + maxNumberOfTaskManagers;
        result = prime * result + maxNumberOfTaskWorkers;
        result = prime * result + ((jobManagerClass == null) ? 0 : jobManagerClass.hashCode());
        result = prime * result + ((taskManagerClass == null) ? 0 : taskManagerClass.hashCode());
        result = prime * result + ((taskWorkerClass == null) ? 0 : taskWorkerClass.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

}