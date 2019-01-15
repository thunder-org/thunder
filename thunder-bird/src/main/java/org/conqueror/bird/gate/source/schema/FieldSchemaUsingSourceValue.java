package org.conqueror.bird.gate.source.schema;

import org.conqueror.bird.exceptions.schema.SchemaException;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.conqueror.lion.serialize.LionSerializer;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class FieldSchemaUsingSourceValue extends FieldSchema {

    private static final Logger logger = LoggerFactory.getLogger(FieldSchemaUsingSourceValue.class);

    public enum FieldType {STRING, INTEGER, LONG, FLOAT, DOUBLE, DATETIME, BOOLEAN}

    public enum ValueConditionSymbol {LT, LE, GT, GE, EQ, NE, IN, NOT_IN}

    protected static final String VALUE_TYPE = "type";
    protected static final String INDEX_FIELD_NAME = "index_field_name";
    protected static final String MUST_EXIST = "must_exist";
    protected static final String MAX_LENGTH = "max_length";
    protected static final String DEFAULT_VALUE = "default_value";
    protected static final String CONDITION_VALUE = "condition_value";
    protected static final String CONDITION_SYMBOL = "condition_symbol";
    protected static final String DATE_FORMAT = "format";

    private final FieldType fieldType;
    private final boolean mustExist;
    private Integer maxLength = null;
    private Object defaultValue = null;
    private String[] defaultValueExpressions = null;
    private Object[] conditionValues = null;
    private ValueConditionSymbol conditionSymbol = null;
    private String[] dateTimeFormats = null;

    private FieldSchemaUsingSourceValue(String srcFieldName, String indexFieldName, FieldType fieldType, boolean mustExist, int fieldNumber) {
        super(srcFieldName, indexFieldName, fieldNumber);
        this.fieldType = fieldType;
        this.mustExist = mustExist;
    }

    public static FieldSchemaUsingSourceValue build(String srcFieldName, JSONObject values, int fieldNumber) throws SchemaException {
        String indexFieldName = toStringValue(values.get(INDEX_FIELD_NAME));
        Boolean mustExist = toBooleanValue(values.get(MUST_EXIST));
        if (mustExist == null) mustExist = false;
        Object defaultValue = values.get(DEFAULT_VALUE);
        Object conditionValue = values.get(CONDITION_VALUE);
        FieldSchemaUsingSourceValue.ValueConditionSymbol conditionSymbol = values.containsKey(CONDITION_SYMBOL) ?
            FieldSchemaUsingSourceValue.ValueConditionSymbol.valueOf(toStringValue(values.get(CONDITION_SYMBOL)).toUpperCase())
            : null;
        FieldType fieldType = FieldType.valueOf(toStringValue(values.get(VALUE_TYPE)).toUpperCase());
        switch (fieldType) {
            case STRING:
                Integer maxLength = toIntegerValue(values.get(MAX_LENGTH));
                return (FieldSchemaUsingSourceValue) buildStringFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist, maxLength
                    , toStringValue(defaultValue), toStringArrayValue(conditionValue), conditionSymbol)
                    .setValues(values);
            case INTEGER:
                return (FieldSchemaUsingSourceValue) buildIntegerFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toIntegerValue(defaultValue), toIntegerArrayValue(conditionValue), conditionSymbol)
                    .setValues(values);
            case LONG:
                return (FieldSchemaUsingSourceValue) buildLongFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toLongValue(defaultValue), toLongArrayValue(conditionValue), conditionSymbol)
                    .setValues(values);
            case FLOAT:
                return (FieldSchemaUsingSourceValue) buildFloatFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toFloatValue(defaultValue), toFloatArrayValue(conditionValue), conditionSymbol)
                    .setValues(values);
            case DOUBLE:
                return (FieldSchemaUsingSourceValue) buildDoubleFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toDoubleValue(defaultValue), toDoubleArrayValue(conditionValue), conditionSymbol)
                    .setValues(values);
            case DATETIME:
                String[] dateTimeFormats = toStringArrayValue(values.get(DATE_FORMAT));
                return (FieldSchemaUsingSourceValue) buildDateTimeFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toDateTimeValue(defaultValue, dateTimeFormats)
                    , toDateTimeValue(conditionValue, dateTimeFormats)
                    , conditionSymbol, dateTimeFormats)
                    .setValues(values);
            case BOOLEAN:
                return (FieldSchemaUsingSourceValue) buildBooleanFieldSchema(srcFieldName, indexFieldName, fieldNumber, mustExist
                    , toBooleanValue(defaultValue), toBooleanValue(conditionValue), conditionSymbol)
                    .setValues(values);
            default:
                return null;
        }
    }

    public static boolean isFieldSchemaUsingSourceValue(JSONObject values) {
        if (values.containsKey(VALUE_TYPE)) {
            try {
                FieldType.valueOf(toStringValue(values.get(VALUE_TYPE)).toUpperCase());
                return true;
            } catch (IllegalArgumentException ignored) {
            }
        }
        return false;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public FieldSchemaUsingSourceValue setMaxLength(Integer maxLength) {
        if (maxLength != null) {
            this.maxLength = maxLength;
        }
        return this;
    }

    public FieldSchemaUsingSourceValue setDefaultValue(Object defaultValue) {
        if (defaultValue != null) {
            if (fieldType.equals(FieldType.STRING) && ((String) defaultValue).indexOf('@') != -1) {
                defaultValueExpressions = ((String) defaultValue).split("[ ]*\\+[ ]*");
            } else {
                this.defaultValue = defaultValue;
            }
        }
        return this;
    }

    public FieldSchemaUsingSourceValue setConditionValue(Object conditionValue) {
        if (conditionValue != null) {
            this.conditionValues = new Object[]{conditionValue};
        }
        return this;
    }

    public FieldSchemaUsingSourceValue setConditionValue(Object[] conditionValues) {
        if (conditionValues != null) {
            this.conditionValues = conditionValues;
        }
        return this;
    }

    public FieldSchemaUsingSourceValue setConditionSymbol(ValueConditionSymbol conditionSymbol) {
        if (conditionSymbol != null) {
            this.conditionSymbol = conditionSymbol;
        }
        return this;
    }

    public FieldSchemaUsingSourceValue setDateTimeFormats(String[] dateTimeFormats) {
        if (dateTimeFormats != null) {
            this.dateTimeFormats = dateTimeFormats;
        }
        return this;
    }

    public static FieldSchemaUsingSourceValue buildStringFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Integer maxLength, String defaultValue, String[] conditionValues
        , ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.STRING, mustExist, fieldNumber)
            .setMaxLength(maxLength)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValues)
            .setConditionSymbol(conditionSymbol);
    }

    public static FieldSchemaUsingSourceValue buildIntegerFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Integer defaultValue, Integer[] conditionValues, ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.INTEGER, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValues)
            .setConditionSymbol(conditionSymbol);
    }

    public static FieldSchemaUsingSourceValue buildLongFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Long defaultValue, Long[] conditionValues, ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.LONG, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValues)
            .setConditionSymbol(conditionSymbol);
    }

    public static FieldSchemaUsingSourceValue buildFloatFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Float defaultValue, Float[] conditionValues, ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.FLOAT, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValues)
            .setConditionSymbol(conditionSymbol);
    }

    public static FieldSchemaUsingSourceValue buildDoubleFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Double defaultValue, Double[] conditionValues, ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.DOUBLE, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValues)
            .setConditionSymbol(conditionSymbol);
    }

    public static FieldSchemaUsingSourceValue buildDateTimeFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, DateTime defaultValue, DateTime conditionValue
        , ValueConditionSymbol conditionSymbol, String[] dateTimeFormats) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.DATETIME, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValue)
            .setConditionSymbol(conditionSymbol)
            .setDateTimeFormats(dateTimeFormats);
    }

    public static FieldSchemaUsingSourceValue buildBooleanFieldSchema(String srcFieldName, String indexFieldName
        , int fieldNumber, boolean mustExist, Boolean defaultValue, Boolean conditionValue, ValueConditionSymbol conditionSymbol) {
        return new FieldSchemaUsingSourceValue(srcFieldName, indexFieldName, FieldType.BOOLEAN, mustExist, fieldNumber)
            .setDefaultValue(defaultValue)
            .setConditionValue(conditionValue)
            .setConditionSymbol(conditionSymbol);
    }

    @Override
    public Object processedValue(Map<String, Object> source) throws SchemaException {
        Object value = source.get(getSrcFieldName());
        Object tranValue;
        switch (fieldType) {
            case STRING:
                tranValue = toStringValue(value);
                break;
            case INTEGER:
                tranValue = toIntegerValue(value);
                break;
            case LONG:
                tranValue = toLongValue(value);
                break;
            case FLOAT:
                tranValue = toFloatValue(value);
                break;
            case DOUBLE:
                tranValue = toDoubleValue(value);
                break;
            case DATETIME:
                tranValue = toDateTimeValue(value, dateTimeFormats);
                break;
            case BOOLEAN:
                tranValue = toBooleanValue(value);
                break;
            default:
                tranValue = value;
        }

        if (tranValue == null) {
            if (defaultValue != null) {
                tranValue = defaultValue;
            } else if (defaultValueExpressions != null) {
                tranValue = makeFieldValue(defaultValueExpressions, source, 1024);
            } else if (mustExist) {
                tranValue = FILTERED_OUT_VALUE;
            }
        } else if (!isProperValue(tranValue)) {
            tranValue = FILTERED_OUT_VALUE;
        }

        return tranValue;
    }

    private boolean isProperValue(Object value) {
        // null check
        if (value == null) {
            return false;
        }

        // length check
        if (maxLength != null) {
            if (((String) value).length() > maxLength) return false;
        }

        // value condition check
        if (conditionValues != null && conditionSymbol != null) {
            int ret = compare(value, conditionValues[0]);
            switch (conditionSymbol) {
                case EQ:
                    return (ret == 0);
                case GE:
                    return (ret >= 0);
                case GT:
                    return (ret > 0);
                case LE:
                    return (ret <= 0);
                case LT:
                    return (ret < 0);
                case NE:
                    return (ret != 0);
                case IN:
                    for (Object conditionValue : conditionValues) {
                        if (compare(conditionValue, value) == 0) return true;
                    }
                    return false;
                case NOT_IN:
                    for (Object conditionValue : conditionValues) {
                        if (compare(conditionValue, value) == 0) return false;
                    }
                    return true;
                default:
                    return false;
            }
        }

        return true;
    }

    private static final int NOT_COMPARE_VALUE = -9999;

    private static int compare(Object src, Object dst) {
        if (src instanceof String) {
            return ((String) src).compareTo((String) dst);
        } else if (src instanceof Integer) {
            return ((Integer) src).compareTo((Integer) dst);
        } else if (src instanceof Long) {
            return ((Long) src).compareTo((Long) dst);
        } else if (src instanceof DateTime) {
            return ((DateTime) src).compareTo((DateTime) dst);
        } else if (src instanceof Boolean) {
            return ((Boolean) src).compareTo((Boolean) dst);
        }

        return NOT_COMPARE_VALUE;
    }

    public static String makeFieldValue(String[] expressions, Map<String, Object> source, int maxLength) {
        StringBuilder fieldValue = new StringBuilder(maxLength > 0 ? maxLength : 1024);
        try {
            for (String expression : expressions) {
                if (expression.length() == 0) continue;
                if (expression.startsWith("@")) {
                    int sidx = expression.indexOf('{');
                    int eidx = expression.lastIndexOf('}');
                    if (sidx != -1 && eidx != -1) {
                        String fieldName = expression.substring(sidx + 1, eidx);
                        String value = null;
                        if (fieldName.indexOf(':') != -1) {
                            String[] segments = fieldName.split("[ ]*:[ ]*", 2);
                            fieldName = segments[0];

                            if (segments[1].indexOf('~') != -1) {
                                // substring
                                segments = segments[1].split("[ ]*~[ ]*");
                                value = toStringValue(source.get(fieldName))
                                    .substring(Integer.parseInt(segments[0]) - 1, Integer.parseInt(segments[1]));
                            } else if (segments[1].contains("->")) {
                                // datetime
                                segments = segments[1].split("[ ]*->[ ]*");
                                DateTime date = toDateTimeValue(source.get(fieldName), segments[0]);
                                value = date.toString(segments[1]);
                            }
                        } else {
                            value = toStringValue(source.get(fieldName));
                        }

                        if (value != null) fieldValue.append(value);
                    }
                } else if (expression.startsWith("\"")) {
                    int sidx = expression.indexOf('"');
                    int eidx = expression.lastIndexOf('"');
                    if (sidx != -1 && eidx != -1 && sidx + 1 < eidx) {
                        fieldValue.append(expression, sidx + 1, eidx);
                    }
                } else {
                    fieldValue.append(expression);
                }
            }
        } catch (Exception e) {
            logger.error("failed to make field value : {} / {}", Arrays.toString(expressions), source);
            return null;
        }
        return fieldValue.toString();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getSrcFieldName());
            LionSerializable.writeSerializableObject(output, getValues());
            output.writeInt(getFieldNumber());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public LionSerializable readObject(DataInput input) throws SerializableException {
        try {
            return FieldSchemaUsingSourceValue.build(input.readUTF(), LionSerializable.readSerializableObject(input), input.readInt());
        } catch (SchemaException | IOException e) {
            throw new SerializableException(e);
        }
    }

}
