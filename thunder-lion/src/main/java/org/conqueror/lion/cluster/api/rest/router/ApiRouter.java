package org.conqueror.lion.cluster.api.rest.router;

import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;


public abstract class ApiRouter extends AllDirectives {

    protected abstract Route createRoute();

}
