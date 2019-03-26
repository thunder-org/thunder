package org.conqueror.bird.gate.parser;

import org.conqueror.common.utils.test.TestClass;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class CSVParserTest extends TestClass {

    @Test
    public void parse() throws IOException {
        String fileName = "csv_test.txt";
        CSVParser parser = new CSVParser("\t");
        BufferedReader reader = new BufferedReader(new FileReader(getResourceFile(fileName)));
        Map<String, Object> datum;

        datum = parser.parse(reader).iterator().next();
        Assert.assertEquals("1", datum.get("id"));
        Assert.assertEquals("a", datum.get("document"));
        Assert.assertEquals("3", datum.get("label"));

        datum = parser.parse(reader).iterator().next();
        Assert.assertEquals("2", datum.get("id"));
        Assert.assertEquals("b", datum.get("document"));
        Assert.assertEquals("2", datum.get("label"));

        datum = parser.parse(reader).iterator().next();
        Assert.assertEquals("3", datum.get("id"));
        Assert.assertEquals("c", datum.get("document"));
        Assert.assertNull(datum.get("label"));

        reader.close();
    }

}