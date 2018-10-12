package org.conqueror.lion.schedule;

import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJob;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.conqueror.lion.schedule.store.JobScheduleMemStore;
import org.conqueror.lion.schedule.store.JobScheduleStore;
import org.quartz.*;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@SuppressWarnings("unused")
public class JobScheduler extends ReentrantLock {

    public enum ScheduleType {CRON, INTERVAL, ONE}

    private final String schedulerName;
    private final JobScheduleStore store;
    private final Scheduler scheduler;

    public JobScheduler(@Nonnull final String schedulerName) throws JobScheduleException {
        this(schedulerName, new JobScheduleMemStore());
    }

    public JobScheduler(@Nonnull final String schedulerName, @Nonnull final JobScheduleStore store) throws JobScheduleException {
        try {
            this.schedulerName = schedulerName;
            this.store = store;

            scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw new JobScheduleException(e);
        }
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    /*
     * start this scheduler
     */
    public void startup() throws JobScheduleException {
        lock();
        checkNotRunning();

        try {
            scheduler.standby();

            load(store);
            scheduler.getListenerManager().addJobListener(new JobScheduleListener(schedulerName));

            scheduler.start();
        } catch (SchedulerException e) {
            throw new JobScheduleException(e);
        } finally {
            unlock();
        }
    }

    /*
     * shutdown this scheduler
     */
    public void shutdown() throws JobScheduleException {
        lock();
        checkRunning();

        try {
            unload(store);
            scheduler.shutdown(false);
        } catch (SchedulerException e) {
            throw new JobScheduleException(e);
        } finally {
            unlock();
        }
    }

    private void load(JobScheduleStore store) throws JobScheduleException {
        store.open();

        for (ScheduledJobInfo jobInfo : store.getRegisteredJobs()) {
            JobDetail jobDetail = JobBuilder.newJob(jobInfo.getJobClass())
                .withIdentity(jobInfo.getName(), jobInfo.getGroup())
                .withDescription(jobInfo.getDescription())
                .usingJobData(jobInfo.getJobDataMap())
                .build();

            scheduleJob(jobInfo.getScheduleType(), jobDetail, jobInfo.getTrigger());

            if (jobInfo.getStatus().isRunning()) {
                jobInfo.getStatus().changeJobStatus(ScheduledJobStatus.JobStatus.IDLE);
            } else if (jobInfo.getStatus().isPaused()) {
                try {
                    scheduler.pauseJob(jobInfo.getJobKey());
                } catch (SchedulerException e) {
                    throw new JobScheduleException(e);
                }
            }
        }
    }

    private void unload(JobScheduleStore store) throws JobScheduleException {
        store.close();
    }

    public void registerJob(Class<? extends ScheduledJob> jobClass, String expression, JobID jobID, String group, String desc) throws JobScheduleException {
        registerJob(jobClass, expression, jobID, group, desc, new JobDataMap());
    }

    public void registerJob(Class<? extends ScheduledJob> jobClass, String expression, JobID jobID, String group, String desc, JobDataMap jobData) throws JobScheduleException {
        if (CronExpression.isValidExpression(expression)) {
            registerCronJob(jobClass, expression, jobID, group, desc, jobData);
        } else {
            try {
                int interval = Integer.parseInt(expression);
                if (interval == 0) {
                    registerOneTimeJob(jobClass, 0, jobID, group, desc, jobData);
                } else if (interval > 0) {
                    registerIntervalJob(jobClass, interval, jobID, group, desc, jobData);
                }
            } catch (NumberFormatException ignore) {
                throw new JobScheduleException(schedulerName, "schedule expression is wrong (job ID :" + jobID + ")");
            }
        }
    }

    /*
     * register a cron type job in scheduler
     */
    public void registerCronJob(Class<? extends ScheduledJob> jobClass, String expression, JobID jobID, String group, String desc) throws JobScheduleException {
        registerCronJob(jobClass, expression, jobID, group, desc, new JobDataMap());
    }

    public void registerCronJob(Class<? extends ScheduledJob> jobClass, String expression, JobID jobID, String group, String desc, JobDataMap jobData) throws JobScheduleException {
        String name = jobID.toString();
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(name, group)
            .withDescription(desc)
            .withSchedule(CronScheduleBuilder.cronSchedule(expression))
            .forJob(name, group)
            .build();

        scheduleJob(jobClass, trigger, jobID, group, desc, jobData);
    }

    /*
     * register a interval type job in scheduler
     */
    public void registerIntervalJob(Class<? extends ScheduledJob> jobClass, int interval, JobID jobID, String group, String desc) throws JobScheduleException {
        registerIntervalJob(jobClass, interval, jobID, group, desc, new JobDataMap());
    }

    public void registerIntervalJob(Class<? extends ScheduledJob> jobClass, int interval, JobID jobID, String group, String desc, JobDataMap jobData) throws JobScheduleException {
        String name = jobID.toString();
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(name, group)
            .withDescription(desc)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(interval)
                .repeatForever())
            .forJob(name, group)
            .startNow()
            .build();

        scheduleJob(jobClass, trigger, jobID, group, desc, jobData);
    }

    /*
     * register a one time job in scheduler
     */
    public void registerOneTimeJob(Class<? extends ScheduledJob> jobClass, int afterSeconds, JobID jobID, String group, String desc) throws JobScheduleException {
        registerOneTimeJob(jobClass, afterSeconds, jobID, group, desc, new JobDataMap());
    }

    public void registerOneTimeJob(Class<? extends ScheduledJob> jobClass, int afterSeconds, JobID jobID, String group, String desc, JobDataMap jobData) throws JobScheduleException {
        String name = jobID.toString();
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(name, group)
            .withDescription(desc)
            .forJob(name, group)
            .startAt(DateBuilder.futureDate(afterSeconds, IntervalUnit.SECOND))
            .build();

        scheduleJob(jobClass, trigger, jobID, group, desc, jobData);
    }

    /*
     * remove a job from this scheduler
     */
    public void removeJob(JobID jobID) throws JobScheduleException {
        JobKey key = getJobKey(jobID);
        if (Objects.isNull(key)) {
            throw new JobScheduleException(schedulerName, "' doesn't have the job (job id : " + jobID + ")");
        }

        lock();
        checkRunning();
        try {
            if (scheduler.checkExists(key)) {
                if (scheduler.deleteJob(key)) {
                    store.removeJob(jobID);
                } else {
                    throw new JobScheduleException(schedulerName, "job is not removed from the scheduler (job id :" + jobID + ")");
                }
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to remove job (job id :" + jobID + ")", e);
        } finally {
            unlock();
        }
    }

    /*
     * remove all jobs from this scheduler
     */
    public void removeAllJobs() throws JobScheduleException {
        lock();
        checkRunning();
        try {
            scheduler.clear();
            store.removeAllJobs();
//            scheduleStore.removeAllJobs();
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "' failed to remove all jobs");
        } finally {
            unlock();
        }
    }

    /*
     * remove all jobs in a group from this scheduler
     */
    public void removeAllJobsInGroup(String group) throws JobScheduleException {
        lock();
        checkRunning();
        try {
            for (JobKey key : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                JobID jobID = getJobID(key);
                if (scheduler.deleteJob(key)) {
                    store.removeJob(jobID);
                } else {
                    throw new JobScheduleException(schedulerName, "job is not removed from the scheduler (job id :" + jobID + ")");
                }
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "' failed to remove jobs in the group (group:" + group + ")");
        } finally {
            unlock();
        }
    }

    /*
     * pause a job from this scheduler
     */
    public void pauseJob(JobID jobID) throws JobScheduleException {
        JobKey key = getJobKey(jobID);
        if (Objects.isNull(key)) {
            throw new JobScheduleException(schedulerName, "doesn't have the job (job ID : " + jobID + ")");
        }

        checkRunning();
        lock();
        try {
            scheduler.pauseJob(key);
            store.changeToPaused(jobID);
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to pause the job (job ID : " + jobID + ")", e);
        } finally {
            unlock();
        }
    }

    /*
     * pause all jobs from this scheduler
     */
    public void pauseAllJobs() throws JobScheduleException {
        checkRunning();
        lock();
        try {
            scheduler.pauseJobs(GroupMatcher.anyJobGroup());
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
                store.changeToPaused(getJobID(jobKey));
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to stop all jobs");
        } finally {
            unlock();
        }
    }

    /*
     * pause all jobs in a group from this scheduler
     */
    public void pauseAllJobsInGroup(String group) throws JobScheduleException {
        try {
            scheduler.pauseJobs(GroupMatcher.jobGroupEquals(group));
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                store.changeToPaused(getJobID(jobKey));
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to stop jobs in the group (" + group + ")");
        }
    }

    /*
     * resume a job from this scheduler
     */
    public void resumeJob(JobID jobID) throws JobScheduleException {
        JobKey key = getJobKey(jobID);
        if (Objects.isNull(key)) {
            throw new JobScheduleException(schedulerName, "doesn't have the job (job ID : " + jobID + ")");
        }

        checkRunning();
        lock();
        try {
            scheduler.resumeJob(key);
            store.changeToIdle(jobID);
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to start the job (job ID : " + jobID + ")");
        } finally {
            unlock();
        }
    }

    /*
     * start all jobs from this scheduler
     */
    public void resumeAllJobs() throws JobScheduleException {
        try {
            scheduler.resumeJobs(GroupMatcher.anyJobGroup());
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyJobGroup())) {
                store.changeToIdle(getJobID(jobKey));
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to start all jobs");
        }
    }

    /*
     * start all jobs in a group from this scheduler
     */
    public void resumeAllJobsInGroup(String group) throws JobScheduleException {
        try {
            scheduler.resumeJobs(GroupMatcher.jobGroupEquals(group));
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(group))) {
                store.changeToIdle(getJobID(jobKey));
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to start jobs in the group (group:" + group + "})");
        }
    }

    private void scheduleJob(Class<? extends ScheduledJob> jobClass, Trigger trigger, JobID jobID, String group, String desc, JobDataMap jobData) throws JobScheduleException {
        String name = jobID.toString();
        JobBuilder builder = JobBuilder.newJob(jobClass)
            .withIdentity(name, group)
            .withDescription(desc);

        if (JobID.isValidJobID(jobID)) {
            jobData.put(ScheduledJob.SchedulerNameKey, schedulerName);
            jobData.put(ScheduledJob.JobIDKey, jobID);

            JobDetail job = builder.usingJobData(jobData).storeDurably().build();

            scheduleJob(jobID, job, trigger);
        } else {
            throw new JobScheduleException(schedulerName, "jod id '" + jobID + "' is not a valid ID");
        }
    }

    private void scheduleJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException {
        lock();
        checkRunning();

        try {
            boolean schedule = false;

            if (scheduler.checkExists(job.getKey())) {
                if (isJobRunning(jobID)) {
                    throw new JobScheduleException(schedulerName, "is running. Try it again later.");
                }

                if (hasTriggerKey(jobID)) {
                    scheduler.rescheduleJob(getTriggerKey(jobID), trigger);
                    store.updateJob(jobID, job, trigger);
                } else {
                    if (scheduler.deleteJob(job.getKey())) {
                        store.removeJob(jobID);
                    }

                    schedule = true;
                }
            } else {
                schedule = true;
            }

            if (schedule) {
                scheduler.scheduleJob(job, trigger);
                store.registerJob(jobID, job, trigger);
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to register job (job ID : " + jobID + ")", e);
        } finally {
            unlock();
        }

    }

    /*
     * for existing schedule
     *  - not register on schedule store
     *  - not use lock
     */
    private void scheduleJob(ScheduleType type, JobDetail job, Trigger trigger) throws JobScheduleException {
        try {
            Date nextFireTime = trigger.getNextFireTime();
            Date now = new Date();
            boolean mustRegister = true;
            @SuppressWarnings("unchecked")
            TriggerBuilder<Trigger> builder = (TriggerBuilder<Trigger>) trigger.getTriggerBuilder();
            if (Objects.nonNull(nextFireTime) && nextFireTime.before(now)) {
                if (type.equals(ScheduleType.CRON)) {
                    nextFireTime = trigger.getFireTimeAfter(now);
                    if (nextFireTime == null) mustRegister = false;
                    else builder.startAt(nextFireTime);
                } else if (type.equals(ScheduleType.ONE)) {
                    mustRegister = false;
                }
            }

            if (mustRegister) {
                scheduler.scheduleJob(job, builder.build());
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, "failed to register job (job ID : " + job.getKey().getName() + ")");
        }
    }

    public boolean hasJobID(JobID jobID) {
        return store.getJobIDs().contains(jobID);
    }

    /*
        state
            - NONE : 더 이상 trigger 없고 해당 job 동작 중
            - NORMAL : trigger 정상 동작 중
            - null : 더 이상 tirgger 없고 해당 job 종료됨
     */
    public TriggerState getJobState(JobID jobID) {
        try {
            TriggerKey key = getTriggerKey(jobID);
            if (key == null) return null;
            return scheduler.getTriggerState(key);
        } catch (SchedulerException e) {
            return null;
        }
    }

    public Collection<JobID> getJobIDs() {
        return store.getJobIDs();
    }

    public JobKey getJobKey(JobID jobID) {
        return store.getJobKey(jobID);
    }

    public JobID getJobID(JobKey jobKey) {
        try {
            return new JobID(Integer.parseInt(jobKey.getName()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public TriggerKey getTriggerKey(JobID jobID) {
        try {
            return scheduler.getTriggersOfJob(getJobKey(jobID)).get(0).getKey();
        } catch (SchedulerException | IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    public boolean hasTriggerKey(JobID jobID) {
        return Objects.nonNull(getTriggerKey(jobID));
    }

    public ScheduleType getScheduleType(JobID jobID) {
        return store.getJobInfo(jobID).getScheduleType();
    }

    public String getScheduleExpression(JobID jobID) {
        return store.getJobInfo(jobID).getScheduleExpr();
    }

    public String getJobDesc(JobID jobID) {
        try {
            return scheduler.getJobDetail(getJobKey(jobID)).getDescription();
        } catch (SchedulerException e) {
            return null;
        }
    }

    public boolean isJobRunning(JobID jobID) {
        try {
            for (JobExecutionContext context : scheduler.getCurrentlyExecutingJobs()) {
                if (jobID.equals(getJobID(context.getJobDetail().getKey()))) return true;
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void checkRunning() throws JobScheduleException {
        if (isNotRunning()) {
            throw new JobScheduleException(schedulerName, "' is not running");
        }
    }

    private void checkNotRunning() throws JobScheduleException {
        if (isRunning()) {
            throw new JobScheduleException(schedulerName, "' is running");
        }
    }

    private boolean isRunning() {
        try {
            return scheduler.isStarted();
        } catch (SchedulerException e) {
            return false;
        }
    }

    private boolean isNotRunning() {
        return !isRunning();
    }

    private void makeJobIDJobKeys(Map<JobID, JobKey> jobIDJobKeyMap) throws JobScheduleException {
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.anyGroup())) {
                jobIDJobKeyMap.put(getJobID(jobKey), jobKey);
            }
        } catch (SchedulerException e) {
            throw new JobScheduleException(schedulerName, e);
        }
    }

}
