package org.conqueror.bird.exceptions.document;

import org.conqueror.bird.exceptions.BirdException;


public class DocumentException extends BirdException {

    public DocumentException() {
        super();
    }

    public DocumentException(String message) {
        super(message);
    }

    public DocumentException(Throwable cause) {
        super(cause);
    }

    public DocumentException(Exception e) {
        this(e.getMessage(), e.getCause());
    }

    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }

}
