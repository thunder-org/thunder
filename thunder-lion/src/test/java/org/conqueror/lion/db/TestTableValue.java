package org.conqueror.lion.db;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class TestTableValue implements LionSerializable<TestTableValue> {

    private static final TestTableValue EmptyTestTableValue = new TestTableValue();

    private String value;

    private TestTableValue() {
        this(null);
    }

    public TestTableValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass() && hashCode() == obj.hashCode();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getValue());
        } catch (IOException e) {
            throw new SerializableException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public TestTableValue readObject(DataInput input) throws SerializableException {
        try {
            return new TestTableValue(input.readUTF());
        } catch (IOException e) {
            throw new SerializableException(e.getMessage(), e.getCause());
        }
    }

    public static Serializer<TestTableValue> getSerializer() {
        return LionSerializable.getSerializer(TestTableValue.class);
    }

    public static TestTableValue getEmptyInstance() {
        return EmptyTestTableValue;
    }

}
