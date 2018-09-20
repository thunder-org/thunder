package org.conqueror.lion.cluster.schedule;

import akka.actor.ActorRef;
import org.conqueror.lion.cluster.actor.NodeComponentActor;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.exceptions.schedule.JobScheduleException;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.schedule.JobScheduler;
import org.conqueror.lion.schedule.JobSchedulerFactory;


public class ScheduleManager extends NodeComponentActor {

    private JobScheduler scheduler;

    public ScheduleManager(NodeConfig config, ActorRef nodeMaster) throws JobScheduleException {
        super(config, nodeMaster);
    }

    @Override
    public Receive buildWorkingReceive() {
        return receiveBuilder()
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        scheduler.startup();
    }

    @Override
    protected void postWorking() throws JobScheduleException {
        scheduler = JobSchedulerFactory.getInstance().getOrCreateScheduler(getSelf().path().name());
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

}
