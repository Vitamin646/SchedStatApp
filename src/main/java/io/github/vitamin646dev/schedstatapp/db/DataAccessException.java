package io.github.vitamin646dev.schedstatapp.db;

/**
 * Unchecked wrapper for database failures.
 */
public class DataAccessException extends RuntimeException {

	public DataAccessException(String message) {
		super(message);
	}

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
