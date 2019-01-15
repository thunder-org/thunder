package org.conqueror.bird.analysis;

import akka.event.LoggingAdapter;
import org.conqueror.bird.gate.document.Document;
import org.conqueror.bird.gate.source.schema.DocumentSchema;
import org.conqueror.bird.gate.source.schema.FieldSchema;
import org.conqueror.bird.gate.source.schema.FieldSchemaUsingAnalyzer;
import org.conqueror.bird.gate.source.schema.FieldSchemaUsingIndexingFieldData;
import org.conqueror.es.index.source.IndexContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Analyzer {

    protected LoggingAdapter log;

    public Analyzer(LoggingAdapter log) {
        this.log = log;
    }

    public IndexContent analyze(Document doc) {
        DocumentSchema docSchema = doc.getSchema();
        try {
            Map<String, AnalysisResult[]> analysisResults = new HashMap<>();

            XContentBuilder content = XContentFactory.jsonBuilder();
            content.startObject();
            for (FieldSchema fieldSchema : docSchema.getFieldSchemas()) {
                if (!fieldSchema.isIndexingField() || fieldSchema instanceof FieldSchemaUsingIndexingFieldData)
                    continue;

                if (fieldSchema instanceof FieldSchemaUsingAnalyzer) {
                    AnalysisResult[] analysisResult = analyzeDocument(doc, (FieldSchemaUsingAnalyzer) fieldSchema);
                    analysisResults.put(fieldSchema.getIndexFieldName(), analysisResult);
                } else {
                    Object value = doc.getFieldValue(fieldSchema.getFieldNumber());
                    try {
                        if (value != null) {
                            if (value.getClass().isArray()) {
                                content.array(fieldSchema.getIndexFieldName(), value);
                            } else {
                                content.field(fieldSchema.getIndexFieldName(), value);
                            }
                        }
                    } catch (Exception e) {
                        log.error("failed to build content - {}\n\t- reason:{}", fieldSchema, Arrays.toString(e.getStackTrace()));
                        return null;
                    }
                }
            }
            for (FieldSchema fieldSchema : docSchema.getFieldSchemas()) {
                if (fieldSchema instanceof FieldSchemaUsingIndexingFieldData) {
                    Object value = ((FieldSchemaUsingIndexingFieldData) fieldSchema).processedValue(doc, analysisResults);
                    if (value != null) {
                        if (value.getClass().isArray()) {
                            content.array(fieldSchema.getIndexFieldName(), (Object[]) value);
                        } else {
                            content.field(fieldSchema.getIndexFieldName(), value);
                        }
                    }
                }
            }
            content.endObject();

            return makeIndexContent(doc, content);
        } catch (Exception e) {
            log.error("failed to analyze : {}\n\t- reason:{}", doc, Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    protected abstract KeywordResult analyze(String text);

    protected abstract void close();

    private AnalysisResult[] analyzeDocument(Document doc, FieldSchemaUsingAnalyzer fieldSchema) {
        String value = doc.getStringFieldValue(fieldSchema.getFieldNumber());
        AnalysisResult result = new AnalysisResult();
        KeywordResult keywordResult = analyze(value);

        if (fieldSchema.hasAnalysisItem(FieldSchemaUsingAnalyzer.AnalysisItem.INDEXTERM)) {
            result.setIndexTerms(makeTerms(keywordResult.getTerms()));
        }

        return new AnalysisResult[]{result};
    }

    public static String makeTerms(List<String> terms) {
        StringBuilder keywordsString = new StringBuilder();
        for (String term : terms) {
            if (keywordsString.length() > 0) keywordsString.append(' ');
            keywordsString.append(term);
        }
        return keywordsString.toString();
    }

    private IndexContent makeIndexContent(Document doc, XContentBuilder content) {
        DocumentSchema docSchema = doc.getSchema();
        return new IndexContent(doc.getIndexName(), doc.getType(), doc.getParentId(), content, docSchema.isPutIfAbsent());
    }

}
