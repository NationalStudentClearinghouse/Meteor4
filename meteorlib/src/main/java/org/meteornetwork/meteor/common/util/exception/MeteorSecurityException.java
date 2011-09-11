package org.meteornetwork.meteor.common.util.exception;

public class MeteorSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5733882284013986905L;

	public MeteorSecurityException() {
		super();
	}
	
	public MeteorSecurityException(String message) {
		super(message);
	}
	
	public MeteorSecurityException(String message, Throwable t) {
		super(message, t);
	}
}
