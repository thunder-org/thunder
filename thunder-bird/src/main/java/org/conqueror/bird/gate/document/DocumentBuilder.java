package org.conqueror.bird.gate.document;

import org.conqueror.bird.exceptions.document.DocumentException;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.bird.gate.source.schema.FieldSchema;
import org.conqueror.bird.gate.source.schema.FieldSchemaUsingIndexingFieldData;
import org.conqueror.bird.gate.source.schema.FieldSchemaUsingSourceValue;

import java.util.Map;


public class DocumentBuilder {

    private final int maxIndexNameSize;

    public DocumentBuilder(int maxIndexNameSize) {
        this.maxIndexNameSize = maxIndexNameSize;
    }

    public Document buildDocument(DocumentSchema docSchema, Map<String, Object> source) throws DocumentException {
        if (source == null) return null;

        final Object[] fieldValues = new Object[source.size()];
        for (FieldSchema fieldSchema : docSchema.getFieldSchemas()) {
            if (fieldSchema instanceof FieldSchemaUsingIndexingFieldData) continue;

            try {
                Object value = fieldSchema.processedValue(source);

                if (FieldSchema.isFilteredOutValue(value)) {
                    return null;
                }

                if (fieldSchema.isIndexingField()) {
                    fieldValues[fieldSchema.getFieldNumber()] = value;
                }
            } catch (Exception e) {
                throw new DocumentException(e);
            }
        }

        return new Document(makeIndexName(docSchema, source), docSchema, fieldValues);
    }

    private String makeIndexName(DocumentSchema schema, Map<String, Object> source) {
        return FieldSchemaUsingSourceValue.makeFieldValue(schema.getIndexNameExpression(), source, maxIndexNameSize);
    }

}
