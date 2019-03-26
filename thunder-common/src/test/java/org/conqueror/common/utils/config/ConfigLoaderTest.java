package org.conqueror.common.utils.config;

import com.typesafe.config.Config;
import org.conqueror.common.utils.test.TestClass;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ConfigLoaderTest extends TestClass {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");

    @Test
    public void test() {
        File configFile = getResourceFile("test.conf");

        check(ConfigLoader.load(configFile));
        check(ConfigLoader.parse(
            "values {\n" +
                "string-value = \"abc\"\n" +
                "integer-value = 100\n" +
                "long-value = 100000000000\n" +
                "double-value = 10000000.1234\n" +
                "stringlist-value = [ \"a1\", \"a2\", \"a3\" ]\n" +
                "datetime-value = \"20190308\"\n" +
                "boolean-value = true\n" +
                "}")
        );
        check((TestConfig) ConfigLoader.build(TestConfig.class, configFile));
    }

    public void check(Config config) {
        Assert.assertEquals("abc", Configuration.getStringFromConfig(config, "values.string-value", true));
        Assert.assertEquals(new Integer(100), Configuration.getIntegerFromConfig(config, "values.integer-value", true));
        Assert.assertEquals(new Long(100000000000L), Configuration.getLongFromConfig(config, "values.long-value", true));
        Assert.assertEquals(new Double(10000000.1234), Configuration.getDoubleFromConfig(config, "values.double-value", true));
        Assert.assertArrayEquals(new String[]{"a1", "a2", "a3"}, Configuration.getStringListFromConfig(config, "values.stringlist-value", true).toArray(new String[0]));
        Assert.assertEquals(formatter.parseDateTime("20190308"), Configuration.getDateTimeFromConfig(config, "values.datetime-value", formatter, true));
        Assert.assertEquals(true, Configuration.getBooleanFromConfig(config, "values.boolean-value", true));
    }

    public void check(TestConfig config) {
        Assert.assertEquals( "abc", config.getStringValue());
        Assert.assertEquals(100, config.getIntegerValue());
        Assert.assertEquals(100000000000L, config.getLongValue());
        Assert.assertEquals(10000000.1234d, config.getDoubleValue(), 0.00001d);
        List<String> list = new ArrayList<>(3);
        list.add("a1");
        list.add("a2");
        list.add("a3");
        Assert.assertEquals(list, config.getStringListValue());
        Assert.assertEquals(formatter.parseDateTime("20190308"), config.getDateTimeValue());
        Assert.assertEquals(true, config.getBooleanValue());

    }

}