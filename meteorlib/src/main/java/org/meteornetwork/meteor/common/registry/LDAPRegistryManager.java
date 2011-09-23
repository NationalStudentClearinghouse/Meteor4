package org.meteornetwork.meteor.common.registry;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapName;

import org.meteornetwork.meteor.saml.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

@Service
public class LDAPRegistryManager implements RegistryManager {

	private LdapTemplate ldapTemplate;
	private LdapTemplate ldapFailoverTemplate;

	@Override
	public X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		// TODO: catch javax.naming.CommunicationException and try failover
		return getCertificate(meteorInstitutionId, providerType, ldapTemplate);
	}

	private X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType, LdapTemplate ldapTemplate) throws RegistryException {
		String preferredEncryption = (String) ldapTemplate.lookup("File=" + providerType.getType() + ",FileTypeFamily=Meteor,Institution=" + meteorInstitutionId, new AttributesMapper() {
			public Object mapFromAttributes(Attributes attrs) throws NamingException {
				return attrs.get("PreferredEncryption").get();
			}
		});

		try {
			preferredEncryption = stripBaseDn(preferredEncryption);
		} catch (InvalidNameException e1) {
			throw new RegistryException(e1);
		}

		byte[] certificateBinary = (byte[]) ldapTemplate.lookup(preferredEncryption, new AttributesMapper() {
			public Object mapFromAttributes(Attributes attrs) throws NamingException {
				return attrs.get("CryptData;binary").get();
			}
		});

		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

			ByteArrayInputStream certInputStream = new ByteArrayInputStream(certificateBinary);
			return (X509Certificate) certificateFactory.generateCertificate(certInputStream);
		} catch (CertificateException e) {
			throw new RegistryException(e);
		}
	}

	private String stripBaseDn(String ldapName) throws InvalidNameException {
		LdapName name = new LdapName(ldapName);
		name.remove(0);
		return name.toString();
	}

	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	@Autowired
	@Qualifier("ldapTemplate")
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public LdapTemplate getLdapFailoverTemplate() {
		return ldapFailoverTemplate;
	}

	@Autowired
	@Qualifier("ldapFailoverTemplate")
	public void setLdapFailoverTemplate(LdapTemplate ldapFailoverTemplate) {
		this.ldapFailoverTemplate = ldapFailoverTemplate;
	}

}