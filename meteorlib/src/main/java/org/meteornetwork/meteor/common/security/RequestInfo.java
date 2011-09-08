package org.meteornetwork.meteor.common.security;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("requestInfo")
@Scope("request")
public class RequestInfo {

	private String meteorInstitutionIdentifier;

	private Integer level;
	private String userHandle;
	private Role role;

	private String organizationType;
	private String organizationID;
	private String organizationIDType;

	public String getMeteorInstitutionIdentifier() {
		return meteorInstitutionIdentifier;
	}

	public void setMeteorInstitutionIdentifier(String meteorInstitutionIdentifier) {
		this.meteorInstitutionIdentifier = meteorInstitutionIdentifier;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getOrganizationID() {
		return organizationID;
	}

	public void setOrganizationID(String organizationID) {
		this.organizationID = organizationID;
	}

	public String getOrganizationIDType() {
		return organizationIDType;
	}

	public void setOrganizationIDType(String organizationIDType) {
		this.organizationIDType = organizationIDType;
	}

}
