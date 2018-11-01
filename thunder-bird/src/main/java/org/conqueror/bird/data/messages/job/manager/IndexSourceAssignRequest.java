package org.conqueror.bird.data.messages.job.manager;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class IndexSourceAssignRequest extends JobManagerMessage.TaskAssignRequest {

    public final int size;

    public IndexSourceAssignRequest() {
        this(0);
    }

    public IndexSourceAssignRequest(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(size);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public IndexSourceAssignRequest readObject(DataInput input) throws SerializableException {
        try {
            return new IndexSourceAssignRequest(input.readInt());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
