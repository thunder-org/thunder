package org.conqueror.bird.data.messages.index;

import org.conqueror.es.client.index.source.IndexContent;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class IndexContents extends IndexMessage.IndexRequest {

    private List<IndexContent> contents;

    private IndexContents() {
        this(new ArrayList<>(0));
    }

    public IndexContents(List<IndexContent> contents) {
        this.contents = contents;
    }

    public List<IndexContent> getIndexContents() {
        return contents;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(contents.size());
            for (IndexContent content : contents) {
                content.writeObject(output);
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public IndexContents readObject(DataInput input) throws SerializableException {
        try {
            int size = input.readInt();
            List<IndexContent> contents = new ArrayList<>(size);
            for (int idx = 0; idx < size; idx++) {
                contents.add(IndexContent.getEmptyInstance().readObject(input));
            }
            return new IndexContents(contents);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
