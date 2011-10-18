package org.meteornetwork.meteor.common.ws.security;

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
import org.meteornetwork.meteor.saml.ProviderType;
import org.opensaml.common.SAMLVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class RegistrySamlCreationCallbackHandler implements CallbackHandler {

	private static final String METEORNETWORK_ORG = "meteornetwork.org";
	private static final Integer VALIDITY_PERIOD_DEFAULT = 120; // seconds

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
		callback.setSamlVersion(SAMLVersion.VERSION_20);
		callback.setIssuer(METEORNETWORK_ORG);

		SubjectBean subject = createSubjectBean(authenticationProperties.getProperty("authentication.identifier"));
		subject.setSubjectNameQualifier(METEORNETWORK_ORG);
		callback.setSubject(subject);

		DateTime currentDateTime = new DateTime(DateTimeZone.UTC);

		AuthenticationStatementBean authenticationStatement = createAuthenticationStatementBean(currentDateTime);
		authenticationStatement.setAuthenticationMethod(METEORNETWORK_ORG);
		callback.setAuthenticationStatementData(Collections.singletonList(authenticationStatement));

		callback.setSamlVersion(SAMLVersion.VERSION_20);
		callback.setIssuer(METEORNETWORK_ORG);

		ProviderType providerType = ProviderType.valueOfType(authenticationProperties.getProperty("authentication.providertype"));
		if (providerType != null) {
			AttributeStatementBean attributeStatement = new AttributeStatementBean();
			AttributeBean attributeBean = new AttributeBean();
			attributeBean.setSimpleName("ProviderType");
			attributeBean.setAttributeValues(Collections.singletonList(providerType.getType()));
			attributeStatement.getSamlAttributes().add(attributeBean);
			callback.setAttributeStatementData(Collections.singletonList(attributeStatement));
		}

		callback.setConditions(createConditionsBean(currentDateTime));
	}

	private SubjectBean createSubjectBean(String subjectName) {
		SubjectBean subject = new SubjectBean();
		subject.setSubjectName(subjectName);
		return subject;
	}

	private AuthenticationStatementBean createAuthenticationStatementBean(DateTime currentDateTime) {
		AuthenticationStatementBean authenticationStatement = new AuthenticationStatementBean();
		authenticationStatement.setAuthenticationInstant(currentDateTime);

		try {
			SubjectLocalityBean subjectLocality = new SubjectLocalityBean();
			subjectLocality.setIpAddress(InetAddress.getLocalHost().getHostAddress());
			subjectLocality.setDnsAddress(InetAddress.getLocalHost().getHostName());
			authenticationStatement.setSubjectLocality(subjectLocality);
		} catch (UnknownHostException e) {
			// do not set subject locality if ip and dns addresses cannot be
			// resolved
		}

		return authenticationStatement;
	}

	private ConditionsBean createConditionsBean(DateTime currentDateTime) {
		ConditionsBean conditions = new ConditionsBean();
		DateTime notOnOrAfter = currentDateTime.plusSeconds(VALIDITY_PERIOD_DEFAULT);
		conditions.setNotBefore(currentDateTime);
		conditions.setNotAfter(notOnOrAfter);
		return conditions;
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
