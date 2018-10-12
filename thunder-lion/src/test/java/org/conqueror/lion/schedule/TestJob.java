package org.conqueror.lion.schedule;

import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJob;
import org.quartz.JobDataMap;

import java.util.Date;


public class TestJob extends ScheduledJob {

    @Override
    protected void before(JobID jobID, JobDataMap data) {

    }

    @Override
    protected void doJob(JobID jobID, JobDataMap data) {
        System.out.printf("test job executed - %s\n", new Date());
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void after(JobID jobID, JobDataMap data) {

    }

}
