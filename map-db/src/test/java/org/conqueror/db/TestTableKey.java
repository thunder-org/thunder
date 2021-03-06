package org.conqueror.db;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.serialize.ThunderSerializable;
import org.mapdb.Serializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TestTableKey implements ThunderSerializable<TestTableKey> {

	private static final TestTableKey EmptyTestTableKey = new TestTableKey();

	private String value;

	public TestTableKey() {
		this(null);
	}

	public TestTableKey(String value) {
		this.value = value;
	}

	public String getKey() {
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
			output.writeUTF(getKey());
		} catch (IOException e) {
			throw new SerializableException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public TestTableKey readObject(DataInput input) throws SerializableException {
		try {
			new TestTableKey(input.readUTF());
		} catch (IOException e) {
			throw new SerializableException(e.getMessage(), e.getCause());
		}

		return this;
	}

	public static Serializer<TestTableKey> getSerializer() {
//		return ThunderSerializable.getSerializer(TestTableKey.class);
        return null;
	}

	public static TestTableKey getEmptyTestTableKey() {
		return EmptyTestTableKey;
	}

}
