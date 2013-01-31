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

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;

@WebService
public interface RegistryService {

	@WebMethod(operationName = "GetCertificate")
	@WebResult(name = "GetCertificateResponse")
	byte[] getCertificate(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;

	@WebMethod(operationName = "GetIndexProviders")
	@WebResult(name = "GetIndexProvidersResponse")
	ArrayList<IndexProvider> getIndexProviders(@WebParam(name = "MeteorVersion") Version meteorVersion) throws RegistryException;

	@WebMethod(operationName = "GetDataProvider")
	@WebResult(name = "GetDataProviderResponse")
	DataProvider getDataProvider(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId) throws RegistryException;

	@WebMethod(operationName = "GetAllDataProviders")
	@WebResult(name = "GetAllDataProvidersResponse")
	ArrayList<DataProvider> getAllDataProviders() throws RegistryException;

	@WebMethod(operationName = "GetAliases")
	@WebResult(name = "GetAliasesResponse")
	ArrayList<String> getAliases(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;

	@WebMethod(operationName = "GetAuthenticationLevel")
	@WebResult(name = "GetAuthenticationLevelResponse")
	Integer getAuthenticationLevel(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "AuthenticationProcessId") String authProcId, @WebParam(name = "ProviderType") ProviderType providerType, @WebParam(name = "Role") Role role) throws RegistryException;

	@WebMethod(operationName = "GetRoles")
	@WebResult(name = "GetRolesResponse")
	ArrayList<Role> getRoles(@WebParam(name = "MeteorInstitutionId") String meteorInstitutionId, @WebParam(name = "AuthenticationProcessId") String authProcId, @WebParam(name = "ProviderType") ProviderType providerType) throws RegistryException;
}
