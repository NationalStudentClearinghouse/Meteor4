package org.meteornetwork.meteor.common.ws.security;

import java.util.List;

import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.SignatureProcessor;
import org.meteornetwork.meteor.common.registry.ProviderType;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 * Extends WSS4J SignatureProcessor to retrieve public certificates from the
 * Meteor registry. Assumes crypto provider is MeteorCertificateStore
 * 
 * @author jlazos
 * 
 */
public class MeteorSignatureProcessor extends SignatureProcessor {

	private RegistryManager registryManager;

	@Override
	public List<WSSecurityEngineResult> handleToken(Element elem, RequestData data, WSDocInfo wsDocInfo) throws WSSecurityException {
		if (!(data.getSigCrypto() instanceof MeteorCertificateStore)) {
			throw new WSSecurityException("Signature crypto implementation is not MeteorCertificateStore");
		}
		
		MeteorCertificateStore crypto = (MeteorCertificateStore) data.getSigCrypto();
		try {
			crypto.addCertificate(registryManager.getCertificate("AP33LTI", ProviderType.ACCESS));
		} catch (RegistryException e) {
			throw new WSSecurityException("Could not get X509 certificate from Meteor registry for institution " + "AP33LTI", e);
		}

		return super.handleToken(elem, data, wsDocInfo);
	}

	public RegistryManager getRegistryManager() {
		return registryManager;
	}

	@Autowired
	public void setRegistryManager(RegistryManager registryManager) {
		this.registryManager = registryManager;
	}

}
