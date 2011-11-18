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
package org.meteornetwork.meteor.common.util.message;

public enum MeteorMessage {

	REGISTRY_NO_CONNECTION("D-010", "registry.noconnection"),

	INDEX_RESPONSE_ERROR("D-002", "index.noresponse"),
	INDEX_NO_DATA_PROVIDERS_FOUND_FAA("D-003a", "index.nodataproviders.faa"),
	INDEX_NO_DATA_PROVIDERS_FOUND_BORROWER("D-003b",
			"index.nodataproviders.borrower"),

	DATA_ERROR_ALL("D-004a", "data.noresponse.all"),
	DATA_ERROR("D-004b", "data.noresponse.one"),
	DATA_NODATA("D-005", "data.nodata"),
	DATA_INSUFFICIENT_LEVEL("D-007", "data.insufficientlevel"),
	DATA_NO_MINIMUM_LEVEL("D-015", "data.nominimumlevel"),
	DATA_ROLE_NOT_SUPPORTED("D-016", "data.role.notsupported"),
	DATA_NOSSN("D-017", "data.nossn"),
	DATA_SSN_NOTAUTHORIZED("D-018", "data.ssn.notauthorized"),

	ACCESS_ERROR("D-006", "access.invalidtoken"),
	ACCESS_INVALID_MESSAGE_SIGNATURE("D-011", "access.invalidmessagesignature"),
	ACCESS_ALIAS_ERROR("D-019", "access.aliaserror"),

	SECURITY_INVALID_TOKEN("D-011", "security.invalidtoken"),
	SECURITY_TOKEN_NOT_ALLOWED("D-012", "security.tokennotallowed"),
	SECURITY_TOKEN_NOT_PASSED("D-013", "security.tokennotpassed"),
	SECURITY_LEVEL_TOO_LOW("D-014", "security.leveltoolow"),
	SECURITY_TOKEN_EXPIRED("D-020", "security.tokenexpired");

	private String code;
	private String propertyRef;

	private MeteorMessage(String code, String propertyRef) {
		this.code = code;
		this.propertyRef = propertyRef;
	}

	public String getCode() {
		return code;
	}

	public String getPropertyRef() {
		return propertyRef;
	}
}
