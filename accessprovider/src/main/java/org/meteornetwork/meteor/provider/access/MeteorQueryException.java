package org.meteornetwork.meteor.provider.access;

public class MeteorQueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 676414945335029930L;

	public MeteorQueryException() {
		super();
	}
	
	public MeteorQueryException(String message) {
		super(message);
	}
	
	public MeteorQueryException(String message, Throwable t) {
		super(message, t);
	}
}
