package org.conqueror.lion.schedule;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JobSchedulerFactory {

    private static final JobSchedulerFactory FACTORY = new JobSchedulerFactory();

    private final Map<String, JobScheduler> schedulers = new ConcurrentHashMap<>();

    private JobSchedulerFactory() {
    }

    public static JobSchedulerFactory getInstance() {
        return FACTORY;
    }

    public JobScheduler createScheduler(String name) throws JobScheduleException {
        if (schedulers.containsKey(name)) {
            throw new JobScheduleException("exist a scheduler with same name");
        }
        JobScheduler scheduler = new JobScheduler(name);
        schedulers.put(name, scheduler);

        return scheduler;
    }

    public JobScheduler getScheduler(String name) {
        return schedulers.get(name);
    }

    public JobScheduler getOrCreateScheduler(String name) throws JobScheduleException {
        JobScheduler scheduler = getScheduler(name);
        return scheduler != null? scheduler : createScheduler(name);
    }

    public void removeScheduler(String name) throws JobScheduleException {
        JobScheduler scheduler = schedulers.remove(name);
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

}
