package org.meteornetwork.meteor.common.registry.data;

/**
 * Contains information about an Index Provider returned from the Meteor
 * Registry
 * 
 * @author jlazos
 * 
 */
public class IndexProvider {

	private String institutionIdentifier;
	private String name;
	private String url;

	public String getInstitutionIdentifier() {
		return institutionIdentifier;
	}

	public void setInstitutionIdentifier(String institutionIdentifier) {
		this.institutionIdentifier = institutionIdentifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
