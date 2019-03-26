package org.conqueror.lion.cluster.actor;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.IDIssuerMessage;
import org.conqueror.lion.message.ThunderMessage;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public abstract class NodeComponentActor extends IDActor {

    private final NodeConfig config;

    private final ActorRef node;

    public NodeComponentActor(NodeConfig config, ActorRef node) {
        this.config = config;
        this.node = node;
    }

    protected NodeConfig getConfig() {
        return config;
    }

    protected ActorRef getNode() {
        return node;
    }

    @Override
    protected void tellToIDIssuer(IDIssuerMessage.IDIssuerRequest request) {
        tellToNode(request);
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

    protected void tellToNode(ThunderMessage message) {
        getNode().tell(message, getSelf());
    }

    protected void forwardToNode(ThunderMessage message) {
        getNode().forward(message, getContext());
    }

}
