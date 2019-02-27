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
                "   conf-path = \"conf/komoran.conf\"\n" +
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
        KeywordResult result = analyzer.analyze("밀리언 달러 베이비랑 바람과 함께 사라지다랑 뭐가 더 재밌었어?");
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