package org.conqueror.lion.schedule;

import org.quartz.*;


public class JobScheduleListener implements JobListener {

    private static final JobSchedulerFactory manager = JobSchedulerFactory.getInstance();

    private final String schedulerName;

    public JobScheduleListener(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    @Override
    public String getName() {
        return schedulerName;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext ctx) {
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext ctx, JobExecutionException exception) {
        try {
            Scheduler scheduler = ctx.getScheduler();
            TriggerKey key = ctx.getTrigger().getKey();
            if (scheduler.getTriggerState(key).equals(Trigger.TriggerState.NONE)) {
                scheduler.unscheduleJob(key);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
