package org.conqueror.common.utils.collection;

import java.util.HashSet;
import java.util.Locale;

/*
    hash set for only string elements
 */
public class StringIgnoreSet extends HashSet<String> {

    @Override
    public boolean add(String elim) {
        return super.add(elim.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public boolean remove(Object obj) {
        return (obj instanceof String) && remove((String) obj);
    }

    public boolean remove(String obj) {
        return super.remove(obj.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public boolean contains(Object obj) {
        return (obj instanceof String) && contains((String) obj);
    }

    public boolean contains(String obj) {
        return super.contains(obj.toLowerCase(Locale.ENGLISH));
    }

}
