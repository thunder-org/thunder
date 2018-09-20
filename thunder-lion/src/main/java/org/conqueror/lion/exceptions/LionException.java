package org.conqueror.lion.exceptions;

import org.conqueror.common.exceptions.ThunderException;

@SuppressWarnings("unused")
public class LionException extends ThunderException {

	public LionException() {
	}

	public LionException(String message) {
		super(message);
	}

	public LionException(Throwable cause) {
		super(cause);
	}

	public LionException(String message, Throwable cause) {
		super(message, cause);
	}

}
