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
package org.meteornetwork.meteor.saml;

public enum ProviderType {
	ACCESS("AccessProvider"),
	INDEX("IndexProvider"),
	DATA("DataProvider"),
	STATUS("StatusProvider"),
	AUTHENTICATION("AuthenticationProvider"),
	LENDER("Lender");

	private String type;

	private ProviderType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static ProviderType valueOfType(String type) {
		for (ProviderType value : ProviderType.values()) {
			if (value.getType().equals(type)) {
				return value;
			}
		}
		
		return null;
	}
}
