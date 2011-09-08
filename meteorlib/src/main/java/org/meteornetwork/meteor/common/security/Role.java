package org.meteornetwork.meteor.common.security;

/**
 * Defines meteor roles
 * 
 * @author jlazos
 */
public enum Role {
	FAA("FAA"),
	BORROWER("BORROWER"),
	APCSR("APCSR"),
	LENDER("LENDER"),
	HELPDESK("HELPDESK");

	private String name;

	private Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
