package org.meteornetwork.meteor.provider.access.ws.security;

import org.apache.ws.security.saml.ext.bean.AttributeStatementBean;
import org.apache.ws.security.saml.ext.bean.AuthenticationStatementBean;
import org.apache.ws.security.saml.ext.bean.ConditionsBean;
import org.apache.ws.security.saml.ext.bean.SubjectBean;
import org.joda.time.DateTime;
import org.meteornetwork.meteor.saml.SecurityTokenImpl;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class SecurityTokenBeanMethodImpl extends SecurityTokenImpl {

	public SubjectBean createSubjectBean() {
		return super.createSubjectBean();
	}

	public ConditionsBean createConditionsBean(DateTime currentDateTime) throws SecurityTokenException {
		return super.createConditionsBean(currentDateTime);
	}

	public AuthenticationStatementBean createAuthenticationStatementBean(DateTime currentDateTime) {
		return super.createAuthenticationStatementBean(currentDateTime);
	}

	public AttributeStatementBean createAttributeStatementBean() {
		return super.createAttributeStatementBean();
	}

}
