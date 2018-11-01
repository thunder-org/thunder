package org.conqueror.bird.exceptions;

import org.conqueror.common.exceptions.ThunderException;


public class BirdException extends ThunderException {

    public BirdException() {
    }

    public BirdException(String message) {
        super(message);
    }

    public BirdException(Throwable cause) {
        super(cause);
    }

    public BirdException(String message, Throwable cause) {
        super(message, cause);
    }

}
