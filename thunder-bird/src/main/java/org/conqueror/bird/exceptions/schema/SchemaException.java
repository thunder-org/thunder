package org.conqueror.bird.exceptions.schema;

import org.conqueror.bird.exceptions.BirdException;


public class SchemaException extends BirdException {

    public SchemaException() {
        super();
    }

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(Throwable cause) {
        super(cause);
    }

    public SchemaException(Exception e) {
        this(e.getMessage(), e.getCause());
    }

    public SchemaException(String message, Throwable cause) {
        super(message, cause);
    }

}
