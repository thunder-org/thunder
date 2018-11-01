package org.conqueror.lion.serialize;

import akka.serialization.JSerializer;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.*;


public class LionSerializer extends JSerializer {

    // This is whether "fromBinary" requires a "clazz" or not
    @Override
    public boolean includeManifest() {
        return true;
    }

    // Pick a unique identifier for your Serializer,
    // you've got a couple of billions to choose from,
    // 0 - 40 is reserved by Akka itself
    @Override
    public int identifier() {
        return 19810309;
    }

    // "toBinary" serializes the given object to an Array of Bytes
    @Override
    public byte[] toBinary(Object obj) {
        try {
            if (obj instanceof LionSerializable) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
                        ((LionSerializable) obj).writeObject(out);
                    }
                } catch (IOException e) {
                    throw new SerializableException(e);
                }
                return baos.toByteArray();
            } else {
                throw new IllegalArgumentException("Unknown type: " + obj);
            }
        } catch (SerializableException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // "fromBinary" deserializes the given array,
    // using the type hint (if any, see "includeManifest" above)
    @Override
    public Object fromBinaryJava(byte[] bytes, Class<?> clazz) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            try (ObjectInputStream in = new ObjectInputStream(bais)) {
                return ((LionSerializable) clazz.newInstance()).readObject(in);
            }
        } catch (IOException | InstantiationException | IllegalAccessException | SerializableException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
