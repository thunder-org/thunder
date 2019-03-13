package org.conqueror.common.serialize;

import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.*;


public interface ThunderSerializable<T extends ThunderSerializable> extends Serializable {

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    void writeObject(DataOutput output) throws SerializableException;

    T readObject(DataInput input) throws SerializableException;

    static <T extends ThunderSerializable> T newInstance(Class<T> serializableClass) throws SerializableException {
        try {
            return serializableClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SerializableException(e);
        }
    }

    static void writeSerializableObject(DataOutput output, Serializable obj) throws SerializableException {
        byte[] bytes = serializeObject(obj);
        try {
            output.writeInt(bytes.length);
            output.write(bytes);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    static <T extends Serializable> T readSerializableObject(DataInput input) throws SerializableException {
        try {
            int size = input.readInt();
            byte[] bytes = readBytes(input, size);
            return deserializeObject(bytes);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    static byte[] serializeObject(Serializable obj) throws SerializableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(obj);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
        return baos.toByteArray();
    }

    static <T extends Serializable> T deserializeObject(byte[] bytes) throws SerializableException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream in = new ObjectInputStream(bais);
            //noinspection unchecked
            return (T) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new SerializableException(e);
        }
    }

    static byte[] readBytes(DataInput input, final int size) throws SerializableException {
        byte[] bytes = new byte[size];
        for (int idx = 0; idx < size; idx++) {
            try {
                bytes[idx] = input.readByte();
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        return bytes;
    }

}
