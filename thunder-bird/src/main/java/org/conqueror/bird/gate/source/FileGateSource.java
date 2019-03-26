package org.conqueror.bird.gate.source;

import org.conqueror.bird.gate.parser.Parser;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class FileGateSource extends GateSource {

    private int number;
    private final String fileUri;
    private int hashCode = 0;

    public FileGateSource() {
        super((DocumentSchema[]) null, null);
        fileUri = null;
    }

    public FileGateSource(String fileUri, DocumentSchema[] schemas, Parser parser) {
        super(schemas, parser);
        this.fileUri = fileUri;
    }

    public FileGateSource(String fileUri, DocumentSchema[] schemas, Parser parser, int number) {
        super(schemas, parser);
        this.fileUri = fileUri;
        setNumber(number);
    }

    public String getFileUri() {
        return fileUri;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("FileGateSource{fileUri=").append(fileUri);
        string.append(", schema=[");
        int schemaNum = 0;
        for (DocumentSchema schema : getSchemas()) {
            if (schemaNum++ > 0) string.append(" ,");
            string.append(schema.getSchemaName());
        }
        string.append("], parser=").append(parser);
        string.append(", number =").append(number);
        string.append('}');

        return string.toString();
    }

    @Override
    public int hashCode() {
        return (hashCode != 0) ? hashCode : (hashCode = toString().hashCode());
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            super.writeObject(output);
            output.writeUTF(getFileUri());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public FileGateSource readObject(DataInput input) throws SerializableException {
        try {
            int schemaSize = input.readInt();
            DocumentSchema[] schemas = new DocumentSchema[schemaSize];
            for (int schemaNum = 0; schemaNum < schemaSize; schemaNum++) {
                schemas[schemaNum] = DocumentSchema.getInstance().readObject(input);
            }
            String parserClassName = input.readUTF();
            Parser parser = (Parser) ((Parser) Class.forName(parserClassName).newInstance()).readObject(input);
            String fileUri = input.readUTF();
            return new FileGateSource(fileUri, schemas, parser);
        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new SerializableException(e);
        }
    }

}
