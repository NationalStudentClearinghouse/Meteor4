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
package org.meteornetwork.meteor.common.ws;

import java.io.Serializable;

public class SubmitHPCResponse implements Serializable {

	private static final long serialVersionUID = 7409528203878968269L;
	
	private String returnXml;

	public SubmitHPCResponse() {
	}

	public SubmitHPCResponse(String returnXml) {
		this.returnXml = returnXml;
	}

	public String getReturnXml() {
		return returnXml;
	}

	public void setReturnXml(String returnXml) {
		this.returnXml = returnXml;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((returnXml == null) ? 0 : returnXml.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubmitHPCResponse other = (SubmitHPCResponse) obj;
		if (returnXml == null) {
			if (other.returnXml != null)
				return false;
		} else if (!returnXml.equals(other.returnXml))
			return false;
		return true;
	}

}
