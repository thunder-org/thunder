package org.conqueror.bird.data.messages.index;

import org.conqueror.bird.data.messages.BirdMessage;


public abstract class IndexMessage implements BirdMessage {

    public static abstract class IndexRequest extends IndexMessage {

    }

    public static abstract class IndexResponse extends IndexMessage {

    }

}
