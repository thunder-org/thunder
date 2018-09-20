package org.conqueror.lion.exceptions.id;

import org.conqueror.lion.exceptions.LionException;


public class IDIssuerException extends LionException {

    public IDIssuerException() {
    }

    public IDIssuerException(String message) {
        super(message);
    }

    public IDIssuerException(Throwable cause) {
        super(cause);
    }

    public IDIssuerException(Exception e) {
        this(e.getMessage(), e.getCause());
    }

    public IDIssuerException(String message, Throwable cause) {
        super(message, cause);
    }

}
