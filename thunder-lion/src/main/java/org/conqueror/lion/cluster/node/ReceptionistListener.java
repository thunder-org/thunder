package org.conqueror.lion.cluster.node;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.cluster.client.ClusterClientUnreachable;
import akka.cluster.client.ClusterClientUp;
import akka.cluster.client.ClusterClients;
import akka.cluster.client.SubscribeClusterClients;

import java.util.HashSet;
import java.util.Set;


public class ReceptionistListener extends AbstractLoggingActor {
    private final ActorRef targetReceptionist;
    private final Set<ActorRef> clusterClients = new HashSet<>();

    public ReceptionistListener(ActorRef targetReceptionist) {
        this.targetReceptionist = targetReceptionist;
    }

    @Override
    public void preStart() {
        targetReceptionist.tell(SubscribeClusterClients.getInstance(), sender());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ClusterClients.class, msg -> {
                clusterClients.addAll(msg.getClusterClients());
                // Now do something with an up-to-date "clusterClients"
            })
            .match(ClusterClientUp.class, msg -> {
                clusterClients.add(msg.clusterClient());
                // Now do something with an up-to-date "clusterClients"
            })
            .match(ClusterClientUnreachable.class, msg -> {
                clusterClients.remove(msg.clusterClient());
                // Now do something with an up-to-date "clusterClients"
            })
            .build();
    }

}
