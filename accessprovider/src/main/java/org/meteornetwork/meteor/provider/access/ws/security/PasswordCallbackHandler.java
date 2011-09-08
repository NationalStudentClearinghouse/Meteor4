package org.meteornetwork.meteor.provider.access.ws.security;

import java.io.IOException;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("passwordCallbackHandler")
public class PasswordCallbackHandler implements CallbackHandler {

	private Properties authenticationProperties;
	
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof PasswordCallback) {
				PasswordCallback pwdCallback = (PasswordCallback) callback;
				String password = authenticationProperties.getProperty("org.apache.ws.security.saml.issuer.key.password");
				pwdCallback.setPassword(password.toCharArray());
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
