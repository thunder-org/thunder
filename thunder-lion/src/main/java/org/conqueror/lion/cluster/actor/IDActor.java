package org.conqueror.lion.cluster.actor;

import akka.actor.AbstractLoggingActor;
import org.conqueror.lion.message.IDIssuerMessage;


public abstract class IDActor extends AbstractLoggingActor {

    private String id;

    protected Receive buildReadyReceive() {
        return receiveBuilder()
            // id-issuer
            .match(IDIssuerMessage.IDIssuerResponse.class, this::processIssueMyID)
            .build();
    }

    public abstract Receive buildWorkingReceive();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .matchAny(message -> {
                log().warning("received an unhandled request : {}", message);
                unhandled(message);
            })
            .build();
    }

    private void setId(String id) {
        this.id = id;
    }

    protected String getId() {
        return id;
    }

    @Override
    public void preStart() throws Exception {
        become(buildReadyReceive());

        issueID();

        super.preStart();
    }

    protected abstract void postWorking() throws Exception;

    protected abstract IDIssuerMessage.IDIssuerRequest idIssuerRequest();

    protected abstract void tellToIDIssuer(IDIssuerMessage.IDIssuerRequest request);

    private void issueID() {
        tellToIDIssuer(idIssuerRequest());
    }

    private void processIssueMyID(IDIssuerMessage.IDIssuerResponse response)  throws Exception {
        setId(response.getId());

        become(buildWorkingReceive());
        postWorking();
    }

    protected void become(Receive receive) {
        getContext().become(receive);
    }

}
