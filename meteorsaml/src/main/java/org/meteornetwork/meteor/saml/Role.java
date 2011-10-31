package org.meteornetwork.meteor.saml;

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
	
	public static Role valueOfName(String name) {
		for (Role value : Role.values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		
		return null;
	}
}
