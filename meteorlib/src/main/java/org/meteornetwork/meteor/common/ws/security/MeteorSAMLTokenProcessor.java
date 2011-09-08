package org.meteornetwork.meteor.common.ws.security;

import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.SAMLTokenProcessor;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.meteornetwork.meteor.common.registry.ProviderType;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 * Extends WSS4J SAMLTokenProcessor to retrieve public certificates from the
 * Meteor registry. Assumes crypto provider is MeteorCertificateStore
 * 
 * @author jlazos
 * 
 */
public class MeteorSAMLTokenProcessor extends SAMLTokenProcessor {

	private RegistryManager registryManager;
	
	@Override
	public Credential handleSAMLToken(Element token, RequestData data, Validator validator, WSDocInfo docInfo) throws WSSecurityException {
		if (!(data.getSigCrypto() instanceof MeteorCertificateStore)) {
			throw new WSSecurityException("Signature crypto implementation is not MeteorCertificateStore");
		}
		
		MeteorCertificateStore crypto = (MeteorCertificateStore) data.getSigCrypto();
		try {
			crypto.addCertificate(registryManager.getCertificate("AP33LTI", ProviderType.ACCESS));
		} catch (RegistryException e) {
			throw new WSSecurityException("Could not get X509 certificate from Meteor registry for institution " + "AP33LTI", e);
		}

		return super.handleSAMLToken(token, data, validator, docInfo);
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}
}
