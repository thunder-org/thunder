package org.conqueror.lion.schedule.store;

import org.mapdb.Serializer;

import java.io.*;


public interface JobStoreSerializer<T> extends Serializer<T> {

    static void writeSerializableObject(DataOutput output, Serializable obj) throws IOException {
        byte[] bytes = serializeObject(obj);
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    static <T extends Serializable> T readSerializableObject(DataInput input) throws IOException {
        int size = input.readInt();
        byte[] bytes = readBytes(input, size);
        return deserializeObject(bytes);
    }

    static byte[] serializeObject(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(obj);
        return baos.toByteArray();
    }

    static <T extends Serializable> T deserializeObject(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream in = new ObjectInputStream(bais);
            return (T) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    static byte[] readBytes(DataInput input, final int size) throws IOException {
        byte[] bytes = new byte[size];
        for (int idx = 0; idx < size; idx++) {
            bytes[idx] = input.readByte();
        }

        return bytes;
    }

}
