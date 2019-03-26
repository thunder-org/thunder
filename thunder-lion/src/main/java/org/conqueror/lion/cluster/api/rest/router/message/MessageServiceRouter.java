package org.conqueror.lion.cluster.api.rest.router.message;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.RouteAdapter;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import org.conqueror.lion.cluster.api.rest.router.ApiRouter;
import org.conqueror.lion.cluster.communicate.Asker;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.ThunderMessage;

import java.util.Map;
import java.util.function.Function;


public abstract class MessageServiceRouter extends ApiRouter implements Asker {

    private final LoggingAdapter log;

    private final ActorRef nodeMaster;
    private final long messageTimeout;
    private final NodeConfig nodeConfig;


    public MessageServiceRouter(ActorSystem system, ActorRef nodeMaster, NodeConfig config, long messageTimeout) {
        log = Logging.getLogger(system, this);
        this.nodeMaster = nodeMaster;
        this.messageTimeout = messageTimeout;
        this.nodeConfig = config;
    }

    @Override
    public Route createRoute() {
        return createGetRoute()
            .orElse(createPostRoute())
            .orElse(createPutRoute())
            .orElse(createDeleteRoute());
    }

    protected LoggingAdapter log() {
        return log;
    }

    protected NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    protected abstract Route createGetRoute();

    protected abstract Route createPostRoute();

    protected abstract Route createPutRoute();

    protected abstract Route createDeleteRoute();

    protected ActorRef getNodeMaster() {
        return nodeMaster;
    }

    protected long getMessageTimeout() {
        return messageTimeout;
    }

    /*
        ~/prefix -> send static request to master / return response
     */
    protected Route processPathPrefix(String prefix, ThunderMessage request, Class<? extends ThunderMessage> responseClass) {
        return pathPrefix(prefix, () ->
            processRequest(request, responseClass)
        );
    }

    /*
        ~/prefix, entity -> marshall json entity to request -> send static request to master / return response
     */
    protected Route processPathPrefixEntity(String prefix, Class<? extends ThunderMessage> requestClass, Class<? extends ThunderMessage> responseClass) {
        return pathPrefix(prefix, () ->
            entity(
                Jackson.unmarshaller(requestClass)
                , request -> processRequest(request, responseClass)
            )
        );
    }

    protected Route processPathPrefixStringEntity(String prefix, Function<String, ? extends ThunderMessage> function, Class<? extends ThunderMessage> responseClass) {
        return pathPrefix(prefix, () ->
            entity(
                Unmarshaller.entityToString()
                , entity -> processRequest(function.apply(entity), responseClass)
            )
        );
    }

    /*
        ~/prefix/path
     */
    protected Route processPathPrefixPath(String prefix, Function<String, Route> route) {
        return pathPrefix(prefix, () ->
            path(
                route
            )
        );
    }

    /*
        ~/prefix?params
     */
    protected Route processPathPrefixParam(String prefix, Function<Map<String, String>, Route> route) {
        return pathPrefix(prefix, () ->
            parameterMap(
                route
            )
        );
    }

    protected RouteAdapter processRequest(ThunderMessage request, Class<? extends ThunderMessage> responseClass) {
        return completeOKWithFuture(
            Asker.ask(getNodeMaster(), request, responseClass, getMessageTimeout())
            , Jackson.marshaller()
        );
    }

}
