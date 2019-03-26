package org.conqueror.bird.gate.parser;

import org.conqueror.bird.exceptions.parse.ParserException;
import org.conqueror.bird.exceptions.parse.ParserException.BufferedReaderNullPointerException;
import org.conqueror.common.serialize.ThunderSerializable;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class JsonArrayParser implements Parser {

    private static final JsonArrayParser parser = new JsonArrayParser();

    public static JsonArrayParser getInstance() {
        return parser;
    }

    @Override
    public Collection<Map<String, Object>> parse(BufferedReader reader) throws ParserException {
        try {
            JSONArray values = (JSONArray) JSONValue.parse(reader);
            if (values != null) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (Object value : values) {
                    list.add((Map<String, Object>) value);
                }
                return list;
            }
        } catch (Exception e) {
            if (reader == null) throw new BufferedReaderNullPointerException();
            throw new ParserException(e);
        }

        return null;
    }

    @Override
    public void writeObject(DataOutput output) {

    }

    @Override
    public ThunderSerializable readObject(DataInput input) {
        return getInstance();
    }

}
