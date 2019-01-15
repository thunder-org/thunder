package org.conqueror.bird.gate.document;

import org.conqueror.bird.data.BirdData;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.joda.time.DateTime;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;


public class Document implements BirdData {

    private static final Document instance = new Document();

    private final String indexName;
    private final DocumentSchema schema;
    private final Object[] fieldValues;

    public Document() {
        this(null, null, null);
    }

    public Document(String indexName, DocumentSchema schema, Object[] fieldValues) {
        this.indexName = indexName;
        this.schema = schema;
        this.fieldValues = fieldValues;
    }

    public static Document getInstance() {
        return instance;
    }

    public String getIndexName() {
        return indexName;
    }

    public DocumentSchema getSchema() {
        return schema;
    }

    public String getParentId() {
        return getStringFieldValueByIndexFieldName(schema.getIdFieldName());
    }

    public String getType() {
        return schema.getType();
    }

    public String getChildIdFieldName() {
        return schema.getChildIdFieldName();
    }

    public String getChildType() {
        return schema.getChildType();
    }

    public Object getOnlyOneKey() {
        return schema.isOnlyOne() ? getFieldValueByIndexFieldName(schema.getOnlyOneKey()) : null;
    }

    public Object getOnlyOneValue() {
        return schema.isOnlyOne() ? getFieldValueByIndexFieldName(schema.getOnlyOneField()) : null;
    }

    public Object getFieldValue(int index) {
        return fieldValues[index];
    }

    public Object getFieldValueBySrcFieldName(String fieldName) {
        return fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public Object getFieldValueByIndexFieldName(String fieldName) {
        return fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public String getStringFieldValue(int index) {
        return (String) fieldValues[index];
    }

    public String getStringFieldValueBySrcFieldName(String fieldName) {
        return (String) fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public String getStringFieldValueByIndexFieldName(String fieldName) {
        return (String) fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public Integer getIntegerFieldValue(int index) {
        return (Integer) fieldValues[index];
    }

    public Integer getIntegerFieldValueBySrcFieldName(String fieldName) {
        return (Integer) fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public Integer getIntegerFieldValueByIndexFieldName(String fieldName) {
        return (Integer) fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public Long getLongFieldValue(int index) {
        return (Long) fieldValues[index];
    }

    public Long getLongFieldValueBySrcFieldName(String fieldName) {
        return (Long) fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public Long getLongFieldValueByIndexFieldName(String fieldName) {
        return (Long) fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public DateTime getDateTimeFieldValue(int index) {
        return (DateTime) fieldValues[index];
    }

    public DateTime getDateTimeFieldValueBySrcFieldName(String fieldName) {
        return (DateTime) fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public DateTime getDateTimeFieldValueByIndexFieldName(String fieldName) {
        return (DateTime) fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public Boolean getBooleanFieldValue(int index) {
        return (Boolean) fieldValues[index];
    }

    public Boolean getBooleanFieldValueBySrcFieldName(String fieldName) {
        return (Boolean) fieldValues[schema.getFieldSchemaBySrcFieldName(fieldName).getFieldNumber()];
    }

    public Boolean getBooleanFieldValueByIndexFieldName(String fieldName) {
        return (Boolean) fieldValues[schema.getFieldSchemaByIndexFieldName(fieldName).getFieldNumber()];
    }

    public boolean isOver() {
        return this instanceof EmptyDocument;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder(1024);

        string.append("Document:{ ");
        int num = 0;
        for (Object value : fieldValues) {
            if (num++ > 0) string.append(',');
            string.append(value)
                .append('(')
                .append(value != null ? value.getClass().toString() : "null")
                .append(')');
        }
        string.append(" }");

        return string.toString();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getIndexName());
            schema.writeObject(output);
            output.writeInt(fieldValues.length);
            for (Object value : fieldValues) {
                if (!(value instanceof Serializable))
                    throw new SerializableException("field value is not serializable");
                LionSerializable.writeSerializableObject(output, (Serializable) value);
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public Document readObject(DataInput input) throws SerializableException {
        try {
            String indexName = input.readUTF();
            DocumentSchema documentSchema = DocumentSchema.getInstance().readObject(input);
            int fieldSize = input.readInt();
            Object[] fieldValues = new Object[fieldSize];
            for (int fieldNum = 0; fieldNum < fieldSize; fieldNum++) {
                fieldValues[fieldNum] = LionSerializable.readSerializableObject(input);
            }

            return new Document(indexName, documentSchema, fieldValues);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }
}
