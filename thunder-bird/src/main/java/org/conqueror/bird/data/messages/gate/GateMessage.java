package org.conqueror.bird.data.messages.gate;

import org.conqueror.bird.data.messages.BirdMessage;


public abstract class GateMessage implements BirdMessage {

    public static abstract class GateRequest extends GateMessage {

    }

    public static abstract class GateResponse extends GateMessage {

    }

}
