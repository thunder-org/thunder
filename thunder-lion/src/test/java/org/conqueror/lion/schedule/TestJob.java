package org.conqueror.lion.schedule;

import org.conqueror.lion.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJob;
import org.quartz.JobDataMap;


public class TestJob extends ScheduledJob {

    @Override
    protected void before(JobID jobID, JobDataMap data) {

    }

    @Override
    protected void doJob(JobID jobID, JobDataMap data) {

    }

    @Override
    protected void after(JobID jobID, JobDataMap data) {

    }

}
