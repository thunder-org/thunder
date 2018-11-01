package org.conqueror.bird.gate.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.source.GateSource;
import org.conqueror.bird.gate.source.GateSourceAccessor;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;


public abstract class GateScanner {

    private static final Logger logger = LoggerFactory.getLogger(GateScanner.class);

    protected final String id = UUID.randomUUID().toString();

    private final GateSourceAccessor sourceAccessor;
    private final BlockingQueue<Document> documentQueue;

    public GateScanner(GateSourceAccessor sourceAccessor, BlockingQueue<Document> documentQueue) {
        this.sourceAccessor = sourceAccessor;
        this.documentQueue = documentQueue;
    }

    public void run() {
        int numberOfSources = 0;
        int numberOfFailures = 0;

        logger.info("[Gate Scanner] [Start]");

        GateSource source;
        while (!(source = takeSource()).isOver()) {
            logger.info("[Scanning] [Start] source:{}", source);

            boolean success = processSource(source);

            completeSource(source, success);
            if (!success) numberOfFailures++;

            numberOfSources++;
//			logger.info("[Scanning] [End] completed:{} source:{}, documents:{}, filtered:{}"
//					, (success? "success" : "fail"), source, gateStats.getNumberOfDocuments(), gateStats.getNumberOfFiltered());
        }

        logger.info("[Gate Scanner] [End] sources:{} , failures:{}", numberOfSources, numberOfFailures);
    }

    /*
        source -> documents
     */
    public abstract boolean processSource(GateSource source);

    /*
        take a gate-source from the distributor
     */
    protected GateSource takeSource() {
        return sourceAccessor.take();
    }

    protected void completeSource(GateSource source, boolean success) {
        sourceAccessor.completed(source, success);
    }

    protected void put(Document doc) {
        try {
            if (doc != null) documentQueue.put(doc);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void put(Document[] docs) {
        for (Document doc : docs) {
            put(doc);
        }
    }

}
