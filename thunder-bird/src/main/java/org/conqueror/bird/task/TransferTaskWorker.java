package org.conqueror.bird.task;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.LionMessage;


public abstract class TransferTaskWorker<C extends JobConfig> extends AbstractLoggingActor {

    private final C config;
    private final ActorRef transferTaskManager;
    private final ActorRef transferTo;

    public TransferTaskWorker(JobConfig config, ActorRef transferTaskManager, ActorRef transferTo) {
        this.config = (C) config;
        this.transferTaskManager = transferTaskManager;
        this.transferTo = transferTo;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(BirdMessage.class, this::processTask)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    protected abstract BirdMessage work(BirdMessage data);

    protected C getConfig() {
        return config;
    }

    protected void sendToTransferTaskManager(LionMessage message) {
        transferTaskManager.tell(message, getSelf());
    }

    protected void sendToNextTransferTaskManager(LionMessage message) {
        transferTo.tell(message, getSelf());
    }

    private void processTask(BirdMessage data) {
        //noinspection unchecked
        BirdMessage result = work(data);

        // transfer to next task-manager
        if (result != null && transferTo != null) sendToNextTransferTaskManager(result);
    }

}
