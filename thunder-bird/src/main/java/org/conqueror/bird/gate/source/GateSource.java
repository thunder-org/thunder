package org.conqueror.bird.gate.source;

import org.conqueror.bird.data.BirdData;
import org.conqueror.bird.gate.parser.Parser;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.lion.exceptions.Serialize.SerializableException;

import java.io.DataOutput;
import java.io.IOException;


public abstract class GateSource implements BirdData {

    protected final DocumentSchema[] schemas;
    protected final Parser parser;

    public GateSource(DocumentSchema schemas, Parser parser) {
        this(new DocumentSchema[]{schemas}, parser);
    }

    public GateSource(DocumentSchema[] schemas, Parser parser) {
        this.schemas = schemas != null? schemas : new DocumentSchema[0];
        this.parser = parser;
    }

    public DocumentSchema getSchema() {
        return (schemas.length > 0) ? schemas[0] : null;
    }

    public DocumentSchema[] getSchemas() {
        return schemas;
    }

    public boolean hasMultiSchemas() {
        return schemas.length > 1;
    }

    public Parser getParser() {
        return parser;
    }

    public boolean isOver() {
        return this instanceof EmptyGateSource;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(getSchemas().length);
            for (DocumentSchema docSchema : getSchemas()) {
                docSchema.writeObject(output);
            }
            output.writeUTF(parser.getClass().getName());
            parser.writeObject(output);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
