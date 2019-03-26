package org.conqueror.lion.cluster.schedule;

import akka.actor.ActorRef;
import akka.cluster.Cluster;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.job.ScheduledMessagingJob;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.JobMasterMessage;
import org.conqueror.lion.message.ScheduleManagerMessage;
import org.conqueror.lion.schedule.JobScheduler;
import org.conqueror.lion.schedule.JobSchedulerFactory;
import org.conqueror.lion.schedule.store.JobScheduleDDataStore;
import org.conqueror.lion.schedule.store.JobScheduleStore;
import org.quartz.JobDataMap;


public class ScheduleManager extends NodeComponentActor {

    private JobScheduler scheduler;

    public ScheduleManager(NodeConfig config, ActorRef nodeMaster) throws JobScheduleException {
        super(config, nodeMaster);

        String name = "DURABLE_" + getSelf().path().toStringWithoutAddress();
        JobScheduleStore store = new JobScheduleDDataStore(name, Cluster.get(getContext().getSystem()), getSelf(), getNode(), config.getAskTimeout());
        scheduler = JobSchedulerFactory.getInstance().getOrCreateScheduler(name, store);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            .match(ScheduleManagerMessage.JobRegisterRequest.class, this::processRegisterJob)
            .match(ScheduleManagerMessage.JobRemoveRequest.class, this::processRemoveJob)
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    @Override
    protected void postWorking() throws JobScheduleException {
        scheduler.startup();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        scheduler.shutdown();
    }

    @Override
    protected IDIssuerMessage.IDIssuerRequest idIssuerRequest() {
        return new IDIssuerMessage.ScheduleManagerIssueIDRequest();
    }

    private void processRegisterJob(ScheduleManagerMessage.JobRegisterRequest request) throws JobScheduleException {
        boolean succ = true;
        try {
            registerJob(request.getConfig());
        } catch (JobScheduleException e) {
            succ = false;
        }
        getSender().tell(new ScheduleManagerMessage.JobRegisterResponse(succ), getSelf());
    }

    private void processRemoveJob(ScheduleManagerMessage.JobRemoveRequest request) throws JobScheduleException {
        boolean succ = true;
        try {
            unregisterJob(request.getConfig());
        } catch (JobScheduleException e) {
            succ = false;
        }
        getSender().tell(new ScheduleManagerMessage.JobRemoveResponse(succ), getSelf());
    }

    public void registerJob(JobConfig config) throws JobScheduleException {
        JobDataMap jobData = new JobDataMap();
        jobData.put(ScheduledMessagingJob.ReceiverKey, getNode());
        jobData.put(ScheduledMessagingJob.SenderKey, getSelf());
        jobData.put(ScheduledMessagingJob.MessageKey, new JobMasterMessage.JobManagerCreateRequest(config));

        scheduler.registerJob(ScheduledMessagingJob.class, config.getSchedule(), config.getJobID(), config.getJobGroup(), config.getJobDescription(), jobData);
        log().info("job was scheduled - job-id : {}, job-name : {}, schedule-exp : {}", config.getJobID(), config.getName(), config.getSchedule());
    }

    public void unregisterJob(JobConfig config) throws JobScheduleException {
        scheduler.removeJob(config.getJobID());
        log().info("scheduled job was removed - job-id : {}, job-name : {}, schedule-exp : {}", config.getJobID(), config.getName(), config.getSchedule());
    }

}
