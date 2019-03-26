package org.conqueror.drone.data.messages;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.lion.message.JobManagerMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class CrawlSourceAssignRequest extends JobManagerMessage.TaskAssignRequest {

    public final int size;

    public CrawlSourceAssignRequest() {
        this(0);
    }

    public CrawlSourceAssignRequest(int size) {
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
    public CrawlSourceAssignRequest readObject(DataInput input) throws SerializableException {
        try {
            return new CrawlSourceAssignRequest(input.readInt());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
