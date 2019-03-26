package org.conqueror.bird.api.rest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.Route;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.lion.cluster.api.rest.router.message.MessageServiceRouter;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.ScheduleManagerMessage;

import java.util.function.Function;


public class BirdMessageServiceRouter extends MessageServiceRouter {

    public BirdMessageServiceRouter(ActorSystem system, ActorRef nodeMaster, NodeConfig config, long messageTimeout) {
        super(system, nodeMaster, config, messageTimeout);
    }

    @Override
    protected Route createGetRoute() {
        return get(
            () ->
                route(
                    processPathPrefixPath("index", path -> complete("<h1>Say " + path + " to akka-http</h1>"))
                )
        );
    }

    @Override
    protected Route createPostRoute() {
        return post(
            () ->
                route(

                )
        );
    }

    @Override
    protected Route createPutRoute() {
        return put(
            () ->  route(
                pathPrefix("index", () ->
                    processPathPrefixStringEntity("register", transformToJobRegisterRequest(), ScheduleManagerMessage.JobRegisterResponse.class)
                )
            )
        );
    }

    @Override
    protected Route createDeleteRoute() {
        return delete(
            () ->
                route(
                )
        );
    }

    protected Function<String, ScheduleManagerMessage.JobRegisterRequest> transformToJobRegisterRequest() {
        return jsonConfig -> new ScheduleManagerMessage.JobRegisterRequest(new IndexConfig(getNodeConfig().getConfig(), jsonConfig));
    }

}
