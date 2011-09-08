package org.meteornetwork.meteor.common.registry;

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
}
