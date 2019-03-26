package org.conqueror.bird.stats;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;


public class JobStats implements Serializable {

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static class TimeStats implements Serializable {

    }

}
