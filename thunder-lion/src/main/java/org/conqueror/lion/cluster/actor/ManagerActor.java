package org.conqueror.lion.cluster.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.ThunderMessage;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public abstract class ManagerActor extends AbstractLoggingActor {

    private final JobConfig config;

    private final ActorRef master;

    public ManagerActor(JobConfig config, ActorRef master) {
        this.config = config;
        this.master = master;
    }

    protected JobConfig getConfig() {
        return config;
    }

    protected ActorRef getMaster() {
        return master;
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
            param -> {
                if (param instanceof Exception) {
                    return SupervisorStrategy.restart();
                } else {
                    return SupervisorStrategy.stop();
                }
            }
        );
    }

    protected void tellToMaster(ThunderMessage message) {
        getMaster().tell(message, getSelf());
    }

    protected void forwardToMaster(ThunderMessage message) {
        getMaster().forward(message, getContext());
    }

}
