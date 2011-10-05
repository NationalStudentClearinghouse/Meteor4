package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;
import java.util.List;

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
}
