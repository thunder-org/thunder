package org.conqueror.db.serializable;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;
import org.jetbrains.annotations.NotNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public interface DBSerializer<T extends ThunderSerializable> extends ThunderSerializable<T> {

    Map<Class, Serializer> serializers = new HashMap<>();

    @SuppressWarnings("unchecked")
    static <T extends ThunderSerializable> Serializer<T> getSerializer(Class<T> serializableClass) {
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

}
