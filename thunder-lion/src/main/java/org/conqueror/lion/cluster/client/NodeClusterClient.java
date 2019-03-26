package org.conqueror.lion.cluster.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.conqueror.lion.config.ClientConfig;
import org.conqueror.lion.message.ThunderMessage;
import scala.compat.java8.FutureConverters;

import java.io.Closeable;
import java.util.concurrent.CompletionStage;

import static org.conqueror.lion.cluster.node.Path.*;


public class NodeClusterClient implements Closeable {

    private final ActorSystem clientSystem;
    private final ActorRef clusterClient;

    private final Timeout timeout;

    public NodeClusterClient(String baseName) {
        this(new ClientConfig(baseName));
    }

    public NodeClusterClient(ClientConfig config) {
        clientSystem = ActorSystem.create("cluster-client", config.getConfig());
        clusterClient = clientSystem.actorOf(ClusterClient.props(ClusterClientSettings.create(clientSystem))
            , "client-of-" + config.getClientName());
        timeout = config.getAskTimeout();
    }

    public ActorRef getClient() {
        return clusterClient;
    }

    @Override
    public void close() {
        clientSystem.stop(getClient());
        clientSystem.terminate();
    }

    public CompletionStage<Object> askToMaster(ThunderMessage request) {
        return ask(request, NODE_MASTER_SINGLETON_PATH);
    }

    public CompletionStage<Object> askToWorker(ThunderMessage request, String nodeWorkerID) {
        return ask(request, getNodeWorkerPath(nodeWorkerID));
    }

    public CompletionStage<Object> askToWorkers(ThunderMessage request) {
        return askToAll(request, NODE_WORKERS_PATH);
    }

    public CompletionStage<Object> ask(ThunderMessage request, String path) {
        return ask(new ClusterClient.Send(path, request));
    }

    public CompletionStage<Object> askToAll(ThunderMessage request, String path) {
        return ask(new ClusterClient.SendToAll(path, request));
    }

    private CompletionStage<Object> ask(Object message) {
        return FutureConverters.toJava(Patterns.ask(clusterClient, message, timeout));
    }

    public void tellToMaster(ThunderMessage request, ActorRef sender) {
        tell(request, NODE_MASTER_SINGLETON_PATH, sender);
    }

    public void tellToWorkers(ThunderMessage request, ActorRef sender, String nodeWorkerID) {
        tellToAll(request, getNodeWorkerPath(nodeWorkerID), sender);
    }

    public void tell(ThunderMessage request, String path, ActorRef sender) {
        tell(new ClusterClient.Send(path, request), sender);
    }

    public void tellToAll(ThunderMessage request, String path, ActorRef sender) {
        tell(new ClusterClient.SendToAll(path, request), sender);
    }

    private void tell(Object message, ActorRef sender) {
        clusterClient.tell(message, sender);
    }

}
