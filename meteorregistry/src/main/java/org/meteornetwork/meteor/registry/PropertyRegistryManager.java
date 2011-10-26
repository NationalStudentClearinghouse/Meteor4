package org.meteornetwork.meteor.registry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.registry.RegistryException;
import org.meteornetwork.meteor.common.registry.RegistryManager;
import org.meteornetwork.meteor.common.registry.data.DataProvider;
import org.meteornetwork.meteor.common.registry.data.IndexProvider;
import org.meteornetwork.meteor.common.util.Version;
import org.meteornetwork.meteor.saml.ProviderType;
import org.meteornetwork.meteor.saml.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PropertyRegistryManager implements RegistryManager {

	private static final Logger LOG = LoggerFactory.getLogger(PropertyRegistryManager.class);

	private Properties directoryProperties;
	private ResourceBundle directoryDataProperties;

	@Override
	public X509Certificate getCertificate(String meteorInstitutionId, ProviderType providerType) throws RegistryException {

		String certFileKey = meteorInstitutionId + "." + providerType.getType() + ".Certificate";
		if (!directoryDataProperties.containsKey(certFileKey)) {
			LOG.error("Could not find certifiate file in properties file for institution" + meteorInstitutionId);
			throw new RegistryException("Could not find certificate is registry for " + meteorInstitutionId);
		}

		String certFile = directoryDataProperties.getString(certFileKey);
		X509Certificate cert = null;
		try {
			InputStream inStream = new FileInputStream(certFile);
			try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				cert = (X509Certificate) cf.generateCertificate(inStream);
			} finally {
				inStream.close();
			}
		} catch (Exception e) {
			LOG.error("Unable to load x509 certificate from file: " + certFile);
			throw new RegistryException(e);
		}

		return cert;
	}

	@Override
	public List<IndexProvider> getIndexProviders(Version meteorVersion) throws RegistryException {
		try {
			List<IndexProvider> indexProviders = new ArrayList<IndexProvider>();

			Set<String> indexProviderIds = new HashSet<String>();
			for (String propertyName : directoryDataProperties.keySet()) {
				if (ProviderType.INDEX.getType().equals(directoryDataProperties.getString(propertyName))) {
					StringTokenizer tokenizer = new StringTokenizer(propertyName, ".");
					indexProviderIds.add(tokenizer.nextToken());
				}
			}

			for (String indexProviderId : indexProviderIds) {
				for (String supportedVersion : getSupportedVersions(indexProviderId)) {
					if (meteorVersion.matches(supportedVersion)) {
						indexProviders.add(createRegistryIndexProvider(indexProviderId));
					}
				}
			}

			return indexProviders;

		} catch (Exception e) {
			throw new RegistryException(e);
		}
	}

	private IndexProvider createRegistryIndexProvider(String indexProviderId) {
		IndexProvider indexProvider = new IndexProvider();
		indexProvider.setInstitutionIdentifier(indexProviderId);
		indexProvider.setUrl(getUrl(indexProviderId, ProviderType.INDEX));
		return indexProvider;
	}

	@Override
	public DataProvider getDataProvider(String meteorInstitutionId) throws RegistryException {
		DataProvider dataProvider = new DataProvider();

		try {
			dataProvider.setInstitutionIdentifier(meteorInstitutionId);
			List<String> supportedVersions = getSupportedVersions(meteorInstitutionId);
			dataProvider.setDescription(getDescription(meteorInstitutionId));
			dataProvider.setMeteorVersion(supportedVersions.get(0));
			dataProvider.setUrl(getUrl(meteorInstitutionId, ProviderType.DATA));
		} catch (Exception e) {
			throw new RegistryException(e);
		}

		return dataProvider;
	}

	@Override
	public List<DataProvider> getAllDataProviders() throws RegistryException {
		Set<String> dataProviderIds = new HashSet<String>();
		for (String propertyName : directoryDataProperties.keySet()) {
			if (ProviderType.DATA.getType().equals(directoryDataProperties.getString(propertyName))) {
				StringTokenizer tokenizer = new StringTokenizer(propertyName, ".");
				dataProviderIds.add(tokenizer.nextToken());
			}
		}

		List<DataProvider> dataProviders = new ArrayList<DataProvider>();
		for (String dataProviderId : dataProviderIds) {
			dataProviders.add(getDataProvider(dataProviderId));
		}

		return dataProviders;
	}

	private String getUrl(String id, ProviderType providerType) {
		String thisKey = id + "." + providerType.getType() + ".URL";
		return directoryDataProperties.containsKey(thisKey) ? directoryDataProperties.getString(thisKey) : null;
	}
	
	private String getDescription(String id) {
		String thisKey = id + ".Description";
		return directoryDataProperties.containsKey(thisKey) ? directoryDataProperties.getString(thisKey) : null;
	}

	private List<String> getSupportedVersions(String id) {
		List<String> supportedVersions = new ArrayList<String>();

		String propertyKey = id + ".Version";

		if (directoryDataProperties.containsKey(propertyKey)) {
			supportedVersions.add(directoryDataProperties.getString(propertyKey));
			return supportedVersions;
		}

		for (int i = 1;; ++i) {
			String thisKey = propertyKey + "." + i;
			if (directoryDataProperties.containsKey(thisKey)) {
				supportedVersions.add(directoryDataProperties.getString(thisKey));
			} else {
				break;
			}
		}

		return supportedVersions;
	}

	@Override
	public List<String> getAliases(String meteorInstitutionId, ProviderType providerType) throws RegistryException {
		List<String> aliasesList = new ArrayList<String>();
		String aliases;
		try {
			aliases = directoryDataProperties.getString(meteorInstitutionId + "." + providerType.getType() + ".Aliases");
		} catch (MissingResourceException e) {
			throw new RegistryException(e);
		}

		if (aliases != null) {
			StringTokenizer tokenizer = new StringTokenizer(aliases, ",");
			while (tokenizer.hasMoreTokens()) {
				aliasesList.add(tokenizer.nextToken());
			}
		}

		return aliasesList;
	}

	@Override
	public Integer getAuthenticationLevel(String meteorInstitutionId, String authProcId, ProviderType providerType, Role role) throws RegistryException {
		try {
			String authLevel = directoryDataProperties.getString(meteorInstitutionId + "." + role.getName() + ".AuthenticationLevel");
			return (Integer.valueOf(authLevel));
		} catch (MissingResourceException e) {
			return null;
		}
	}

	@Override
	public List<Role> getRoles(String meteorInstitutionId, String authProcId, ProviderType providerType) throws RegistryException {
		List<Role> roles = new ArrayList<Role>();
		for (Role role : Role.values()) {
			if (getAuthenticationLevel(meteorInstitutionId, authProcId, providerType, role) != null) {
				roles.add(role);
			}
		}

		return roles;
	}

	public Properties getDirectoryProperties() {
		return directoryProperties;
	}

	@Autowired
	@Qualifier("DirectoryProperties")
	public void setDirectoryProperties(Properties directoryProperties) {
		this.directoryProperties = directoryProperties;
		directoryDataProperties = ResourceBundle.getBundle(this.directoryProperties.getProperty("directory.properties.directorydata"));
	}

}
