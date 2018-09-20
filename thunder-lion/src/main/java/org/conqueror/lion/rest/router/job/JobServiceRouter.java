package org.conqueror.lion.rest.router.job;

import akka.actor.ActorRef;
import akka.http.javadsl.server.Route;
import org.conqueror.lion.rest.router.APIRouter;


public class JobServiceRouter extends APIRouter {

    public JobServiceRouter(ActorRef master, long messageTimeout) {
        super(master, messageTimeout);
    }

    protected Route createGetRoute() {
        return route(

        );
    }

    protected Route createPostRoute() {
        return route(

        );
    }

    protected Route createPutRoute() {
        return route(

        );
    }

}
