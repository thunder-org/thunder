package org.conqueror.lion.schedule.store;

import akka.actor.ActorRef;
import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.util.Timeout;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.job.ScheduledMessagingJob;
import org.conqueror.lion.schedule.JobScheduler;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJobInfo;
import org.conqueror.lion.schedule.job.ScheduledJobStatus;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import scala.concurrent.duration.Duration;

import java.util.Collection;
import java.util.concurrent.TimeUnit;


public class JobScheduleDDataStore implements JobScheduleStore {

    private final String id;
    private final Cluster cluster;
    private final Timeout timeout;
    private final ActorRef sender;
    private final ActorRef receiver;

    private DDataMap<JobID, ScheduledJobInfo> jobs = null;

    public JobScheduleDDataStore(String id, Cluster cluster, long timeoutSec) {
        this(id, cluster, ActorRef.noSender(), null, timeoutSec);
    }

    public JobScheduleDDataStore(String id, Cluster cluster, Timeout timeout) {
        this(id, cluster, ActorRef.noSender(), null, timeout);
    }

    public JobScheduleDDataStore(String id, Cluster cluster, ActorRef sender, ActorRef receiver, long timeoutSec) {
        this(id, cluster, sender, receiver, new Timeout(Duration.create(timeoutSec, TimeUnit.SECONDS)));
    }

    public JobScheduleDDataStore(String id, Cluster cluster, ActorRef sender, ActorRef receiver, Timeout timeout) {
        this.id = id;
        this.cluster = cluster;
        this. timeout = timeout;

        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void open() {
        jobs = new DDataMap<>(id, getCluster(), DistributedData.get(getCluster().system()).replicator(), getTimeout());
    }

    @Override
    public void close() {
    }

    @Override
    public void registerJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException {
        JobScheduler.ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        JobDataMap dataMap = job.getJobDataMap();
        dataMap.remove(ScheduledMessagingJob.SenderKey);
        dataMap.remove(ScheduledMessagingJob.ReceiverKey);

        jobs.put(jobID, new ScheduledJobInfo(jobID, job, trigger, type, expression));
    }

    @Override
    public void updateJob(JobID jobID, JobDetail job, Trigger trigger) throws JobScheduleException {
        JobScheduler.ScheduleType type = getType(trigger);
        String expression = getExpression(trigger, type);

        JobDataMap dataMap = job.getJobDataMap();
        dataMap.remove(ScheduledMessagingJob.SenderKey);
        dataMap.remove(ScheduledMessagingJob.ReceiverKey);

        jobs.put(jobID, new ScheduledJobInfo(jobID, job, trigger, type, expression));
    }

    @Override
    public void removeJob(JobID jobID) {
        jobs.remove(jobID);
    }

    @Override
    public void removeAllJobs() {
        jobs.clear();
    }

    @Override
    public Collection<ScheduledJobInfo> getRegisteredJobs() {
        Collection<ScheduledJobInfo> registeredJobs = jobs.values();

        registeredJobs.forEach(jobInfo -> {
            JobDataMap jobData = jobInfo.getJobDataMap();
            jobData.put(ScheduledMessagingJob.ReceiverKey, getReceiver());
            jobData.put(ScheduledMessagingJob.SenderKey, getSender());
        });

        return registeredJobs;
    }

    @Override
    public Collection<JobID> getJobIDs() {
        return jobs.keySet();
    }

    @Override
    public JobKey getJobKey(JobID jobID) {
        return jobs.get(jobID).getJobKey();
    }

    @Override
    public ScheduledJobStatus getJobStatus(JobID jobID) {
        return jobs.get(jobID).getStatus();
    }

    @Override
    public ScheduledJobInfo getJobInfo(JobID jobID) {
        return jobs.get(jobID);
    }

    @Override
    public void changeToIdle(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.IdleStatus);
        jobs.put(jobID, info);
    }

    @Override
    public void changeToRunning(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.RunningStatus);
        jobs.put(jobID, info);
    }

    @Override
    public void changeToPaused(JobID jobID) {
        ScheduledJobInfo info = jobs.get(jobID);
        info.getStatus().changeJobStatus(ScheduledJobStatus.PausedStatus);
        jobs.put(jobID, info);
    }

    private String getId() {
        return id;
    }

    private Cluster getCluster() {
        return cluster;
    }

    private Timeout getTimeout() {
        return timeout;
    }

    private ActorRef getSender() {
        return sender;
    }

    private ActorRef getReceiver() {
        return receiver;
    }
}
