package org.meteornetwork.meteor.provider.access.ws.security;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.saml.ext.SAMLCallback;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.opensaml.common.SAMLVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class SamlCreationCallbackHandler implements CallbackHandler {

	private static final String METEORNETWORK_ORG = "meteornetwork.org";

	private Properties authenticationProperties;

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof SAMLCallback) {
				generateSaml((SAMLCallback) callback);
			}
		}
	}

	private void generateSaml(SAMLCallback callback) throws UnknownHostException {
		RequestInfo requestInfo = getRequestInfo();

		SecurityTokenBeanMethodImpl token = new SecurityTokenBeanMethodImpl();
		token.setSubjectName(authenticationProperties.getProperty("authentication.identifier"));
		token.setProviderType(ProviderType.ACCESS);
		token.setOrganizationId(requestInfo.getSecurityToken().getOrganizationId());
		token.setOrganizationIdType(requestInfo.getSecurityToken().getOrganizationIdType());
		token.setOrganizationType(requestInfo.getSecurityToken().getOrganizationType());
		token.setAuthenticationProcessId(authenticationProperties.getProperty("authentication.process.identifier"));
		token.setLevel(requestInfo.getSecurityToken().getLevel());
		token.setUserHandle(requestInfo.getSecurityToken().getUserHandle());
		token.setRole(requestInfo.getSecurityToken().getRole());
		token.setSsn(requestInfo.getSecurityToken().getSsn());
		token.setLender(requestInfo.getSecurityToken().getLender());

		callback.setSamlVersion(SAMLVersion.VERSION_20);
		callback.setIssuer(METEORNETWORK_ORG);

		SubjectBean subject = token.createSubjectBean();
		subject.setSubjectNameQualifier(METEORNETWORK_ORG);
		callback.setSubject(subject);

		DateTime currentDateTime = new DateTime(DateTimeZone.UTC);

		try {
			callback.setConditions(token.createConditionsBean(currentDateTime));
			callback.getConditions().setNotAfter(callback.getConditions().getNotBefore().plusSeconds(60));
		} catch (SecurityTokenException e) {
			/*
			 * will not be thrown because conditions NotBefore and NotOnOrAfter
			 * will be generated
			 */
		}

		AuthenticationStatementBean authenticationStatement = token.createAuthenticationStatementBean(currentDateTime);
		authenticationStatement.setAuthenticationMethod(METEORNETWORK_ORG);
		callback.setAuthenticationStatementData(Collections.singletonList(authenticationStatement));

		AttributeStatementBean attributeStatement = token.createAttributeStatementBean();
		callback.setAttributeStatementData(Collections.singletonList(attributeStatement));
	}

	protected RequestInfo getRequestInfo() {
		// method injection implemented by Spring
		return null;
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
