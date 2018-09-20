package org.conqueror.common.exceptions;

public class ThunderException extends Exception {

	public ThunderException() {
	}

	public ThunderException(String message) {
		super(message);
	}

	public ThunderException(Throwable cause) {
		super(cause);
	}

	public ThunderException(String message, Throwable cause) {
		super(message, cause);
	}

}
