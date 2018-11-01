package org.conqueror.bird.gate.parser;

import org.conqueror.bird.exceptions.parse.ParserException;
import org.conqueror.bird.exceptions.parse.ParserException.BufferedReaderNullPointerException;
import org.conqueror.lion.exceptions.Serialize.SerializableException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.Map;


public class JsonParser implements Parser {

    private static final JsonParser parser = new JsonParser();

    public JsonParser() {
    }

    public static JsonParser getInstance() {
        return parser;
    }

    @Override
    public Map<String, Object> parse(BufferedReader reader) throws ParserException {
        try {
            String json = reader.readLine();
            if (json != null) return (JSONObject) JSONValue.parse(json);
        } catch (Exception e) {
            if (reader == null) throw new BufferedReaderNullPointerException();
            throw new ParserException(e);
        }

        return null;
    }

    @Override
    public String toString() {
        return "JsonParser{}";
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {

    }

    @Override
    public JsonParser readObject(DataInput input) throws SerializableException {
        return getInstance();
    }

}
