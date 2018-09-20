package org.conqueror.lion.exceptions.Serialize;

import org.conqueror.lion.exceptions.LionException;

@SuppressWarnings("unused")
public class SerializableException extends LionException {

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
