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
