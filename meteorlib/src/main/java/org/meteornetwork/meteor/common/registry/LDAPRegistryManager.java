package org.meteornetwork.meteor.common.registry;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

public class LDAPRegistryManager implements RegistryManager {

	private static final Log LOG = LogFactory.getLog(LDAPRegistryManager.class);

	private static final String INDEX_PROVIDER_SEARCH_CRITERIA = "(&(File=" + ProviderType.INDEX.getType() + ")(objectClass=File))";
	private LdapTemplate ldapTemplate;
	private LdapTemplate ldapFailoverTemplate;

	@Override
	public X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		try {
			return getCertificate(meteorInstitutionId, providerType, ldapTemplate);
		} catch (Exception e) {
			LOG.error("Exception occurred while contacting LDAP Registry. Trying failover registry", e);
			try {
				return getCertificate(meteorInstitutionId, providerType, ldapFailoverTemplate);
			} catch (Exception e1) {
				LOG.error("Exception occurred while contacting LDAP failover registry", e1);
				throw e1 instanceof RegistryException ? (RegistryException) e1 : new RegistryException(e1);
			}
		}

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

	@Override
	public List<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException {
		try {
			return getIndexProviders(meteorVersion, ldapTemplate);
		} catch (Exception e) {
			LOG.error("Exception occurred while contacting LDAP Registry. Trying failover registry", e);
			try {
				return getIndexProviders(meteorVersion, ldapFailoverTemplate);
			} catch (Exception e1) {
				LOG.error("Exception occurred while contacting LDAP failover registry", e1);
				throw e1 instanceof RegistryException ? (RegistryException) e1 : new RegistryException(e1);
			}
		}
	}

	private List<IndexProvider> getIndexProviders(Version meteorVersion, LdapTemplate ldapTemplate) throws RegistryException {
		List<IndexProvider> indexProviders = new ArrayList<IndexProvider>();

		class LdapIndexProvider {
			String preferredTransport;
			String[] supportedVersions;
		}

		// TODO: get index provider failover url from LDAP registry. Can we get
		// the meteor institution identifier too?

		@SuppressWarnings("unchecked")
		List<LdapIndexProvider> ldapIPs = (List<LdapIndexProvider>) ldapTemplate.search("", INDEX_PROVIDER_SEARCH_CRITERIA, SearchControls.SUBTREE_SCOPE, new AttributesMapper() {

			public LdapIndexProvider mapFromAttributes(Attributes attrs) throws NamingException {
				LdapIndexProvider ip = new LdapIndexProvider();
				ip.preferredTransport = (String) attrs.get("PreferredTransport").get();

				Attribute versionAttr = attrs.get("Ver");
				ip.supportedVersions = new String[versionAttr.size()];
				for (int i = 0; i < versionAttr.size(); ++i) {
					ip.supportedVersions[i] = (String) versionAttr.get(i);
				}
				return ip;
			}
		});

		for (LdapIndexProvider ldapIP : ldapIPs) {
			boolean versionSupported = false;
			for (String version : ldapIP.supportedVersions) {
				if (meteorVersion.matches(version)) {
					versionSupported = true;
					break;
				}
			}
			if (!versionSupported) {
				continue;
			}

			try {
				ldapIP.preferredTransport = stripBaseDn(ldapIP.preferredTransport);
			} catch (InvalidNameException e1) {
				throw new RegistryException(e1);
			}

			IndexProvider indexProvider = new IndexProvider();
			indexProvider.setUrl((String) ldapTemplate.lookup(ldapIP.preferredTransport, new AttributesMapper() {
				public Object mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get("URL").get();
				}
			}));
			
			indexProviders.add(indexProvider);
		}

		return indexProviders;
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