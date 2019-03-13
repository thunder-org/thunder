package org.conqueror.common.exceptions.serialize;


import org.conqueror.common.exceptions.ThunderException;


public class SerializableException extends ThunderException {

	public SerializableException() {
		super();
	}

	public SerializableException(String message) {
		super(message);
	}

	public SerializableException(Throwable cause) {
		super(cause);
	}

	public SerializableException(Exception e) {
		this(e.getMessage(), e.getCause());
	}

	public SerializableException(String message, Throwable cause) {
		super(message, cause);
	}

}
