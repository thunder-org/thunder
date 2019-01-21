package org.conqueror.bird.task;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.lion.config.JobConfig;
import org.conqueror.lion.message.LionMessage;


public abstract class DeliveryTaskWorker<C extends JobConfig> extends AbstractLoggingActor {

    private final C config;
    private final ActorRef deliveryTaskManager;
    private final ActorRef deliverTo;

    public DeliveryTaskWorker(JobConfig config, ActorRef deliveryTaskManager, ActorRef deliverTo) {
        this.config = (C) config;
        this.deliveryTaskManager = deliveryTaskManager;
        this.deliverTo = deliverTo;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(getAssignTaskMessageClass(), this::processTask)
            .build();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        finish();
        super.postStop();
    }

    protected abstract Class<?> getAssignTaskMessageClass();
    protected abstract BirdMessage work(Object data);
    protected abstract void finish();

    protected C getConfig() {
        return config;
    }

    protected void sendToDeliveryTaskManager(LionMessage message) {
        deliveryTaskManager.tell(message, getSelf());
    }

    protected void sendToNextDeliveryTaskManager(LionMessage message) {
        deliverTo.tell(message, getSelf());
    }

    protected void processTask(Object data) {
        //noinspection unchecked
        BirdMessage result = work(data);

        // delivery to next task-manager
        if (result != null && deliverTo != null) sendToNextDeliveryTaskManager(result);
    }

}
