package org.meteornetwork.meteor.common.ws.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class MeteorSamlCreationCallbackHandler implements CallbackHandler {

	private static final Logger LOG = LoggerFactory.getLogger(RegistrySamlCreationCallbackHandler.class);

	private static final String METEORNETWORK_ORG = "meteornetwork.org";
	private static final Integer SAML_TOKEN_VALIDITY_PERIOD = 60; // seconds

	private Properties authenticationProperties;

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if (callback instanceof SAMLCallback) {
				generateSaml((SAMLCallback) callback);
			}
		}
	}

	private void generateSaml(SAMLCallback callback) {
		LOG.debug("Adding assertion to meteor request");
		RequestInfo requestInfo = getRequestInfo();

		SecurityTokenBeanMethodImpl token = new SecurityTokenBeanMethodImpl();
		token.setSubjectName(authenticationProperties.getProperty("authentication.identifier"));
		token.setProviderType(ProviderType.valueOfType(authenticationProperties.getProperty("authentication.providertype")));
		token.setOrganizationId(requestInfo.getSecurityToken().getOrganizationId());
		token.setOrganizationIdType(requestInfo.getSecurityToken().getOrganizationIdType());
		token.setOrganizationType(requestInfo.getSecurityToken().getOrganizationType());
		token.setAuthenticationProcessId(requestInfo.getSecurityToken().getAuthenticationProcessId());
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
			callback.getConditions().setNotAfter(callback.getConditions().getNotBefore().plusSeconds(SAML_TOKEN_VALIDITY_PERIOD));
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
