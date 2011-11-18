/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
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
