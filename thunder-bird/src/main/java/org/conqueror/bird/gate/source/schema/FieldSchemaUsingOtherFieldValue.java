package org.conqueror.bird.gate.source.schema;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.json.simple.JSONObject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;


public class FieldSchemaUsingOtherFieldValue extends FieldSchema {

    public enum FieldType {EXIST, START_WITH}

    public enum ToType {STRING, INTEGER, LONG, BOOLEAN}

    protected static final String SRC_FIELD_NAME = "src_field_name";
    protected static final String INDEX_FIELD_NAME = "index_field_name";
    protected static final String VALUE_EXPRESSION = "value";
    protected static final String TO_TYPE = "to_type";
    protected static final String FIELD_TYPE = "type";

    protected final String[] expressions;  // @{field_name_1} + "text" + @{field_name_2} -> @{field_name_1}, "text", @{field_name_2}
    protected ToType toType = null;
    protected FieldType fieldType = null;

    protected FieldSchemaUsingOtherFieldValue(String srcFieldName, String indexFieldName, String[] expressions, String toType, String fieldType, int fieldNumber) {
        super(srcFieldName, indexFieldName, fieldNumber);
        this.expressions = expressions;
        try {
            if (toType != null) {
                this.toType = ToType.valueOf(toType.toUpperCase(Locale.ENGLISH));
            }
            if (fieldType != null) {
                this.fieldType = FieldType.valueOf(fieldType.toUpperCase(Locale.ENGLISH));
            }
        } catch (IllegalArgumentException ignored) {
        }

    }

    protected FieldSchemaUsingOtherFieldValue(String srcFieldName, String indexFieldName, String valueExpression, String toType, String fieldType, int fieldNumber) {
        this(srcFieldName, indexFieldName, (valueExpression != null) ? valueExpression.split("[ ]*\\+[ ]*") : null, toType, fieldType, fieldNumber);
    }

    public static FieldSchemaUsingOtherFieldValue build(JSONObject values, int fieldNumber) {
        return new FieldSchemaUsingOtherFieldValue(toStringValue(values.get(SRC_FIELD_NAME)), toStringValue(values.get(INDEX_FIELD_NAME))
            , toStringValue(values.get(VALUE_EXPRESSION)), toStringValue(values.get(TO_TYPE)), toStringValue(values.get(FIELD_TYPE)), fieldNumber);
    }

    @Override
    public Object processedValue(Map<String, Object> source) {
        Object value = null;
        if (fieldType != null) {
            switch (fieldType) {
                case START_WITH:
                    value = toStringValue(source.get(getSrcFieldName())).startsWith(expressions[0]);
                    break;
                case EXIST:
                    value = source.containsKey(getSrcFieldName());
                    break;
            }
        } else {
            value = FieldSchemaUsingSourceValue.makeFieldValue(expressions, source, 0);
            if (value != null && toType != null) {
                switch (toType) {
                    case STRING:
                        value = toStringValue(value);
                        break;
                    case INTEGER:
                        value = toIntegerValue(value);
                        break;
                    case LONG:
                        value = toLongValue(value);
                        break;
                    case BOOLEAN:
                        value = toBooleanValue(value);
                        break;
                }
            }
        }

        return (value != null) ? value : FILTERED_OUT_VALUE;
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            LionSerializable.writeSerializableObject(output, getValues());
            output.writeInt(getFieldNumber());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public FieldSchemaUsingOtherFieldValue readObject(DataInput input) throws SerializableException {
        try {
            JSONObject values = LionSerializable.readSerializableObject(input);
            int fieldNumber = input.readInt();

            return build(values, fieldNumber);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
