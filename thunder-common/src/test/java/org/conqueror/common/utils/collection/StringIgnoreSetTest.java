package org.conqueror.common.utils.collection;

import org.conqueror.common.utils.test.TestClass;
import org.junit.Assert;
import org.junit.Test;


public class StringIgnoreSetTest extends TestClass {

    @Test
    public void testAll() {
        StringIgnoreSet set = new StringIgnoreSet();

        Object abc = "abc";
        Object ABC = "ABC";

        // 대소문자 삽입/삭제
        set.add(ABC.toString());
        Assert.assertTrue(set.contains(abc));
        set.remove(abc);
        Assert.assertFalse(set.contains(ABC));
        set.add(abc.toString());
        Assert.assertTrue(set.contains(ABC));
        set.remove(ABC);
        Assert.assertFalse(set.contains(abc));
    }

}