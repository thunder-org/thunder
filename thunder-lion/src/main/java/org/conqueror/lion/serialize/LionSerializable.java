package org.conqueror.lion.serialize;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public interface LionSerializable<T extends LionSerializable> extends Serializable {

    Map<Class, Serializer> serializers = new HashMap<>();

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    void writeObject(DataOutput output) throws SerializableException;

    T readObject(DataInput input) throws SerializableException;

    static <T extends LionSerializable> T newInstance(Class<T> serializableClass) throws SerializableException {
        try {
            return serializableClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SerializableException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends LionSerializable> Serializer<T> getSerializer(Class<T> serializableClass) {
        Serializer serializer = serializers.get(serializableClass);
        if (Objects.nonNull(serializer)) return serializer;

        synchronized (serializers) {
            serializer = serializers.get(serializableClass);
            if (serializer == null) {
                serializer = new Serializer<T>() {
                    @Override
                    public void serialize(@NotNull DataOutput2 out, @NotNull T value) throws IOException {
                        try {
                            value.writeObject(out);
                        } catch (SerializableException e) {
                            throw new IOException(e);
                        }
                    }

                    @Override
                    public T deserialize(@NotNull DataInput2 input, int available) throws IOException {
                        try {
                            T value = serializableClass.newInstance();
                            value.readObject(input);
                            return value;
                        } catch (InstantiationException | IllegalAccessException | SerializableException e) {
                            throw new IOException(e);
                        }
                    }
                };

                serializers.put(serializableClass, serializer);
            }

            return serializer;
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
