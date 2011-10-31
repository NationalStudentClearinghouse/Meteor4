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
