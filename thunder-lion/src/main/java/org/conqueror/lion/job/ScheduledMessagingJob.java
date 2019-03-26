package org.conqueror.lion.job;

import akka.actor.ActorRef;
import org.conqueror.lion.message.ThunderMessage;
import org.conqueror.lion.schedule.job.JobID;
import org.conqueror.lion.schedule.job.ScheduledJob;
import org.quartz.JobDataMap;


public class ScheduledMessagingJob extends ScheduledJob {

    public static String SenderKey = "SENDER";
    public static String ReceiverKey = "RECEIVER";
    public static String MessageKey = "MESSAGE";


    @Override
    protected void before(JobID jobID, JobDataMap data) {
    }

    @Override
    protected void doJob(JobID jobID, JobDataMap data) {
        ActorRef sender = (ActorRef) data.get(SenderKey);
        ActorRef receiver = (ActorRef) data.get(ReceiverKey);
        ThunderMessage message = (ThunderMessage) data.get(MessageKey);

        receiver.tell(message, sender);
    }

    @Override
    protected void after(JobID jobID, JobDataMap data) {

    }

}
