package org.conqueror.db.exceptions.db;


import org.conqueror.common.exceptions.ThunderException;


@SuppressWarnings("unused")
public class DBException extends ThunderException {

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
