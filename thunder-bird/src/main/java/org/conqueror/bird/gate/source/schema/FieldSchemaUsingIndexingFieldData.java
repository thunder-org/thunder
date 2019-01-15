package org.conqueror.bird.gate.source.schema;

import org.conqueror.bird.analysis.AnalysisResult;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;


public class FieldSchemaUsingIndexingFieldData extends FieldSchemaUsingOtherFieldValue {

    protected FieldSchemaUsingIndexingFieldData(String indexFieldName, String[] expressions, int fieldNumber) {
        super(null, indexFieldName, expressions, null, null, fieldNumber);
    }

    protected FieldSchemaUsingIndexingFieldData(String indexFieldName, String valueExpression, int fieldNumber) {
        this(indexFieldName, valueExpression.split("[ ]*\\+[ ]*"), fieldNumber);
    }

    public static FieldSchemaUsingIndexingFieldData build(JSONObject values, int fieldNumber) {
        return new FieldSchemaUsingIndexingFieldData(toStringValue(values.get(INDEX_FIELD_NAME))
            , toStringValue(values.get(VALUE_EXPRESSION)), fieldNumber);
    }

    public static boolean isFieldSchemaUsingIndexFieldData(JSONObject values) {
        return values.containsKey(VALUE_EXPRESSION)
            && (toStringValue(values.get(VALUE_EXPRESSION))).contains("${");
    }

    public Object processedValue(Document document, Map<String, AnalysisResult[]> analysisResults) {
        return processedValue(document, analysisResults, 0);
    }

    public Object processedValue(Document document, Map<String, AnalysisResult[]> analysisResults, int analysisResultNumber) {
        if (expressions.length > 1) {
            return makeFieldValue(expressions, document, analysisResults, analysisResultNumber, 0);
        } else if (expressions.length == 1) {
            return makeFieldValue(expressions[0], document, analysisResults, analysisResultNumber);
        }
        return null;
    }

    public static String makeFieldValue(String[] expressions, Document document, Map<String, AnalysisResult[]> analysisResults, int analysisResultNumber, int maxLength) {
        StringBuilder fieldValue = new StringBuilder(maxLength > 0 ? maxLength : 1024);
        for (String expression : expressions) {
            if (expression.length() == 0) continue;
            Object value = makeFieldValue(expression, document, analysisResults, analysisResultNumber);
            fieldValue.append(value);
        }
        return fieldValue.toString();
    }

    public static Object makeFieldValue(String expression, Document document, Map<String, AnalysisResult[]> analysisResults, int analysisResultNumber) {
        try {
            /*
                ${index_field_name:begin_idx~end_idx}
                ${index_field_name:dateformat->dateformat}
                ${index_field_name.indexterm}
                ${index_field_name}
              */
            if (expression.startsWith("$")) {
                int sidx = expression.indexOf('{');
                int eidx = expression.lastIndexOf('}');
                if (sidx != -1 && eidx != -1) {
                    String fieldName = expression.substring(sidx + 1, eidx);
                    if (fieldName.indexOf(':') != -1) {
                        String[] segments = fieldName.split("[ ]*:[ ]*");
                        fieldName = segments[0];

                        if (segments[1].indexOf('~') != -1) {
                            // substring
                            segments = segments[1].split("[ ]*~[ ]*");
                            return document.getStringFieldValueByIndexFieldName(fieldName)
                                .substring(Integer.parseInt(segments[0]) - 1, Integer.parseInt(segments[1]));
                        } else if (segments[1].contains("->")) {
                            // datetime
                            segments = segments[1].split("[ ]*->[ ]*");
                            DateTime date = document.getDateTimeFieldValueByIndexFieldName(fieldName);
                            return date.toString(segments[1]);
                        }
                    } else if (fieldName.indexOf('.') != -1) {
                        String[] segments = fieldName.split("\\.");
                        return analysisResults.get(segments[0])[analysisResultNumber].getResult(segments[1]);
                    } else {
                        return document.getFieldValueByIndexFieldName(fieldName);
                    }
                }
            } else if (expression.startsWith("\"")) {
                int sidx = expression.indexOf('"');
                int eidx = expression.lastIndexOf('"');
                if (sidx != -1 && eidx != -1) {
                    return expression.substring(sidx, eidx);
                }
            } else {
                return expression;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FieldSchemaUsingIndexingFieldData readObject(DataInput input) throws SerializableException {
        try {
            JSONObject values = LionSerializable.readSerializableObject(input);
            int fieldNumber = input.readInt();

            return build(values, fieldNumber);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
