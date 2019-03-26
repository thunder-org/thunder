package org.conqueror.bird.gate.source.schema;

import org.conqueror.bird.data.BirdData;
import org.conqueror.bird.exceptions.schema.SchemaException;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.conqueror.bird.gate.source.schema.FieldSchemaUsingAnalyzer.isFieldSchemaUsingAnalyzer;
import static org.conqueror.bird.gate.source.schema.FieldSchemaUsingIndexingFieldData.isFieldSchemaUsingIndexFieldData;
import static org.conqueror.bird.gate.source.schema.FieldSchemaUsingSourceValue.isFieldSchemaUsingSourceValue;


public class DocumentSchema implements BirdData {

    public static DocumentSchema documentSchemaInstance = new DocumentSchema();

    private static final String SCHEMA_NAME = "schema_name";
    private static final String INDEX_NAME = "index_name";
    private static final String CHANNEL = "channel";

    private static final String FIELDS = "fields";
    private static final String CHILD_FIELDS = "child";

    private static final String ID_FIELD = "_id_field";
    private static final String TYPE = "_type";
    private static final String PUT_IF_ABSENT = "_put_if_absent";
    private static final String ONLY_ONE = "_only_one";
    private static final String ONLY_ONE_KEY = "key";
    private static final String ONLY_ONE_WHICH = "which";
    private static final String ONLY_ONE_FIELD = "field";

    private String jsonSchema;

    private final String schemaName;
    private final String mappingName;
    private final String channel;

    private String idFieldName;
    private String type;
    private boolean putIfAbsent = true;
    private String onlyOneKey = null;
    private String onlyOneWhich = null;
    private String onlyOneField = null;

    private final List<FieldSchema> fieldSchemas;
    private final Map<String, FieldSchema> fieldSchemasWithSrcFieldNameKey;
    private final Map<String, FieldSchema> fieldSchemasWithIndexFieldNameKey;
    private final String[] indexNameExpression;  // @{field_name_1} + "text" + @{field_name_2} -> @{field_name_1}, "text", @{field_name_2}

    private String childIdFieldName = null;
    private String childType = null;
    private boolean childPutIfAbsent = true;
    private final List<FieldSchema> childFieldSchemas;


    public DocumentSchema() {
        this(null, 0, 0, (String[]) null, null, null);
    }

    private DocumentSchema(String schemaName, int fieldSize, String indexNameExpression, String channel, String mappingName) {
        this(schemaName, fieldSize, 0, indexNameExpression.split("[ ]*\\+[ ]*"), channel, mappingName);
    }

    private DocumentSchema(String schemaName, int fieldSize, int childFieldSize, String indexNameExpression, String channel, String mappingName) {
        this(schemaName, fieldSize, childFieldSize, indexNameExpression.split("[ ]*\\+[ ]*"), channel, mappingName);
    }

    private DocumentSchema(String schemaName, int fieldSize, int childFieldSize, String[] indexNameExpression, String channel, String mappingName) {
        this.schemaName = schemaName;
        this.mappingName = mappingName;
        this.channel = channel;

        this.fieldSchemas = new ArrayList<>(fieldSize);
        this.childFieldSchemas = new ArrayList<>(childFieldSize);
        this.fieldSchemasWithSrcFieldNameKey = new HashMap<>(fieldSize);
        this.fieldSchemasWithIndexFieldNameKey = new HashMap<>(fieldSize);
        this.indexNameExpression = indexNameExpression;
    }

    public static DocumentSchema getInstance() {
        return documentSchemaInstance;
    }

    public static DocumentSchema buildSchema(String jsonSchema, String mappingName) throws SchemaException {
        JSONObject json;
        try {
            json = (JSONObject) JSONValue.parseWithException(jsonSchema);
        } catch (ParseException e) {
            throw new SchemaException("failed to parse the schema json file : " + e.getMessage(), e.getCause());
        }

        JSONObject fields = (JSONObject) json.get(FIELDS);
        JSONObject childFields = (JSONObject) json.get(CHILD_FIELDS);
        String schemaName = (String) json.get(SCHEMA_NAME);
        String indexName = (String) json.get(INDEX_NAME);
        String channel = (String) json.get(CHANNEL);
        DocumentSchema schema = (childFields == null) ?
            new DocumentSchema(schemaName, fields.size(), indexName, channel, mappingName)
            : new DocumentSchema(schemaName, fields.size(), childFields.size(), indexName, channel, mappingName);

        schema.setJsonSchema(jsonSchema);

        int fieldNumber = 0;
        for (Object field : fields.entrySet()) {
            String srcFieldName = (String) ((Entry) field).getKey();

            try {
                if (isIdFieldField(srcFieldName)) {
                    schema.setIdFieldName((String) ((Entry) field).getValue());
                    continue;
                } else if (isTypeField(srcFieldName)) {
                    schema.setType((String) ((Entry) field).getValue());
                    continue;
                } else if (isPutIfAbsentField(srcFieldName)) {
                    schema.setPutIfAbsent((Boolean) ((Entry) field).getValue());
                    continue;
                } else if (isOnlyOneField(srcFieldName)) {
                    schema.setOnlyOne((JSONObject) ((Entry) field).getValue());
                    continue;
                }

                JSONObject values = (JSONObject) ((Entry) field).getValue();

                FieldSchema fieldSchema;
                if (isFieldSchemaUsingSourceValue(values)) {
                    fieldSchema = FieldSchemaUsingSourceValue.build(srcFieldName, values, fieldNumber);
                } else if (isFieldSchemaUsingAnalyzer(values)) {
                    fieldSchema = FieldSchemaUsingAnalyzer.build(values, fieldNumber);
                } else if (isFieldSchemaUsingIndexFieldData(values)) {
                    fieldSchema = FieldSchemaUsingIndexingFieldData.build(values, fieldNumber);
                } else {
                    fieldSchema = FieldSchemaUsingOtherFieldValue.build(values, fieldNumber);
                }

                if (fieldSchema != null) {
                    schema.addFieldSchema(fieldSchema);
                    if (fieldSchema.isIndexingField()) fieldNumber++;
                }
            } catch (Exception e) {
                throw new SchemaException("[Build Schema - parent] failed to parse " + schemaName + "/" + srcFieldName + " : " + e.getMessage(), e.getCause());
            }
        }

        if (childFields != null) {
            fieldNumber = 0;
            for (Object field : childFields.entrySet()) {
                String srcFieldName = (String) ((Entry) field).getKey();

                try {
                    if (isIdFieldField(srcFieldName)) {
                        schema.setChildIdFieldName((String) ((Entry) field).getValue());
                        continue;
                    } else if (isTypeField(srcFieldName)) {
                        schema.setChildType((String) ((Entry) field).getValue());
                        continue;
                    } else if (isPutIfAbsentField(srcFieldName)) {
                        schema.setPutIfAbsent((Boolean) ((Entry) field).getValue());
                        continue;
                    }

                    JSONObject values = (JSONObject) ((Entry) field).getValue();

                    FieldSchema fieldSchema;
                    if (isFieldSchemaUsingIndexFieldData(values)) {
                        fieldSchema = FieldSchemaUsingIndexingFieldData.build(values, fieldNumber);
                        schema.addChildFieldSchema(fieldSchema);
                        if (fieldSchema.isIndexingField()) fieldNumber++;
                    }
                } catch (Exception e) {
                    throw new SchemaException("[Build Schema - child] failed to parse " + schemaName + "/" + srcFieldName + " : " + e.getMessage(), e.getCause());
                }
            }
        }

        return schema;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    private void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    private void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        this.type = type;
    }

    public boolean isPutIfAbsent() {
        return putIfAbsent;
    }

    private void setPutIfAbsent(boolean putIfAbsent) {
        this.putIfAbsent = putIfAbsent;
    }

    public boolean isOnlyOne() {
        return onlyOneKey != null && onlyOneWhich != null && onlyOneField != null;
    }

    public String getOnlyOneKey() {
        return onlyOneKey;
    }

    private void setOnlyOneKey(String onlyOneKey) {
        this.onlyOneKey = onlyOneKey;
    }

    public String getOnlyOneWhich() {
        return onlyOneWhich;
    }

    private void setOnlyOneWhich(String onlyOneWhich) {
        this.onlyOneWhich = onlyOneWhich;
    }

    public String getOnlyOneField() {
        return onlyOneField;
    }

    private void setOnlyOneField(String onlyOneField) {
        this.onlyOneField = onlyOneField;
    }

    public boolean hasChild() {
        return childIdFieldName != null;
    }

    public int getFieldSize() {
        return fieldSchemas.size();
    }

    public String getChildIdFieldName() {
        return childIdFieldName;
    }

    private void setChildIdFieldName(String childIdFieldName) {
        this.childIdFieldName = childIdFieldName;
    }

    public String getChildType() {
        return childType;
    }

    private void setChildType(String childType) {
        this.childType = childType;
    }

    public boolean isChildPutIfAbsent() {
        return childPutIfAbsent;
    }

    private void setChildPutIfAbsent(boolean childPutIfAbsent) {
        this.childPutIfAbsent = childPutIfAbsent;
    }

    public int getChildFieldSize() {
        return childFieldSchemas.size();
    }

    public int getIndexingFieldSize() {
        int count = 0;
        for (FieldSchema schema : fieldSchemas) {
            if (schema.isIndexingField()) count++;
        }
        return count;
    }

    public FieldSchema getSchema(int index) {
        return fieldSchemas.get(index);
    }

    public FieldSchema getChildSchema(int index) {
        return fieldSchemas.get(index);
    }

    public String getMappingName() {
        return mappingName;
    }

    public String getChannel() {
        return channel;
    }

    public List<FieldSchema> getFieldSchemas() {
        return fieldSchemas;
    }

    public List<FieldSchema> getChildFieldSchemas() {
        return childFieldSchemas;
    }

    public FieldSchema getFieldSchemaBySrcFieldName(String srcFieldName) {
        return fieldSchemasWithSrcFieldNameKey.get(srcFieldName);
    }

    public FieldSchema getFieldSchemaByIndexFieldName(String indexFieldName) {
        return fieldSchemasWithIndexFieldNameKey.get(indexFieldName);
    }

    public String[] getIndexNameExpression() {
        return indexNameExpression;
    }

    private DocumentSchema addFieldSchema(FieldSchema schema) {
        fieldSchemas.add(schema);
        if (schema.getSrcFieldName() != null) fieldSchemasWithSrcFieldNameKey.put(schema.getSrcFieldName(), schema);
        if (schema.isIndexingField()) fieldSchemasWithIndexFieldNameKey.put(schema.getIndexFieldName(), schema);
        return this;
    }

    private DocumentSchema addFieldSchema(List<FieldSchema> schemas) {
        for (FieldSchema schema : schemas) {
            addFieldSchema(schema);
        }
        return this;
    }

    private DocumentSchema addFieldSchema(FieldSchema[] schemas) {
        for (FieldSchema schema : schemas) {
            addFieldSchema(schema);
        }
        return this;
    }

    private DocumentSchema addChildFieldSchema(FieldSchema schema) {
        childFieldSchemas.add(schema);
        return this;
    }

    private DocumentSchema addChildFieldSchema(List<FieldSchema> schemas) {
        for (FieldSchema schema : schemas) {
            addChildFieldSchema(schema);
        }
        return this;
    }

    private DocumentSchema addChildFieldSchema(FieldSchema[] schemas) {
        for (FieldSchema schema : schemas) {
            addChildFieldSchema(schema);
        }
        return this;
    }

    private static boolean isIdFieldField(String fieldName) {
        return fieldName.equalsIgnoreCase(ID_FIELD);
    }

    private static boolean isTypeField(String fieldName) {
        return fieldName.equalsIgnoreCase(TYPE);
    }

    private static boolean isPutIfAbsentField(String fieldName) {
        return fieldName.equalsIgnoreCase(PUT_IF_ABSENT);
    }

    private static boolean isOnlyOneField(String fieldName) {
        return fieldName.equalsIgnoreCase(ONLY_ONE);
    }

    private void setOnlyOne(JSONObject values) {
        setOnlyOneKey((String) values.get(ONLY_ONE_KEY));
        setOnlyOneWhich((String) values.get(ONLY_ONE_WHICH));
        setOnlyOneField((String) values.get(ONLY_ONE_FIELD));
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(getJsonSchema());
            output.writeUTF(getMappingName());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public DocumentSchema readObject(DataInput input) throws SerializableException {
        try {
            return buildSchema(input.readUTF(), input.readUTF());
        } catch (IOException | SchemaException e) {
            throw new SerializableException(e);
        }
    }

}
