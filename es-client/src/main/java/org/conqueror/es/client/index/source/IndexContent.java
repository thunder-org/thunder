package org.conqueror.es.client.index.source;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.message.LionMessage;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class IndexContent implements LionMessage {

    private final String indexName;
    private final String mappingName;
    private final String type;
    private final String id;
    private final byte[] content;

    private final boolean putIfAbsent;

    private static final IndexContent instance = new IndexContent();

    private IndexContent() {
        this(null, null, null, null, (byte[]) null, false);
    }

    public IndexContent(String indexName, String mappingJson, String type, String id, XContentBuilder content, boolean putIfAbsent) {
        this(indexName, mappingJson, type, id, BytesReference.bytes(content).toBytesRef().bytes, putIfAbsent);
    }

    public IndexContent(String indexName, String mappingName, String type, String id, byte[] content, boolean putIfAbsent) {
        this.indexName = indexName;
        this.mappingName = mappingName;
        this.type = type;
        this.id = id;
        this.content = content;
        this.putIfAbsent = putIfAbsent;
    }

    public static IndexContent getEmptyInstance() {
        return instance;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getMappingName() {
        return mappingName;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public byte[] getContent() {
        return content;
    }

    public boolean isPutIfAbsent() {
        return putIfAbsent;
    }

    public String toString() {
        return indexName + ":" + type + ":" + id + ":" + putIfAbsent + ":" + toJson(getContent());
    }

    public static final String toJson(byte[] bytes) {
        try {
            return XContentHelper.convertToJson(new BytesArray(bytes), true, true, XContentType.JSON);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getIndexName());
            output.writeUTF(getMappingName());
            output.writeUTF(getType());
            output.writeUTF(getId());
            output.writeInt(getContent().length);
            output.write(getContent());
            output.writeBoolean(isPutIfAbsent());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public IndexContent readObject(DataInput input) throws SerializableException {
        try {
            String indexName = input.readUTF();
            String mappingName = input.readUTF();
            String type = input.readUTF();
            String id = input.readUTF();
            int size = input.readInt();
            byte[] content = new byte[size];
            input.readFully(content);
            boolean putIfAbsent = input.readBoolean();
            return new IndexContent(indexName, mappingName, type, id, content, putIfAbsent);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
