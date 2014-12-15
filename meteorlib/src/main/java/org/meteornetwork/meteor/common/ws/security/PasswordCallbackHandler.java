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
	@Qualifier("authenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

}
