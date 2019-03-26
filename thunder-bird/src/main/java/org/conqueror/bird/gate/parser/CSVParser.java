package org.conqueror.bird.gate.parser;

import org.conqueror.bird.exceptions.parse.ParserException;
import org.conqueror.common.exceptions.serialize.SerializableException;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;


public class CSVParser implements Parser {

    private final String delimiter;
    private String[] keys = null;
    private int numberOfKeys = 0;

    public CSVParser() {
        this(",");
    }

    public CSVParser(String delimiter) {
        this.delimiter = delimiter;
    }

    public CSVParser(String delimiter, String[] keys, int numberOfKeys) {
        this.delimiter = delimiter;
        this.keys = keys;
        this.numberOfKeys = numberOfKeys;
    }

    @Override
    public Collection<Map<String, Object>> parse(BufferedReader reader) throws ParserException {
        try {
            if (keys == null) {
                extractKeysFromHead(reader);
            }

            Map<String, Object> row = extractValuesFromRow(reader);
            if (row != null) {
                List<Map<String, Object>> list = new ArrayList<>();
                list.add(row);
                return list;
            }
        } catch (Exception e) {
            if (reader == null) throw new ParserException.BufferedReaderNullPointerException();
            throw new ParserException(e);
        }

        return null;
    }

    private void extractKeysFromHead(BufferedReader reader) throws IOException, ParserException {
        String value = reader.readLine();
        keys = value.split(delimiter);
        numberOfKeys = keys.length;

        if (numberOfKeys == 0) throw new ParserException("key");
    }

    private Map<String, Object> extractValuesFromRow(BufferedReader reader) throws IOException, ParserException {
        Map<String, Object> values = new HashMap<>(numberOfKeys);
        String line = reader.readLine();
        if (line == null) return null;
        int keyIdx = 0;
        for (String value : line.split(delimiter)) {
            values.put(keys[keyIdx++], value);
        }

        return values;
    }

    @Override
    public String toString() {
        return "CSVParser{" +
            "delimiter='" + delimiter + '\'' +
            ", keys=" + Arrays.toString(keys) +
            ", numberOfKeys=" + numberOfKeys +
            '}';
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeUTF(delimiter);
            output.writeInt(numberOfKeys);
            for (String key : keys) {
                output.writeUTF(key);
            }
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public CSVParser readObject(DataInput input) throws SerializableException {
        try {
            String delimiter = input.readUTF();
            int numberOfKeys = input.readInt();
            String[] keys = new String[numberOfKeys];
            for (int keyIdx = 0; keyIdx < numberOfKeys; keyIdx++) {
                keys[keyIdx] = input.readUTF();
            }
            return new CSVParser(delimiter, keys, numberOfKeys);
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

}
