package org.meteornetwork.meteor.common.registry.data;


/**
 * Contains information about a Data Provider returned from the Meteor Registry
 * 
 * @author jlazos
 * 
 */
public class DataProvider {

	private String institutionIdentifier;
	private String url;
	private String meteorVersion;

	public String getInstitutionIdentifier() {
		return institutionIdentifier;
	}

	public void setInstitutionIdentifier(String institutionIdentifier) {
		this.institutionIdentifier = institutionIdentifier;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMeteorVersion() {
		return meteorVersion;
	}

	public void setMeteorVersion(String meteorVersion) {
		this.meteorVersion = meteorVersion;
	}

}
