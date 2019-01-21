package org.conqueror.bird.index.source;


import org.conqueror.bird.data.messages.BirdMessage;
import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IndexContentQueue implements BirdMessage {

    private final String indexName;
    private final String mappingName;
    private final List<IndexContent> queue;
    private final int capacity;

    public IndexContentQueue() {
        this(null, null, 0);
    }

    /*
     * using a variable capacity queue for waiting queue
     */
    public IndexContentQueue(String indexName, String mappingName, int capacity) {
        queue = new ArrayList<>(capacity);
        this.indexName = indexName;
        this.mappingName = mappingName;
        this.capacity = capacity;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getMappingName() {
        return mappingName;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean put(IndexContent content) {
        queue.add(content);
        return true;
    }

    public boolean put(List<IndexContent> contents) {
        queue.addAll(contents);
        return true;
    }

    public IndexContent take() {
        if (!queue.isEmpty()) {
            return queue.remove(0);
        }
        return null;
    }

    public List<IndexContent> getQueue() {
        return queue;
    }

    public int remainElementsSize() {
        return queue.size();
    }

    public boolean isFull() {
        return (queue.size() >= capacity);
    }

    public void clear() {
        queue.clear();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getIndexName());
            output.writeUTF(getMappingName());
            output.writeInt(queue.size());
            for (IndexContent content : queue) {
                content.writeObject(output);
            }
            output.writeInt(capacity);
        } catch (IOException e) {
            throw new SerializableException();
        }
    }

    @Override
    public IndexContentQueue readObject(DataInput input) throws SerializableException {
        try {
            String indexName = input.readUTF();
            String mappingJson = input.readUTF();
            int size = input.readInt();
            List<IndexContent> contents = new ArrayList<>(size);
            for (int idx = 0; idx < size; idx++) {
                contents.add(IndexContent.getEmptyInstance().readObject(input));
            }
            int capacity = input.readInt();
            IndexContentQueue queue = new IndexContentQueue(indexName, mappingJson, capacity);
            queue.put(contents);

            return queue;
        } catch (IOException e) {
            throw new SerializableException();
        }
    }

}
