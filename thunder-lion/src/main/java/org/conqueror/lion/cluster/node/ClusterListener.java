package org.conqueror.lion.cluster.node;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;


public class ClusterListener extends AbstractLoggingActor {

    private final Cluster cluster = Cluster.get(getContext().getSystem());

    public static Props props() {
        return Props.create(ClusterListener.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ClusterEvent.MemberUp.class, mUp -> {
                log().info("Member is Up: {}", mUp.member());
            })
            .match(ClusterEvent.UnreachableMember.class, mUnreachable -> {
                log().info("Member detected as unreachable: {}", mUnreachable.member());
                getCluster().down(mUnreachable.member().address());
            })
            .match(ClusterEvent.MemberRemoved.class, mRemoved -> {
                log().info("Member is Removed: {}", mRemoved.member());
            })
            .match(ClusterEvent.LeaderChanged.class, change -> {
                log().info("Leader is Changed: {}", change.getLeader());
            })
            .build();
    }

    @Override
    public void preStart() throws Exception {
        // A snapshot of the full state, akka.cluster.ClusterEvent.CurrentClusterState,
        // is sent to the subscriber as the first message, followed by events for incremental updates.
        getCluster().subscribe(self()
            // instead of receiving CurrentClusterState as the first message
            // you will receive the events corresponding to the current state
            // to mimic what you would have seen if you were listening to the events when they occurred in the past
            , ClusterEvent.initialStateAsEvents()
            , ClusterEvent.MemberEvent.class
            , ClusterEvent.UnreachableMember.class
            , ClusterEvent.ClusterDomainEvent.class
        );

        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        getCluster().unsubscribe(self());

        super.postStop();
    }

    protected Cluster getCluster() {
        return cluster;
    }

}
