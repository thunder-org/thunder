package org.conqueror.bird.task;

import akka.actor.ActorRef;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.lion.cluster.task.TaskManager;
import org.conqueror.lion.config.JobConfig;


public class IndexTaskManager extends TaskManager<IndexConfig> {

    public IndexTaskManager(JobConfig config, ActorRef master, ActorRef jobManager) {
        super(config, master, jobManager);
    }

    @Override
    protected void prepareJob() throws Exception {

    }

    @Override
    protected void finishJob() throws Exception {

    }

}
