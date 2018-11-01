package org.conqueror.bird.gate;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.conqueror.bird.config.IndexConfig;
import org.conqueror.bird.exceptions.parse.ParseException;
import org.conqueror.bird.exceptions.schema.SchemaException;
import org.conqueror.bird.gate.source.GateSource;

import java.util.List;


public abstract class GateSourceDistributor {

    private final LoggingAdapter logger;
    private final ActorRef jobManager;
    private final IndexConfig config;

    public GateSourceDistributor(ActorSystem system, ActorRef jobManager, IndexConfig config) {
        logger = Logging.getLogger(system, this);
        this.jobManager = jobManager;
        this.config = config;
    }

    public LoggingAdapter getLogger() {
        return logger;
    }

    public ActorRef getJobManager() {
        return jobManager;
    }

    public IndexConfig getConfig() {
        return config;
    }

    public abstract void loadGateSources() throws ParseException, SchemaException;

    public abstract List<GateSource> takeGateSources(int size);

}
