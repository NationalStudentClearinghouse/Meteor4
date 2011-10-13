package org.meteornetwork.meteor.common.registry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

public class LDAPRegistryManager implements RegistryManager {

	private static final Log LOG = LogFactory.getLog(LDAPRegistryManager.class);

	private static final String STATUS_ACTIVE = "AC";

	private static final String INDEX_PROVIDER_SEARCH_CRITERIA = "(&(File=" + ProviderType.INDEX.getType() + ")(objectClass=File))";
	// INDEX_PROVIDER_4_SEARCH_CRITERIA only needed when Meteor 3 is still
	// supported by the network
	private static final String INDEX_PROVIDER_4_SEARCH_CRITERIA = "(&(File=" + ProviderType.INDEX.getType() + "4)(objectClass=File))";
	private static final String DATA_PROVIDER_SEARCH_CRITERIA = "(&(File=" + ProviderType.DATA.getType() + ")(objectClass=File))";

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
				throw new RegistryException(e1);
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

		ByteArrayInputStream certInputStream = new ByteArrayInputStream(certificateBinary);
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

			return (X509Certificate) certificateFactory.generateCertificate(certInputStream);
		} catch (CertificateException e) {
			throw new RegistryException(e);
		} finally {
			try {
				certInputStream.close();
			} catch (IOException e) {
				throw new RegistryException(e);
			}
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
				throw new RegistryException(e1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<IndexProvider> getIndexProviders(Version meteorVersion, LdapTemplate ldapTemplate) {
		List<IndexProvider> indexProviders = new ArrayList<IndexProvider>();

		List<LdapIndexProvider> ldapIPs = (List<LdapIndexProvider>) ldapTemplate.search("", INDEX_PROVIDER_SEARCH_CRITERIA, SearchControls.SUBTREE_SCOPE, new IndexAttributesMapperImpl());
		try {
			ldapIPs.addAll((List<LdapIndexProvider>) ldapTemplate.search("", INDEX_PROVIDER_4_SEARCH_CRITERIA, SearchControls.SUBTREE_SCOPE, new IndexAttributesMapperImpl()));
		} catch (NullPointerException ex) {
			// empty catch block
		}

		if (ldapIPs == null) {
			return indexProviders;
		}

		for (LdapIndexProvider ldapIP : ldapIPs) {
			if (!STATUS_ACTIVE.equalsIgnoreCase(ldapIP.status)) {
				continue;
			}

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
				LOG.error("Invalid PreferredTransport name", e1);
				continue;
			}

			IndexProvider indexProvider = new IndexProvider();
			indexProvider.setInstitutionIdentifier(ldapIP.id);
			indexProvider.setUrl((String) ldapTemplate.lookup(ldapIP.preferredTransport, new AttributesMapper() {
				public Object mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get("URL").get();
				}
			}));

			indexProviders.add(indexProvider);
		}

		return indexProviders;
	}

	@Override
	public DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException {
		try {
			return getDataProvider(meteorInstitutionId, ldapTemplate);
		} catch (Exception e) {
			LOG.error("Exception occurred while contacting LDAP Registry. Trying failover registry", e);
			try {
				return getDataProvider(meteorInstitutionId, ldapFailoverTemplate);
			} catch (Exception e1) {
				LOG.error("Exception occurred while contacting LDAP failover registry", e1);
				throw new RegistryException(e1);
			}
		}
	}

	private DataProvider getDataProvider(String meteorInstitutionId, LdapTemplate ldapTemplate) throws InvalidNameException {
		LdapDataProvider ldapDP = (LdapDataProvider) ldapTemplate.lookup("File=" + ProviderType.DATA.getType() + ",FileTypeFamily=Meteor,Institution=" + meteorInstitutionId, new AttributesMapper() {
			public LdapDataProvider mapFromAttributes(Attributes attrs) throws NamingException {
				LdapDataProvider dp = new LdapDataProvider();
				dp.preferredTransport = (String) attrs.get("PreferredTransport").get();
				dp.version = (String) attrs.get("Ver").get();
				return dp;
			}
		});

		ldapDP.preferredTransport = stripBaseDn(ldapDP.preferredTransport);

		String dpUrl = (String) ldapTemplate.lookup(ldapDP.preferredTransport, new AttributesMapper() {
			public Object mapFromAttributes(Attributes attrs) throws NamingException {
				return attrs.get("URL").get();
			}
		});

		DataProvider dataProvider = new DataProvider();
		dataProvider.setInstitutionIdentifier(meteorInstitutionId);
		dataProvider.setMeteorVersion(ldapDP.version);
		dataProvider.setUrl(dpUrl);

		return dataProvider;
	}

	@Override
	public List<DataProvider> getAllDataProviders() throws RegistryException {
		try {
			return getAllDataProviders(ldapTemplate);
		} catch (Exception e) {
			LOG.error("Exception occurred while contacting LDAP Registry. Trying failover registry", e);
			try {
				return getAllDataProviders(ldapFailoverTemplate);
			} catch (Exception e1) {
				LOG.error("Exception occurred while contacting LDAP failover registry", e1);
				throw new RegistryException(e1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<DataProvider> getAllDataProviders(LdapTemplate ldapTemplate) {
		List<DataProvider> dataProviders = new ArrayList<DataProvider>();

		List<LdapDataProvider> ldapDPs;

		ldapDPs = (List<LdapDataProvider>) ldapTemplate.search("", DATA_PROVIDER_SEARCH_CRITERIA, new ContextMapper() {

			@Override
			public LdapDataProvider mapFromContext(Object ctx) {
				DirContextAdapter dirCtx = (DirContextAdapter) ctx;

				LdapDataProvider dp = new LdapDataProvider();
				try {
					dp.id = getIdFromDn(dirCtx.getDn());
				} catch (InvalidNameException e) {
					// cannot set id
				}
				dp.preferredTransport = dirCtx.getStringAttribute("PreferredTransport");
				dp.status = dirCtx.getStringAttribute("Status");
				dp.version = dirCtx.getStringAttribute("Ver");
				return dp;
			}
		});

		if (ldapDPs == null) {
			return dataProviders;
		}

		for (LdapDataProvider ldapDP : ldapDPs) {
			if (ldapDP.preferredTransport == null || ldapDP.status == null || ldapDP.version == null) {
				LOG.debug("Encountered incorrectly configured data provider in registry");

			}

			if (!STATUS_ACTIVE.equalsIgnoreCase(ldapDP.status)) {
				continue;
			}

			try {
				ldapDP.preferredTransport = stripBaseDn(ldapDP.preferredTransport);
			} catch (InvalidNameException e1) {
				LOG.debug("Invalid PreferredTransport name", e1);
				continue;
			}

			DataProvider dataProvider = new DataProvider();
			dataProvider.setInstitutionIdentifier(ldapDP.id);
			dataProvider.setMeteorVersion(ldapDP.version);
			dataProvider.setUrl((String) ldapTemplate.lookup(ldapDP.preferredTransport, new AttributesMapper() {
				public Object mapFromAttributes(Attributes attrs) throws NamingException {
					return attrs.get("URL").get();
				}
			}));

			dataProviders.add(dataProvider);
		}

		return dataProviders;
	}

	private String stripBaseDn(String ldapName) throws InvalidNameException {
		LdapName name = new LdapName(ldapName);
		name.remove(0);
		return name.toString();
	}

	private String getIdFromDn(Name dn) throws InvalidNameException {
		LdapName name = new LdapName(dn.toString());
		String institution = name.get(0);
		return institution.replaceFirst("Institution|institution", "").replaceFirst("=", "").trim();
	}

	private class LdapIndexProvider {
		String id;
		String status;
		String preferredTransport;
		String[] supportedVersions;
	}

	private class IndexAttributesMapperImpl implements ContextMapper {

		@Override
		public LdapIndexProvider mapFromContext(Object ctx) {
			/*
			 * TODO document how LDAP registry should allow multiple versions
			 * for Index Providers, indicating that the index provider supports
			 * each version specified. Allows wild cards
			 */
			LdapIndexProvider ip = new LdapIndexProvider();

			DirContextAdapter dirCtx = (DirContextAdapter) ctx;
			try {
				ip.id = getIdFromDn(dirCtx.getDn());
			} catch (InvalidNameException e) {
				// cannot set id
			}
			ip.preferredTransport = dirCtx.getStringAttribute("PreferredTransport");
			ip.status = dirCtx.getStringAttribute("Status");

			String[] versionAttr = dirCtx.getStringAttributes("Ver");
			if (versionAttr != null) {
				ip.supportedVersions = new String[versionAttr.length];
				for (int i = 0; i < versionAttr.length; ++i) {
					ip.supportedVersions[i] = versionAttr[i];
				}
			}
			return ip;
		}
	}

	private class LdapDataProvider {
		String id;
		String status;
		String preferredTransport;
		String version;
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