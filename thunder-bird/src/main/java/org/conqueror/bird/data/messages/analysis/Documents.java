package org.conqueror.bird.data.messages.analysis;

import org.conqueror.bird.gate.document.Document;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class Documents extends AnalysisMessage.AnalysisRequest {

    private List<Document> documents;

    public Documents() {
        this(new ArrayList<>(0));
    }

    public Documents(List<Document> documents) {
        this.documents = documents;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(documents.size());
            for (Document document : documents) {
                document.writeObject(output);
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public Documents readObject(DataInput input) throws SerializableException {
        try {
            int size = input.readInt();
            List<Document> documents = new ArrayList<>(size);
            for (int idx = 0; idx < size; idx++) {
                documents.add(Document.getInstance().readObject(input));
            }
            return new Documents(documents);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
