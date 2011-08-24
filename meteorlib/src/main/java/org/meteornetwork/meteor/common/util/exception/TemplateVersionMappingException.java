package org.meteornetwork.meteor.common.util.exception;

import org.meteornetwork.meteor.common.util.Version;


public class TemplateVersionMappingException extends Exception {

	private static final long serialVersionUID = 1658938313177827639L;

	private final Version versionFrom;
	private final Version versionTo;
	
	public TemplateVersionMappingException(String message, Version versionFrom, Version versionTo) {
		super(message + "( versionFrom = " + versionFrom.toString() + ", versionTo = " + versionTo.toString() + ")");
		
		this.versionFrom = versionFrom;
		this.versionTo = versionTo;
	}

	public Version getVersionFrom() {
		return versionFrom;
	}

	public Version getVersionTo() {
		return versionTo;
	}
}
