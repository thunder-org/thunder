package org.conqueror.lion.serialize;

import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class SerializableObject implements LionSerializable<SerializableObject> {

    private final String text;

    public SerializableObject() {
        this(null);
    }

    public SerializableObject(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(text);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public SerializableObject readObject(DataInput input) throws SerializableException {
        try {
            return new SerializableObject(input.readUTF());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
