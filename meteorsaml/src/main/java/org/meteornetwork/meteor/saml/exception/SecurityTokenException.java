package org.meteornetwork.meteor.saml.exception;

/**
 * Thrown when token provider implementation can't return a security token.
 * CauseCode specifies the UI provider's handling of the exception. If access
 * denied, user is redirected to access denied page. If session has expired,
 * user is directed to the more innocent "Please login again" page
 * 
 * @author jlazos
 */
public class SecurityTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4255617114540968958L;

	/**
	 * ACCESS_DENIED - UI provider will present the user with the Access Denied
	 * page.
	 * 
	 * SESSION_EXPIRED - UI provider will present the user with the Session
	 * Expired page.
	 */
	public enum CauseCode {
		ACCESS_DENIED, SESSION_EXPIRED
	}

	private final CauseCode causeCode;

	public SecurityTokenException() {
		super();
		causeCode = CauseCode.ACCESS_DENIED;
	}

	public SecurityTokenException(CauseCode causeCode) {
		super();
		this.causeCode = causeCode;
	}

	public SecurityTokenException(String message) {
		super(message);
		causeCode = CauseCode.ACCESS_DENIED;
	}

	public SecurityTokenException(String message, CauseCode causeCode) {
		super(message);
		this.causeCode = causeCode;
	}

	public SecurityTokenException(Throwable t) {
		super(t);
		causeCode = CauseCode.ACCESS_DENIED;
	}

	public SecurityTokenException(Throwable t, CauseCode causeCode) {
		super(t);
		this.causeCode = causeCode;
	}

	public SecurityTokenException(String message, Throwable t) {
		super(message, t);
		causeCode = CauseCode.ACCESS_DENIED;
	}

	public SecurityTokenException(String message, Throwable t, CauseCode causeCode) {
		super(message, t);
		this.causeCode = causeCode;
	}

	public CauseCode getCauseCode() {
		return causeCode;
	}
}
