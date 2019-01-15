package org.conqueror.bird.data.messages.analysis;

import org.conqueror.bird.data.messages.BirdMessage;


public abstract class AnalysisMessage implements BirdMessage {

    public static abstract class AnalysisRequest extends AnalysisMessage {

    }

    public static abstract class AnalysisResponse extends AnalysisMessage {

    }

}
