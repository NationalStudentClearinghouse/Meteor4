package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;
import java.util.List;

import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;

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
}
