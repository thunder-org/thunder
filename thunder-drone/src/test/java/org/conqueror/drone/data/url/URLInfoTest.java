package org.conqueror.drone.data.url;

import org.junit.Assert;
import org.junit.Test;


public class URLInfoTest {

    @Test
    public void test() {
        Assert.assertEquals("https://www.geogigani.com/?a=1&c=3", URLInfo.normalize("https://www.geogigani.com?c=3&a=1&b=#abc", false));
        Assert.assertEquals("https://www.geogigani.com/b/#abc?", URLInfo.normalize("https://www.geogigani.com/a/../b/#abc?", true));
        Assert.assertEquals("https://www.geogigani.com/a/", URLInfo.normalize("https://www.geogigani.com/a/./#abc", false));
    }
}