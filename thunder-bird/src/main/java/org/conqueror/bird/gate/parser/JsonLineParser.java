package org.conqueror.bird.gate.parser;

import org.conqueror.bird.exceptions.parse.ParserException;
import org.conqueror.bird.exceptions.parse.ParserException.BufferedReaderNullPointerException;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.*;


public class JsonLineParser implements Parser {

    private static final JsonLineParser parser = new JsonLineParser();

    public JsonLineParser() {
    }

    public static JsonLineParser getInstance() {
        return parser;
    }

    @Override
    public Collection<Map<String, Object>> parse(BufferedReader reader) throws ParserException {
        try {
            String value = reader.readLine();
            if (value != null) {
                List<Map<String, Object>> list = new ArrayList<>();
                list.add((Map<String, Object>) JSONValue.parse(value));
                return list;
            }
        } catch (Exception e) {
            if (reader == null) throw new BufferedReaderNullPointerException();
            throw new ParserException(e);
        }

        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "JsonLineParser{}";
    }

    @Override
    public void writeObject(DataOutput output) {

    }

    @Override
    public JsonLineParser readObject(DataInput input) {
        return getInstance();
    }

}
