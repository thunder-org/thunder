package org.conqueror.drone.messages;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.lion.message.ThunderMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class PageCrawlingEnd implements ThunderMessage<PageCrawlingEnd> {

    private int number;

    public PageCrawlingEnd() {
        this.number = 0;
    }

    public PageCrawlingEnd(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(getNumber());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public PageCrawlingEnd readObject(DataInput input) throws SerializableException {
        try {
            return new PageCrawlingEnd(input.readInt());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
