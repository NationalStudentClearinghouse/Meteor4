package org.meteornetwork.meteor.common;

public class AccessProvider {

	private String meteorInstitutionIdentifier;
	private String name;
	private String userHandle;
	private String issueInstant;

	public AccessProvider() {
	}

	public AccessProvider(org.meteornetwork.meteor.common.xml.AccessProvider accessProvider) {
		meteorInstitutionIdentifier = accessProvider.getMeteorInstitutionIdentifier();
		name = accessProvider.getName();
		userHandle = accessProvider.getUserHandle();
		issueInstant = accessProvider.getIssueInstant();
	}

	public String getMeteorInstitutionIdentifier() {
		return meteorInstitutionIdentifier;
	}

	public void setMeteorInstitutionIdentifier(String meteorInstitutionIdentifier) {
		this.meteorInstitutionIdentifier = meteorInstitutionIdentifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public String getIssueInstant() {
		return issueInstant;
	}

	public void setIssueInstant(String issueInstant) {
		this.issueInstant = issueInstant;
	}

}
