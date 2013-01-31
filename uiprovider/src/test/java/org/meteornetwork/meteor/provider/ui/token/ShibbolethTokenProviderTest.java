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
package org.meteornetwork.meteor.provider.ui.token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.junit.Test;
import org.meteornetwork.meteor.saml.Role;
import org.meteornetwork.meteor.saml.SecurityToken;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;

public class ShibbolethTokenProviderTest {

	private ShibbolethTokenProvider tokenProvider;

	public ShibbolethTokenProviderTest() {
		tokenProvider = new ShibbolethTokenProvider();
	}

	@Test
	public void testGetSecurityToken() throws SecurityTokenException {
		TestRequest request = new TestRequest();
		request.setHeader("AJP_Shib-Session-ID", "shib-sess-id");
		request.setHeader("AJP_Shib-Identity-Provider", "shib-identity-provider");
		request.setHeader("AJP_UserHandle", "theuser");
		request.setHeader("AJP_OrganizationId", "12345");
		request.setHeader("AJP_OrganizationIdType", "OPEID");
		request.setHeader("AJP_OrganizationType", "School");
		request.setHeader("AJP_AuthenticationProcessId", "1");
		request.setHeader("AJP_Level", "3");
		request.setHeader("AJP_Role", "FAA");
		request.setHeader("AJP_SSN", "110101010");
		request.setHeader("AJP_Lender", "54321");

		SecurityToken token = tokenProvider.getSecurityToken(request);
		Assert.assertEquals(token.getAssertionId(), "shib-sess-id");
		Assert.assertEquals(token.getIssuer(), "shib-identity-provider");
		Assert.assertEquals(token.getUserHandle(), "theuser");
		Assert.assertEquals(token.getOrganizationId(), "12345");
		Assert.assertEquals(token.getOrganizationIdType(), "OPEID");
		Assert.assertEquals(token.getOrganizationType(), "School");
		Assert.assertEquals(token.getAuthenticationProcessId(), "1");
		Assert.assertEquals(token.getLevel(), new Integer(3));
		Assert.assertEquals(token.getRole(), Role.FAA);
		Assert.assertEquals(token.getSsn(), "110101010");
		Assert.assertEquals(token.getLender(), "54321");
	}

	@SuppressWarnings("rawtypes")
	private class TestRequest implements HttpServletRequest {

		private Map<String, String> headers;

		public TestRequest() {
			headers = new HashMap<String, String>();
		}

		public void setHeader(String key, String value) {
			headers.put(key, value);
		}

		@Override
		public Object getAttribute(String arg0) {

			return null;
		}

		@Override
		public Enumeration getAttributeNames() {

			return null;
		}

		@Override
		public String getCharacterEncoding() {

			return null;
		}

		@Override
		public int getContentLength() {

			return 0;
		}

		@Override
		public String getContentType() {

			return null;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {

			return null;
		}

		@Override
		public String getLocalAddr() {

			return null;
		}

		@Override
		public String getLocalName() {

			return null;
		}

		@Override
		public int getLocalPort() {

			return 0;
		}

		@Override
		public Locale getLocale() {

			return null;
		}

		@Override
		public Enumeration getLocales() {

			return null;
		}

		@Override
		public String getParameter(String arg0) {

			return null;
		}

		@Override
		public Map getParameterMap() {

			return null;
		}

		@Override
		public Enumeration getParameterNames() {

			return null;
		}

		@Override
		public String[] getParameterValues(String arg0) {

			return null;
		}

		@Override
		public String getProtocol() {

			return null;
		}

		@Override
		public BufferedReader getReader() throws IOException {

			return null;
		}

		@Override
		public String getRealPath(String arg0) {

			return null;
		}

		@Override
		public String getRemoteAddr() {

			return null;
		}

		@Override
		public String getRemoteHost() {

			return null;
		}

		@Override
		public int getRemotePort() {

			return 0;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String arg0) {

			return null;
		}

		@Override
		public String getScheme() {

			return null;
		}

		@Override
		public String getServerName() {

			return null;
		}

		@Override
		public int getServerPort() {

			return 0;
		}

		@Override
		public boolean isSecure() {

			return false;
		}

		@Override
		public void removeAttribute(String arg0) {

		}

		@Override
		public void setAttribute(String arg0, Object arg1) {

		}

		@Override
		public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {

		}

		@Override
		public String getAuthType() {

			return null;
		}

		@Override
		public String getContextPath() {

			return null;
		}

		@Override
		public Cookie[] getCookies() {

			return null;
		}

		@Override
		public long getDateHeader(String arg0) {

			return 0;
		}

		@Override
		public String getHeader(String arg0) {
			return headers.get(arg0);
		}

		@Override
		public Enumeration getHeaderNames() {

			return null;
		}

		@Override
		public Enumeration getHeaders(String arg0) {

			return null;
		}

		@Override
		public int getIntHeader(String arg0) {

			return 0;
		}

		@Override
		public String getMethod() {

			return null;
		}

		@Override
		public String getPathInfo() {

			return null;
		}

		@Override
		public String getPathTranslated() {

			return null;
		}

		@Override
		public String getQueryString() {

			return null;
		}

		@Override
		public String getRemoteUser() {

			return null;
		}

		@Override
		public String getRequestURI() {

			return null;
		}

		@Override
		public StringBuffer getRequestURL() {

			return null;
		}

		@Override
		public String getRequestedSessionId() {

			return null;
		}

		@Override
		public String getServletPath() {

			return null;
		}

		@Override
		public HttpSession getSession() {

			return null;
		}

		@Override
		public HttpSession getSession(boolean arg0) {

			return null;
		}

		@Override
		public Principal getUserPrincipal() {

			return null;
		}

		@Override
		public boolean isRequestedSessionIdFromCookie() {

			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromURL() {

			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromUrl() {

			return false;
		}

		@Override
		public boolean isRequestedSessionIdValid() {

			return false;
		}

		@Override
		public boolean isUserInRole(String arg0) {

			return false;
		}

	}

}
