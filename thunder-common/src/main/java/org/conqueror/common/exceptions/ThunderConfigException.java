package org.conqueror.common.exceptions;

public class ThunderConfigException extends RuntimeException {

	public ThunderConfigException() {
	}

	public ThunderConfigException(String message) {
		super(message);
	}

	public ThunderConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ThunderConfigException(Throwable cause) {
		super(cause);
	}

	public static class WrongPathOrNullValue extends ThunderConfigException {

		public WrongPathOrNullValue(String path) {
			super("ThunderConfigException : must have the path '" + path + "' and value");
		}

	}

	public static class WrongTypeValue extends ThunderConfigException {

		public WrongTypeValue(String path) {
			super("ThunderConfigException : the value of the path '" + path + "' is wrong type");
		}

	}

}
