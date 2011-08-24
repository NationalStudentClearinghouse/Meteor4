package org.meteornetwork.meteor.common.util;

import java.util.List;

import javax.xml.transform.Templates;

import org.meteornetwork.meteor.common.util.exception.TemplateVersionMappingException;


public class TemplateVersionMapper {

	private List<TemplateVersionPair> templateVersionPairs;

	public Templates getTemplateForVersions(String versionFrom, String versionTo) throws TemplateVersionMappingException {
		return getTemplateForVersions(new Version(versionFrom), new Version(versionTo));
	}
	
	public Templates getTemplateForVersions(Version versionFrom, Version versionTo) throws TemplateVersionMappingException {
		for (TemplateVersionPair pair : templateVersionPairs) {
			if (pair.matches(versionFrom, versionTo)) {
				return pair.getTemplate();
			}
		}
		
		throw new TemplateVersionMappingException("Unable to find template for versions", versionFrom, versionTo);
	}
	
	public List<TemplateVersionPair> getTemplateVersionPairs() {
		return templateVersionPairs;
	}

	public void setTemplateVersionPairs(List<TemplateVersionPair> templateVersionPairs) {
		this.templateVersionPairs = templateVersionPairs;
	}
}
