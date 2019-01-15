package org.conqueror.cat.analysis;

import org.conqueror.cat.config.NGramConfig;
import org.conqueror.common.utils.config.ConfigLoader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class NGramAnalyzerTest {

    NGramAnalyzer analyzer;

    @Before
    public void setUp() throws Exception {
        analyzer = new NGramAnalyzer(new NGramConfig("analyzer.conf"));
    }

    @Test
    public void analyze() {
        String input = "The main objective";
        List<String> terms = analyzer.analyze(input);
        ArrayList<String> grams = new ArrayList<>();
        grams.add("Th");
        grams.add("he");
        grams.add("ma");
        grams.add("ai");
        grams.add("in");
        grams.add("ob");
        grams.add("bj");
        grams.add("je");
        grams.add("ec");
        grams.add("ct");
        grams.add("ti");
        grams.add("iv");
        grams.add("ve");
        Assert.assertEquals("ngrams", grams, terms);
    }

    @After
    public void tearDown() throws Exception {
    }
}