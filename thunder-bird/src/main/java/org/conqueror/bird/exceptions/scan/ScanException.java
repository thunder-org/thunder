package org.conqueror.bird.exceptions.scan;


import org.conqueror.bird.exceptions.BirdException;


public class ScanException extends BirdException {

    public ScanException() {
    }

    public ScanException(String message) {
        super(message);
    }

    public ScanException(Throwable cause) {
        super(cause);
    }

    public ScanException(Exception e) {
        this(e.getMessage(), e.getCause());
    }

    public ScanException(String message, Throwable cause) {
        super(message, cause);
    }

}
