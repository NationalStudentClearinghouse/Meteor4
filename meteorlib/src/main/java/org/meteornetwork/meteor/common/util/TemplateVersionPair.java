package org.meteornetwork.meteor.common.util;

import javax.xml.transform.Templates;

public class TemplateVersionPair {

	private String versionFrom;
	private String versionTo;
	private Templates template;

	public boolean matches(Version versionFrom, Version versionTo) {
		return versionFrom.matches(this.versionFrom) && versionTo.matches(this.versionTo);
	}

	public String getVersionFrom() {
		return versionFrom;
	}

	public void setVersionFrom(String versionFrom) {
		this.versionFrom = versionFrom;
	}

	public String getVersionTo() {
		return versionTo;
	}

	public void setVersionTo(String versionTo) {
		this.versionTo = versionTo;
	}

	public Templates getTemplate() {
		return template;
	}

	public void setTemplate(Templates template) {
		this.template = template;
	}

}
