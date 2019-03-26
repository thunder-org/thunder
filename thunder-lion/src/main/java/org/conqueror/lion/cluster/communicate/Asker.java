package org.conqueror.lion.cluster.communicate;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import org.conqueror.lion.message.ThunderMessage;
import scala.compat.java8.FutureConverters;

import java.util.concurrent.CompletionStage;


public interface Asker {

    static <T extends ThunderMessage> CompletionStage<T> ask(ActorRef destination, ThunderMessage message, Class<T> type, long timeout) {
        return FutureConverters.toJava(Patterns.ask(destination, message, timeout)).thenApply(type::cast);
    }

}
