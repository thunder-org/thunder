package org.conqueror.bird.gate.source.schema;

import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.conqueror.lion.serialize.LionSerializable;
import org.json.simple.JSONObject;

import java.io.DataInput;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class FieldSchemaUsingAnalyzer extends FieldSchemaUsingOtherFieldValue {

    public enum AnalysisItem {KEYWORD, SRCHWORD, FEATURE}

    protected static final String ANALYSIS_ITEMS = "analysis";

    private final Set<AnalysisItem> analysisItems = new HashSet<>(AnalysisItem.values().length);


    protected FieldSchemaUsingAnalyzer(String indexFieldName, String[] expressions, int fieldNumber) {
        super(null, indexFieldName, expressions, null, null, fieldNumber);
    }

    protected FieldSchemaUsingAnalyzer(String indexFieldName, String valueExpression, int fieldNumber) {
        this(indexFieldName, valueExpression.split("[ ]*\\+[ ]*"), fieldNumber);
    }

    public static FieldSchemaUsingAnalyzer build(JSONObject values, int fieldNumber) {
        return new FieldSchemaUsingAnalyzer(toStringValue(values.get(INDEX_FIELD_NAME)), toStringValue(values.get(VALUE_EXPRESSION)), fieldNumber)
            .addAnalysisItems(toStringArrayValue(values.get(ANALYSIS_ITEMS)));
    }

    public static boolean isFieldSchemaUsingAnalyzer(JSONObject values) {
        return values.containsKey(ANALYSIS_ITEMS);
    }

    public Set<AnalysisItem> getAnalysisItems() {
        return analysisItems;
    }

    public boolean hasAnalysisItem(AnalysisItem item) {
        return analysisItems.contains(item);
    }

    public FieldSchemaUsingAnalyzer addAnalysisItems(AnalysisItem[] items) {
        Collections.addAll(this.analysisItems, items);
        return this;
    }

    public FieldSchemaUsingAnalyzer addAnalysisItems(String[] items) {
        for (String item : items) {
            addAnalysisItem(item);
        }
        return this;
    }

    public FieldSchemaUsingAnalyzer addAnalysisItem(AnalysisItem item) {
        this.analysisItems.add(item);
        return this;
    }

    public FieldSchemaUsingAnalyzer addAnalysisItem(String item) {
        this.analysisItems.add(AnalysisItem.valueOf(item.toUpperCase(Locale.ENGLISH)));
        return this;
    }

    @Override
    public FieldSchemaUsingAnalyzer readObject(DataInput input) throws SerializableException {
        try {
            JSONObject values = LionSerializable.readSerializableObject(input);
            int fieldNumber = input.readInt();

            return build(values, fieldNumber);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
