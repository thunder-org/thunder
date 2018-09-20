package org.conqueror.lion.rest;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.conqueror.lion.rest.router.job.JobServiceRouter;
import org.conqueror.lion.config.RestAPIConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;


public class RestAPIService extends AllDirectives {

    private final LoggingAdapter log;

    private final ActorSystem system;
    private final ActorRef master;
    private final RestAPIConfig config;
    private final Http http;
    private final ActorMaterializer materializer;

    private CompletionStage<ServerBinding> binding = null;

    public RestAPIService(ActorSystem system, ActorRef master, RestAPIConfig config) {
        this.log = Logging.getLogger(system, this);
        this.system = system;
        this.master = master;
        this.config = config;
        this.http = Http.get(system);
        this.materializer = ActorMaterializer.create(system);
    }

    public void open() {
        Route route = new JobServiceRouter(master, config.getMessageTimeout())
            .createRoute();
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(system, materializer);

        binding = http.bindAndHandle(routeFlow
            , ConnectHttp.toHost(config.getRestAPIHost(), config.getRestAPIPort())
            , materializer);

        binding.exceptionally(failure -> {
            log.error(failure.getCause(), "REST API service binding failure : {}", failure.getMessage());
            system.terminate();
            return null;
        });

        binding.thenAccept(bound -> log.info("REST API service is opened"));
    }

    public void close() {
        if (Objects.nonNull(binding)) {
            binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> {
                    log.info("REST API service is closed");
                    system.terminate();
                });
        }
    }

    public static String extractIP(HttpRequest request) {
        List<Optional<HttpHeader>> headers = new ArrayList<>(8);

        headers.add(request.getHeader("REMOTE_ADDR"));
        headers.add(request.getHeader("HTTP_X_FORWARDED_FOR"));
        headers.add(request.getHeader("HTTP_CLIENT_IP"));
        headers.add(request.getHeader("HTTP_X_FORWARDED"));
        headers.add(request.getHeader("HTTP_X_CLUSTER_CLIENT_IP"));
        headers.add(request.getHeader("HTTP_FORWARDED_FOR"));
        headers.add(request.getHeader("HTTP_FORWARDED"));
        headers.add(request.getHeader("HTTP_VIA"));

        for (Optional<HttpHeader> header : headers) {
            if (header.isPresent()) {
                String ip = header.get().value().trim();
                if (!ip.isEmpty()) return ip;
            }
        }

        return null;
    }

}
