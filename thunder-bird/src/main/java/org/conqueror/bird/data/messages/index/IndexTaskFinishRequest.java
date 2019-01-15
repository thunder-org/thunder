package org.conqueror.bird.data.messages.index;

import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;


public final class IndexTaskFinishRequest extends IndexMessage {

    @Override
    public void writeObject(DataOutput output) throws SerializableException {

    }

    @Override
    public IndexTaskFinishRequest readObject(DataInput input) throws SerializableException {
        return new IndexTaskFinishRequest();
    }

}
