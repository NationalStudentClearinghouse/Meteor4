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
package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;
import java.util.List;

import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;

/**
 * Interface for interacting with the Meteor Registry
 * 
 * @author jlazos
 */
public interface RegistryManager {

	/**
	 * Retrive the public certificate of a Meteor provider
	 * 
	 * @param meteorInstitutionId
	 *            the institution ID of the provider
	 * @param providerType
	 *            the provider type (e.g. Access, Index)
	 * @return the public certificate of the specified Meteor provider
	 * @throws RegistryException
	 *             Some processing error prevents the X509Certificate from being
	 *             retrieved
	 */
	X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException;

	/**
	 * Gets the list of Index Providers that support the specified version of
	 * Meteor
	 * 
	 * @param meteorVersion
	 *            the version of meteor returned index providers must support
	 * @return list of Index Provider URLs
	 * @throws RegistryException
	 *             Some processing error prevents the Index Provider URLs from
	 *             being retrieved
	 */
	List<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException;

	/**
	 * Gets connection information for a specific data provider in the registry
	 * 
	 * @param meteorInstitutionId
	 *            the ID of the data provider to access
	 * @return connection information for the specified data provider id
	 * @throws RegistryException
	 *             Some processing error prevents the Data Provider from being
	 *             retrieved
	 */
	DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException;

	/**
	 * Gets connection information for all active data providers in the registry
	 * 
	 * @return all active data providers in the registry
	 * @throws RegistryException
	 *             Some processing error prevents the Data Providers from being
	 *             retrieved
	 */
	List<DataProvider> getAllDataProviders() throws RegistryException;

	/**
	 * Gets the list of aliases for a meteor institution.
	 * 
	 * @param meteorInstitutionId
	 *            the institution to get aliases for
	 * @param providerType
	 *            the provider type of the institution
	 * @return the list of aliases for a meteor institution
	 */
	List<String> getAliases(String meteorInstitutionId, ProviderType providerType) throws RegistryException;

	/**
	 * Gets the minimum authentication level accepted by the provider for the
	 * specified role
	 * 
	 * @param meteorInstitutionId
	 *            id of the provider
	 * @param authProcId
	 *            the authentication process being considered
	 * @param providerType
	 *            the provider type of the provider
	 * @param role
	 *            the role whose authentication level to return
	 * @return the minimum authentication level the specified provider will
	 *         accept for the specified role
	 * 
	 * @throws RegistryException
	 */
	Integer getAuthenticationLevel(String meteorInstitutionId, String authProcId, ProviderType providerType, Role role) throws RegistryException;

	/**
	 * Gets the roles allowed by the specified provider
	 * 
	 * @param meteorInstitutionId
	 *            id of the provider
	 * @param authProcId
	 *            the authentication process being considered
	 * @param providerType
	 *            the provider type of the provider
	 * @return the roles allowed by the specified provider
	 * @throws RegistryException
	 */
	List<Role> getRoles(String meteorInstitutionId, String authProcId, ProviderType providerType) throws RegistryException;
}
