package org.conqueror.lion.cluster.actor;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.ThunderMessage;
import redis.clients.jedis.exceptions.JedisConnectionException;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public abstract class NodeActor extends IDActor {

    private final NodeConfig config;

    private final Cluster cluster = Cluster.get(getContext().getSystem());

    private final ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

    public NodeActor(NodeConfig config) {
        this.config = config;
    }

    protected NodeConfig getConfig() {
        return config;
    }

    protected Cluster getCluster() {
        return cluster;
    }

    protected ActorSystem getSystem() {
        return getContext().getSystem();
    }

    protected ActorRef getMediator() {
        return mediator;
    }

    @Override
    public void preStart() throws Exception {
        ClusterClientReceptionist.get(getContext().getSystem()).registerService(getSelf());

        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        getCluster().unsubscribe(self());
        ClusterClientReceptionist.get(getContext().getSystem()).unregisterService(getSelf());

        super.postStop();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES),
            param -> {
                if (param instanceof JedisConnectionException) {
                    return SupervisorStrategy.stop();
                } else if (param instanceof Exception) {
                    return SupervisorStrategy.restart();
                } else {
                    return SupervisorStrategy.escalate();
                }
            }
        );
    }

    protected ActorRef createChildActor(Props props, String name) {
        return getContext().actorOf(props, name);
    }

    protected ActorRef createActor(Props props, String name) {
        return getSystem().actorOf(props, name);
    }

    protected ActorRef createComponentActor(Class componentClass, String name) {
        try {
            return createChildActor(Props.create(componentClass, getConfig(), getSelf()), name);
        } finally {
            log().info("{}({}) is started", name, componentClass);
        }
    }

    protected void subscribe(String topic) {
        getMediator().tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
    }

    protected void unsubscribe(String topic) {
        getMediator().tell(new DistributedPubSubMediator.Unsubscribe(topic, getSelf()), getSelf());
    }

    protected void publish(String topic, ThunderMessage message) {
        getMediator().forward(new DistributedPubSubMediator.Publish(topic, message), getContext());
    }

    protected void become(Receive receive) {
        getContext().become(receive);
    }

}
