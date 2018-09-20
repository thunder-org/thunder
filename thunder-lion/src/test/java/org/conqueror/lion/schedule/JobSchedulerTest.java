package org.conqueror.lion.schedule;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.job.JobID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class JobSchedulerTest {

    private JobScheduler scheduler;

    @Before
    public void setUp() throws Exception {
        scheduler = new JobScheduler("test-scheduler");
        scheduler.startup();
    }

    @Test
    public void name() throws JobScheduleException, InterruptedException {
        JobID jobID = new JobID(1);
//        scheduler.registerJob(TestJob.class, "0", jobID, "test-group", "test scheduled job");

        for (int num=0; num<10; num++) {
            System.out.println("state = " + scheduler.getJobState(jobID) + ", running = " + scheduler.isJobRunning(jobID));
            Thread.sleep(500L);
        }

//        System.out.println(scheduler.getJobDesc(jobID));
    }

    @After
    public void tearDown() throws Exception {
        scheduler.shutdown();
    }
}