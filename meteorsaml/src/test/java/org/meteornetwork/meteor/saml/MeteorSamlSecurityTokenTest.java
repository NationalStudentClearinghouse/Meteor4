package org.meteornetwork.meteor.saml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.meteornetwork.meteor.saml.exception.SecurityTokenException;
import org.opensaml.xml.ConfigurationException;

public class MeteorSamlSecurityTokenTest extends TestCase {

	private final Date currentDate;

	SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public MeteorSamlSecurityTokenTest() throws ParseException, ConfigurationException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy Z", Locale.US);
		currentDate = dateFormat.parse("April 5, 2063 -0000");
	}

	@Test
	public void testToXMLStringFAA() throws SecurityException, NoSuchMethodException, SecurityTokenException, ParseException, IOException {

		MeteorSamlSecurityToken token = new MeteorSamlSecurityTokenMock();

		token.setAssertionId("1317048812656");
		token.setIssuer("nchelp.org/meteor");
		token.setSubjectName("ap2");
		token.setSubjectLocalityIpAddress("192.168.1.105");
		token.setSubjectLocalityDnsAddress("lticslt-06");
		token.setOrganizationId("12355");
		token.setOrganizationIdType("OPEID");
		token.setOrganizationType("SCHOOL");
		token.setAuthenticationProcessId("1");
		token.setLevel(3);
		token.setUserHandle("username");
		token.setRole(Role.FAA);

		String assertionString = token.toXMLString();
		SecurityToken tokenResult = MeteorSamlSecurityToken.fromXML(assertionString);
		Assert.assertEquals("1317048812656", tokenResult.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", tokenResult.getIssuer());
		Assert.assertEquals("ap2", tokenResult.getSubjectName());
		Assert.assertEquals("192.168.1.105", tokenResult.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", tokenResult.getSubjectLocalityDnsAddress());
		Assert.assertEquals("12355", tokenResult.getOrganizationId());
		Assert.assertEquals("OPEID", tokenResult.getOrganizationIdType());
		Assert.assertEquals("SCHOOL", tokenResult.getOrganizationType());
		Assert.assertEquals("1", tokenResult.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), tokenResult.getLevel());
		Assert.assertEquals("username", tokenResult.getUserHandle());
		Assert.assertEquals(Role.FAA, tokenResult.getRole());
	}

	@Test
	public void testFromXMLFAA() throws IOException, SecurityTokenException, ParseException {
		String assertionXml = getXmlFromFile("assertion-faa-old.xml");
		MeteorSamlSecurityToken result = MeteorSamlSecurityToken.fromXML(assertionXml);
		Assert.assertEquals("1317048812656", result.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", result.getIssuer());
		Assert.assertEquals("ap2", result.getSubjectName());
		Assert.assertEquals("192.168.1.105", result.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", result.getSubjectLocalityDnsAddress());
		Assert.assertEquals(parseDate("2011-09-26T14:53:32+0000").getTime(), result.getConditionsNotBefore().getMillis());
		Assert.assertEquals(parseDate("2011-09-26T18:53:32+0000").getTime(), result.getConditionsNotOnOrAfter().getMillis());
		Assert.assertEquals("12355", result.getOrganizationId());
		Assert.assertEquals("OPEID", result.getOrganizationIdType());
		Assert.assertEquals("SCHOOL", result.getOrganizationType());
		Assert.assertEquals("1", result.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), result.getLevel());
		Assert.assertEquals("username", result.getUserHandle());
		Assert.assertEquals(Role.FAA, result.getRole());
	}

	@Test
	public void testToXMLStringBorrower() throws SecurityException, NoSuchMethodException, SecurityTokenException, ParseException, IOException {

		SecurityToken token = new MeteorSamlSecurityTokenMock();

		token.setAssertionId("1317048778343");
		token.setIssuer("nchelp.org/meteor");
		token.setSubjectName("ap2");
		token.setSubjectLocalityIpAddress("192.168.1.105");
		token.setSubjectLocalityDnsAddress("lticslt-06");
		token.setAuthenticationProcessId("1");
		token.setLevel(3);
		token.setUserHandle("username");
		token.setRole(Role.BORROWER);
		token.setSsn("101011001");

		String assertionString = token.toXMLString();
		SecurityToken tokenResult = MeteorSamlSecurityToken.fromXML(assertionString);
		Assert.assertEquals("1317048778343", tokenResult.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", tokenResult.getIssuer());
		Assert.assertEquals("ap2", tokenResult.getSubjectName());
		Assert.assertEquals("192.168.1.105", tokenResult.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", tokenResult.getSubjectLocalityDnsAddress());
		Assert.assertEquals("1", tokenResult.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), tokenResult.getLevel());
		Assert.assertEquals("username", tokenResult.getUserHandle());
		Assert.assertEquals(Role.BORROWER, tokenResult.getRole());
		Assert.assertEquals("101011001", tokenResult.getSsn());
	}

	@Test
	public void testFromXMLBorrower() throws IOException, SecurityTokenException, ParseException {
		String assertionXml = getXmlFromFile("assertion-borrower-old.xml");
		MeteorSamlSecurityToken result = MeteorSamlSecurityToken.fromXML(assertionXml);
		Assert.assertEquals("1317048778343", result.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", result.getIssuer());
		Assert.assertEquals("ap2", result.getSubjectName());
		Assert.assertEquals("192.168.1.105", result.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", result.getSubjectLocalityDnsAddress());
		Assert.assertEquals(parseDate("2011-09-26T14:52:58+0000").getTime(), result.getConditionsNotBefore().getMillis());
		Assert.assertEquals(parseDate("2011-09-26T18:52:58+0000").getTime(), result.getConditionsNotOnOrAfter().getMillis());
		Assert.assertEquals("1", result.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), result.getLevel());
		Assert.assertEquals("username", result.getUserHandle());
		Assert.assertEquals(Role.BORROWER, result.getRole());
		Assert.assertEquals("101011001", result.getSsn());
	}

	@Test
	public void testToXMLStringLender() throws SecurityException, NoSuchMethodException, SecurityTokenException, ParseException, IOException {

		MeteorSamlSecurityToken token = new MeteorSamlSecurityTokenMock();

		token.setAssertionId("1317048794609");
		token.setIssuer("nchelp.org/meteor");
		token.setSubjectName("ap2");
		token.setSubjectLocalityIpAddress("192.168.1.105");
		token.setSubjectLocalityDnsAddress("lticslt-06");
		token.setAuthenticationProcessId("1");
		token.setLevel(3);
		token.setUserHandle("username");
		token.setRole(Role.LENDER);
		token.setLender("12345");

		String assertionString = token.toXMLString();
		SecurityToken tokenResult = MeteorSamlSecurityToken.fromXML(assertionString);
		Assert.assertEquals("1317048794609", tokenResult.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", tokenResult.getIssuer());
		Assert.assertEquals("ap2", tokenResult.getSubjectName());
		Assert.assertEquals("192.168.1.105", tokenResult.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", tokenResult.getSubjectLocalityDnsAddress());
		Assert.assertEquals("1", tokenResult.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), tokenResult.getLevel());
		Assert.assertEquals("username", tokenResult.getUserHandle());
		Assert.assertEquals(Role.LENDER, tokenResult.getRole());
		Assert.assertEquals("12345", tokenResult.getLender());
	}

	@Test
	public void testFromXMLLender() throws IOException, SecurityTokenException, ParseException {
		String assertionXml = getXmlFromFile("assertion-lender-old.xml");
		MeteorSamlSecurityToken result = MeteorSamlSecurityToken.fromXML(assertionXml);
		Assert.assertEquals("1317048794609", result.getAssertionId());
		Assert.assertEquals("nchelp.org/meteor", result.getIssuer());
		Assert.assertEquals("ap2", result.getSubjectName());
		Assert.assertEquals("192.168.1.105", result.getSubjectLocalityIpAddress());
		Assert.assertEquals("lticslt-06", result.getSubjectLocalityDnsAddress());
		Assert.assertEquals(parseDate("2011-09-26T14:53:14+0000").getTime(), result.getConditionsNotBefore().getMillis());
		Assert.assertEquals(parseDate("2011-09-26T18:53:14+0000").getTime(), result.getConditionsNotOnOrAfter().getMillis());
		Assert.assertEquals("1", result.getAuthenticationProcessId());
		Assert.assertEquals(Integer.valueOf(3), result.getLevel());
		Assert.assertEquals("username", result.getUserHandle());
		Assert.assertEquals(Role.LENDER, result.getRole());
		Assert.assertEquals("12345", result.getLender());
	}

	private String getXmlFromFile(String fileName) throws IOException {
		File file = new File(this.getClass().getResource(fileName).getFile());
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			FileChannel fc = fileInputStream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.forName("utf-8").decode(bb).toString();
		} finally {
			fileInputStream.close();
		}
	}

	private Date parseDate(String dateString) throws ParseException {
		return DATE_FORMATTER.parse(dateString);
	}

	private class MeteorSamlSecurityTokenMock extends MeteorSamlSecurityToken {

		@Override
		protected DateTime getCurrentDateTime() {
			return new DateTime(currentDate, DateTimeZone.UTC);
		}
	}
}
