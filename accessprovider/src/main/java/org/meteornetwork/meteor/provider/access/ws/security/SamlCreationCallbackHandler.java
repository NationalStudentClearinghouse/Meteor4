package org.meteornetwork.meteor.provider.access.ws.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Properties;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.saml.ext.SAMLCallback;
import org.apache.ws.security.saml.ext.bean.AttributeBean;
import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.apache.ws.security.saml.ext.bean.SubjectLocalityBean;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.meteornetwork.meteor.common.registry.ProviderType;
import org.meteornetwork.meteor.common.security.RequestInfo;
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

		/*
		 * Assertion info
		 */
		callback.setSamlVersion(SAMLVersion.VERSION_20);
		callback.setIssuer(METEORNETWORK_ORG);

		/*
		 * Subject
		 */
		SubjectBean subject = new SubjectBean();
		subject.setSubjectName(authenticationProperties.getProperty("authentication.identifier"));
		subject.setSubjectNameQualifier(METEORNETWORK_ORG);
		callback.setSubject(subject);

		DateTime currentDateTime = new DateTime(DateTimeZone.UTC);

		/*
		 * Conditions
		 */
		ConditionsBean conditions = new ConditionsBean();
		conditions.setNotBefore(currentDateTime);
		conditions.setNotAfter(currentDateTime.plusHours(4));
		callback.setConditions(conditions);

		/*
		 * Authentication statement
		 */
		AuthenticationStatementBean authenticationStatement = new AuthenticationStatementBean();
		authenticationStatement.setAuthenticationInstant(currentDateTime);
		authenticationStatement.setAuthenticationMethod(METEORNETWORK_ORG);
		SubjectLocalityBean subjectLocality = new SubjectLocalityBean();
		subjectLocality.setIpAddress(InetAddress.getLocalHost().getHostAddress());
		subjectLocality.setDnsAddress(InetAddress.getLocalHost().getHostName());

		callback.setAuthenticationStatementData(Collections.singletonList(authenticationStatement));
		
		/*
		 * Attributes
		 */
		AttributeStatementBean attributeStatement = new AttributeStatementBean();

		attributeStatement.getSamlAttributes().add(createAttributeBean("ProviderType", ProviderType.ACCESS.getType()));
		
		if (requestInfo.getOrganizationID() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationID", requestInfo.getOrganizationID()));
		}

		if (requestInfo.getOrganizationIDType() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationIDType", requestInfo.getOrganizationIDType()));
		}

		if (requestInfo.getOrganizationType() != null) {
			attributeStatement.getSamlAttributes().add(createAttributeBean("OrganizationType", requestInfo.getOrganizationType()));
		}

		attributeStatement.getSamlAttributes().add(createAttributeBean("AuthenticationProcessID", authenticationProperties.getProperty("authentication.process.identifier")));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Level", requestInfo.getLevel().toString()));
		attributeStatement.getSamlAttributes().add(createAttributeBean("UserHandle", requestInfo.getUserHandle()));
		attributeStatement.getSamlAttributes().add(createAttributeBean("Role", requestInfo.getRole().getName()));
	
		callback.setAttributeStatementData(Collections.singletonList(attributeStatement));
	}

	private AttributeBean createAttributeBean(String simpleName, String value) {
		AttributeBean attributeBean = new AttributeBean();
		attributeBean.setSimpleName(simpleName);
		attributeBean.setAttributeValues(Collections.singletonList(value));
		return attributeBean;
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
