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
