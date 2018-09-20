package org.conqueror.lion.rest.router;

import akka.actor.ActorRef;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;


public abstract class APIRouter extends AllDirectives {

    private final ActorRef master;
    private final long messageTimeout;

    public APIRouter(ActorRef master, long messageTimeout) {
        this.master = master;
        this.messageTimeout = messageTimeout;
    }

    public Route createRoute() {
        return route(
            get(this::createGetRoute)
            .orElse(post(this::createPostRoute))
            .orElse(put(this::createPutRoute))
        );
    }

    protected abstract Route createGetRoute();

    protected abstract Route createPostRoute();

    protected abstract Route createPutRoute();

}
