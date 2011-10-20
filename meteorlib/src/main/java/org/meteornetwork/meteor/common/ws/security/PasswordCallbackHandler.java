package org.meteornetwork.meteor.common.ws.security;

import java.io.IOException;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PasswordCallbackHandler implements CallbackHandler {

	private Properties authenticationProperties;
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof PasswordCallback) {
				PasswordCallback pwdCallback = (PasswordCallback) callback;
				String password = authenticationProperties.getProperty("org.apache.ws.security.saml.issuer.key.password");
				if (password == null) {
					password = authenticationProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.private.password");
				}
				pwdCallback.setPassword(password.toCharArray());
			} else if (callback instanceof WSPasswordCallback) {
				WSPasswordCallback pwdCallback = (WSPasswordCallback) callback;
				String password = authenticationProperties.getProperty("org.apache.ws.security.saml.issuer.key.password");
				if (password == null) {
					password = authenticationProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.private.password");
				}
				pwdCallback.setPassword(password);
			}
		}
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

}
