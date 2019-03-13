package org.conqueror.common.utils.string;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;


public class StringUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        List<String> testList = new ArrayList<>(3);
        testList.add("abc def");
        testList.add("ghi");
        testList.add("jkl");
        Assert.assertThat(
            StringUtils.splitIgnoringInQuotes("\"abc def\" ghi jkl", " ")
            , is(testList)
        );

        Assert.assertEquals(
            "bcdef"
            , StringUtils.charsToString(new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g'}, 1, 6)
        );

        Assert.assertEquals(
            StringUtils.CharCodeType.KOREAN
            , StringUtils.getCharCodeType('가')
        );
        Assert.assertEquals(
            StringUtils.CharCodeType.ENG
            , StringUtils.getCharCodeType('A')
        );
        Assert.assertEquals(
            StringUtils.CharCodeType.NUM
            , StringUtils.getCharCodeType('1')
        );
        Assert.assertEquals(
            StringUtils.CharCodeType.ETC
            , StringUtils.getCharCodeType('@')
        );

        Assert.assertTrue(StringUtils.isSpecialChar('#'));
        Assert.assertFalse(StringUtils.isSpecialChar('a'));
        Assert.assertTrue(StringUtils.isSpecialChar("!@#"));
        Assert.assertFalse(StringUtils.isSpecialChar("abc"));

        Assert.assertTrue(StringUtils.hasWhiteSpace("abc def"));
        Assert.assertFalse(StringUtils.hasWhiteSpace("abcdef"));

        Assert.assertTrue(StringUtils.isEnglishChar("abc"));
        Assert.assertFalse(StringUtils.isEnglishChar("abc가나다"));
        Assert.assertTrue(StringUtils.isEnglishChar('a'));
        Assert.assertFalse(StringUtils.isEnglishChar('가'));
        Assert.assertTrue(StringUtils.isEnglishChar((CharSequence) "abc"));
        Assert.assertFalse(StringUtils.isEnglishChar((CharSequence) "abc가나다"));

        Assert.assertTrue(StringUtils.isNumberChar("12300"));
        Assert.assertFalse(StringUtils.isNumberChar("abcdef"));
        Assert.assertTrue(StringUtils.isNumberChar('0'));
        Assert.assertFalse(StringUtils.isNumberChar('가'));
        Assert.assertFalse(StringUtils.isNumberChar('a'));
        Assert.assertFalse(StringUtils.isNumberChar('#'));

        Assert.assertEquals(
            "is google web site."
            , StringUtils.removeUrl("https://www.google.com/ is google web site.")
        );

        Assert.assertEquals(
            "is google mail address."
            , StringUtils.removeEmail("abc@gmail.com is google mail address.")
        );

        Assert.assertEquals(
            "a b c d e f"
            , StringUtils.refineAllWhiteSpace("a\tb\nc\fd\re f")
        );
    }

    @After
    public void tearDown() throws Exception {
    }
}