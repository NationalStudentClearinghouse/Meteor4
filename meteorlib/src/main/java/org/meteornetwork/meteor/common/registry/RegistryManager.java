package org.meteornetwork.meteor.common.registry;

import java.security.cert.X509Certificate;

import org.meteornetwork.meteor.saml.ProviderType;

/**
 * Interface for interacting with the Meteor Registry
 * 
 * @author jlazos
 */
public interface RegistryManager {

	X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException;
}
