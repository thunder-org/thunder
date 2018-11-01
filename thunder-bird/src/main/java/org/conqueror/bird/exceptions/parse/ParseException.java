package org.conqueror.bird.exceptions.parse;

import org.conqueror.bird.exceptions.BirdException;


public class ParseException extends BirdException {

    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(Exception e) {
        this(e.getMessage(), e.getCause());
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
