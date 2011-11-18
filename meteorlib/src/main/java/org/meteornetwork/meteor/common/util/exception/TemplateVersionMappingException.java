/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
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
