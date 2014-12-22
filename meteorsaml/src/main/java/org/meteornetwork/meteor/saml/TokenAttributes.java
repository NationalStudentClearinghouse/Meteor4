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
package org.meteornetwork.meteor.saml;

import java.io.Serializable;

public class TokenAttributes implements Serializable{

	private static final long serialVersionUID = 1L;
	private ProviderType providerType;
	private String organizationId;
	private String organizationIdType;
	private String organizationType;
	private String authenticationProcessId;
	private Integer level;
	private String userHandle;
	private Role role;
	private String ssn;
	private String lender;

	public ProviderType getProviderType() {
		return providerType;
	}

	public void setProviderType(ProviderType providerType) {
		this.providerType = providerType;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationIdType() {
		return organizationIdType;
	}

	public void setOrganizationIdType(String organizationIdType) {
		this.organizationIdType = organizationIdType;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getAuthenticationProcessId() {
		return authenticationProcessId;
	}

	public void setAuthenticationProcessId(String authenticationProcessId) {
		this.authenticationProcessId = authenticationProcessId;
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

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getLender() {
		return lender;
	}

	public void setLender(String lender) {
		this.lender = lender;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authenticationProcessId == null) ? 0 : authenticationProcessId.hashCode());
		result = prime * result + ((lender == null) ? 0 : lender.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((organizationId == null) ? 0 : organizationId.hashCode());
		result = prime * result + ((organizationIdType == null) ? 0 : organizationIdType.hashCode());
		result = prime * result + ((organizationType == null) ? 0 : organizationType.hashCode());
		result = prime * result + ((providerType == null) ? 0 : providerType.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((ssn == null) ? 0 : ssn.hashCode());
		result = prime * result + ((userHandle == null) ? 0 : userHandle.hashCode());
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
		TokenAttributes other = (TokenAttributes) obj;
		if (authenticationProcessId == null) {
			if (other.authenticationProcessId != null)
				return false;
		} else if (!authenticationProcessId.equals(other.authenticationProcessId))
			return false;
		if (lender == null) {
			if (other.lender != null)
				return false;
		} else if (!lender.equals(other.lender))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (organizationId == null) {
			if (other.organizationId != null)
				return false;
		} else if (!organizationId.equals(other.organizationId))
			return false;
		if (organizationIdType == null) {
			if (other.organizationIdType != null)
				return false;
		} else if (!organizationIdType.equals(other.organizationIdType))
			return false;
		if (organizationType == null) {
			if (other.organizationType != null)
				return false;
		} else if (!organizationType.equals(other.organizationType))
			return false;
		if (providerType != other.providerType)
			return false;
		if (role != other.role)
			return false;
		if (ssn == null) {
			if (other.ssn != null)
				return false;
		} else if (!ssn.equals(other.ssn))
			return false;
		if (userHandle == null) {
			if (other.userHandle != null)
				return false;
		} else if (!userHandle.equals(other.userHandle))
			return false;
		return true;
	}
}
