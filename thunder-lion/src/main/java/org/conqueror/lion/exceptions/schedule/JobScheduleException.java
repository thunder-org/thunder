package org.conqueror.lion.exceptions.schedule;

import org.conqueror.lion.exceptions.LionException;

public class JobScheduleException extends LionException {

	public JobScheduleException(String message) {
		super(message);
	}

	public JobScheduleException(String schedulerName, String message) {
		super("scheduler '" + schedulerName + "' : " + message);
	}

	public JobScheduleException(String schedulerName, Exception e) {
		super("scheduler '" + schedulerName + "' : " + e.getMessage(), e.getCause());
	}

	public JobScheduleException(String schedulerName, String message, Exception e) {
		super("scheduler '" + schedulerName + "' : " + message + " - " + e.getMessage(), e.getCause());
	}

	public JobScheduleException(Throwable cause) {
		super(cause);
	}

	public JobScheduleException(Exception e) {
		this(e.getMessage(), e.getCause());
	}

	public JobScheduleException(String message, Throwable cause) {
		super(message, cause);
	}

}
