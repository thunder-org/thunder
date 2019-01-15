package org.conqueror.lion.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.config.Configuration;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.serialize.LionSerializable;

import java.io.DataOutput;
import java.io.IOException;


public abstract class JobConfig<T extends JobConfig> extends Configuration implements LionSerializable<T> {

    public final Config config;

    private final String name;
    private final JobID jobID;
    private final String schedule;
    private final String group;
    private final String description;

    private final int maxNumberOfTaskManagers;
    private final int maxNumberOfTaskWorkers;

    private final String jobManagerClass;
    private final String taskManagerClass;
    private final String taskWorkerClass;

    private final int watingTimeSec;

    public JobConfig() {
        config = null;
        name = null;
        jobID = null;
        schedule = null;
        group = null;
        description = null;
        maxNumberOfTaskManagers = 0;
        maxNumberOfTaskWorkers = 0;
        jobManagerClass = null;
        taskManagerClass = null;
        taskWorkerClass = null;
        watingTimeSec = 1;
    }

    public JobConfig(String configFile) {
        this(ConfigLoader.load(configFile));
    }

    public JobConfig(Config config) {
        this.config = config;

        name = getStringFromConfig(config, "job.name", true);
        jobID = new JobID(getIntegerFromConfig(config, "job.id", true));
        schedule = getStringFromConfig(config, "job.schedule", "0");
        group = getStringFromConfig(config, "job.group", false);
        description = getStringFromConfig(config, "job.description", false);
        maxNumberOfTaskManagers = getIntegerFromConfig(config, "job.task.manager.number", 1);
        maxNumberOfTaskWorkers = getIntegerFromConfig(config, "job.task.worker.number", 1);

        jobManagerClass = getStringFromConfig(config, "job.job-manager.class", true);
        taskManagerClass = getStringFromConfig(config, "job.task-manager.class", true);
        taskWorkerClass = getStringFromConfig(config, "job.task-worker.class", true);

        watingTimeSec = getIntegerFromConfig(config, "wating-time-sec", 3);
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

    public String getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
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

    public int getWatingTimeSec() {
        return watingTimeSec;
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
        result = prime * result + watingTimeSec;

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(config.root().render(ConfigRenderOptions.concise()));
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
