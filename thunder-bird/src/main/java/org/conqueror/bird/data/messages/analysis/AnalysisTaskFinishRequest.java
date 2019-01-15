package org.conqueror.bird.data.messages.analysis;

import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;


public final class AnalysisTaskFinishRequest extends AnalysisMessage {

    @Override
    public void writeObject(DataOutput output) throws SerializableException {

    }

    @Override
    public AnalysisTaskFinishRequest readObject(DataInput input) throws SerializableException {
        return new AnalysisTaskFinishRequest();
    }

}
