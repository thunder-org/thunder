package org.conqueror.cat.analysis;

import com.typesafe.config.ConfigFactory;
import org.conqueror.cat.config.KeywordAnalyzerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class KomoranKeywordAnalyzerTest {

    private KomoranKeywordAnalyzer analyzer;

    @Before
    public void setUp() throws Exception {
        KeywordAnalyzerConfig config = new KeywordAnalyzerConfig(ConfigFactory.parseString(
            "analyzer {\n" +
                "   conf-file-path = \"G:\\\\workspace\\\\thunder\\\\data\\\\conf\\\\analyzer\\\\komoran.conf\"\n" +
                "   refine {\n" +
                "       remove-url = true\n" +
                "       remove-email = true\n" +
                "   }\n" +
                "   analysis {\n" +
                "       types = [\"noun\", \"number\", \"english\"]\n" +
                "   }\n" +
                "}\n"
        ));
        analyzer = new KomoranKeywordAnalyzer(config);
    }

    @Test
    public void analyze() {
        KeywordResult result = analyzer.analyze("");
        for (String term : result.getTerms()) {
            System.out.println(term);
        }
        System.out.println(result);
    }

    @After
    public void tearDown() throws Exception {
        analyzer.close();
    }
}