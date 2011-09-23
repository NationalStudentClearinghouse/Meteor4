package org.meteornetwork.meteor.saml.exception;

public class SecurityTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4255617114540968958L;

	public SecurityTokenException() {
		super();
	}

	public SecurityTokenException(String message) {
		super(message);
	}

	public SecurityTokenException(Throwable t) {
		super(t);
	}

	public SecurityTokenException(String message, Throwable t) {
		super(message, t);
	}
}
