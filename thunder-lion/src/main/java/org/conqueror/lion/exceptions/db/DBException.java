package org.conqueror.lion.exceptions.db;

import org.conqueror.lion.exceptions.LionException;

@SuppressWarnings("unused")
public class DBException extends LionException {

	public DBException() {
		super();
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

	public DBException(Exception e) {
		this(e.getMessage(), e.getCause());
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

}
