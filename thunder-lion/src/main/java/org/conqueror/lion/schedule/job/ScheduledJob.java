package org.conqueror.lion.schedule.job;

import org.conqueror.lion.job.JobID;
import org.quartz.*;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class ScheduledJob implements InterruptableJob {

    public static final String SchedulerNameKey = "SCHEDULER_NAME";
    public static final String JobIDKey = "JOB_ID";

    private AtomicBoolean isInterrupted = new AtomicBoolean(false);

    protected abstract void before(JobID jobID, JobDataMap data);

    protected abstract void doJob(JobID jobID, JobDataMap data);

    protected abstract void after(JobID jobID, JobDataMap data);

    public final void execute(JobExecutionContext ctx) {
        JobDataMap data = ctx.getJobDetail().getJobDataMap();
        JobID jobID = (JobID) data.get(JobIDKey);
        try {
            before(jobID, data);
            doJob(jobID, data);
        } finally {
            after(jobID, data);
        }
    }

    public final void interrupt() {
        isInterrupted.set(true);
        Thread thisThread = Thread.currentThread();
        if (isInterrupted() && !thisThread.isInterrupted()) {
            thisThread.interrupt();
        }
    }

    public boolean isInterrupted() {
        return this.isInterrupted.get();
    }

}