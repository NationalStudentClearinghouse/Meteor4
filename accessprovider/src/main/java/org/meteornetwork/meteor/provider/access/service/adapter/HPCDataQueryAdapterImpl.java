package org.meteornetwork.meteor.provider.access.service.adapter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.hpc.HPCManager;
import org.meteornetwork.meteor.common.hpc.HPCMessageParams;
import org.meteornetwork.meteor.common.hpc.HPCSecurityManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.DigitalSignatureManager;
import org.meteornetwork.meteor.common.util.PrivateKeyParams;
import org.meteornetwork.meteor.common.util.TemplateVersionMapper;
import org.meteornetwork.meteor.common.util.XSLTransformManager;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component("hpcDataQueryAdapterImpl")
@Scope("prototype")
public class HPCDataQueryAdapterImpl implements DataQueryAdapter, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(HPCDataQueryAdapterImpl.class);

	private Properties authenticationProperties;

	private DataProvider dataProvider;
	private AccessProvider accessProvider;
	private String ssn;
	private String meteorVersion;

	private RequestInfo requestInfo;

	private HPCManager hpcManager;
	private HPCSecurityManager hpcSecurityManager;
	private DigitalSignatureManager keystoreManager;

	private XSLTransformManager xslTransformManager;
	private Templates saml1ToMeteorSAMLTemplate;
	private TemplateVersionMapper requestTemplateVersionMapper;
	private TemplateVersionMapper responseTemplateVersionMapper;

	private ApplicationContext applicationContext;

	@Override
	public MeteorRsMsg call() throws Exception {
		LOG.debug("Calling data provider (ID: " + dataProvider.getEntityID() + ", Version: " + dataProvider.getMeteorVersion());

		MeteorDataRequest request = createRequest();
		StringWriter marshalledRequest = new StringWriter();
		marshalRequest(request, marshalledRequest);
		LOG.debug("(Query data provider ID " + dataProvider.getEntityID() + ") Marshalled meteor request: " + marshalledRequest.toString());

		String transformedRequest = xslTransformManager.transformXML(marshalledRequest.toString(), requestTemplateVersionMapper.getTemplateForVersions(meteorVersion, dataProvider.getMeteorVersion()));
		LOG.debug("(Query data provider ID " + dataProvider.getEntityID() + ") Transformed data provider request: " + transformedRequest);

		transformedRequest = assertAndSign(transformedRequest);

		HPCMessageParams messageParams = new HPCMessageParams();
		messageParams.setRecipientId(dataProvider.getEntityID());

		String hpcRequest = hpcManager.generateHPCMessage(transformedRequest, messageParams);

		JaxWsProxyFactoryBean hpcDataClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("hpcDataClientProxyFactory");
		hpcDataClientProxyFactory.setAddress(dataProvider.getEntityURL());

		DataProviderHPCService dataService = (DataProviderHPCService) hpcDataClientProxyFactory.create();
		String responseXml = dataService.submitHPC(hpcRequest);
		String unwrappedHpc = hpcManager.retrieveHPCContent(responseXml);
		LOG.debug("(Query data provider ID " + dataProvider.getEntityID() + ") Data provider response: " + unwrappedHpc);

		String transformedResponse = xslTransformManager.transformXML(unwrappedHpc, responseTemplateVersionMapper.getTemplateForVersions(dataProvider.getMeteorVersion(), meteorVersion));
		LOG.debug("(Query data provider ID " + dataProvider.getEntityID() + ") Data provider response translated to Meteor 4.0: " + transformedResponse);
		
		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(transformedResponse));
		return response;
	}

	private MeteorDataRequest createRequest() {
		MeteorDataRequest request = new MeteorDataRequest();
		request.setAccessProvider(accessProvider);
		request.setSSN(ssn);
		return request;
	}

	private void marshalRequest(MeteorDataRequest request, StringWriter stringWriter) throws MarshalException, ValidationException, IOException {
		Marshaller marshaller = new Marshaller();
		marshaller.setWriter(stringWriter);
		marshaller.setSuppressNamespaces(true);
		marshaller.marshal(request);
	}

	private String assertAndSign(String request) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, IOException, MeteorSecurityException, ParserConfigurationException, SAXException, TransformerException {
		PrivateKeyParams privateKeyParams = new PrivateKeyParams();
		privateKeyParams.setKeystoreType(authenticationProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.type"));
		privateKeyParams.setKeystoreFile(authenticationProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.file"));
		privateKeyParams.setKeystorePass(authenticationProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.password"));
		privateKeyParams.setPrivateKeyAlias(authenticationProperties.getProperty("org.apache.ws.security.saml.issuer.key.name"));
		privateKeyParams.setPrivateKeyPass(authenticationProperties.getProperty("org.apache.ws.security.saml.issuer.key.password"));

		PrivateKey privateKey = keystoreManager.getPrivateKey(privateKeyParams);

		// TODO get authentication process ID from request info
		// TODO append X509Certificate to signatures? make optional?
		String saml = hpcSecurityManager.createSaml(accessProvider.getMeteorInstitutionIdentifier(), "1", requestInfo);
		saml = xslTransformManager.transformXML(saml, saml1ToMeteorSAMLTemplate);
		saml = hpcSecurityManager.signAssertion(saml, privateKey, null);
		return hpcSecurityManager.signBody(request, saml, privateKey, null);
	}

	public Properties getAuthenticationProperties() {
		return authenticationProperties;
	}

	@Autowired
	@Qualifier("AuthenticationProperties")
	public void setAuthenticationProperties(Properties authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	@Override
	public DataProvider getDataProvider() {
		return dataProvider;
	}

	@Override
	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	public String getSsn() {
		return ssn;
	}

	@Override
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	@Override
	public AccessProvider getAccessProvider() {
		return accessProvider;
	}

	@Override
	public void setAccessProvider(AccessProvider accessProvider) {
		this.accessProvider = accessProvider;
	}

	@Override
	public String getMeteorVersion() {
		return meteorVersion;
	}

	@Override
	public void setMeteorVersion(String meteorVersion) {
		this.meteorVersion = meteorVersion;
	}

	public HPCManager getHpcManager() {
		return hpcManager;
	}

	@Autowired
	public void setHpcManager(HPCManager hpcManager) {
		this.hpcManager = hpcManager;
	}

	public XSLTransformManager getXslTransformManager() {
		return xslTransformManager;
	}

	@Autowired
	public void setXslTransformManager(XSLTransformManager xslTransformManager) {
		this.xslTransformManager = xslTransformManager;
	}

	public TemplateVersionMapper getRequestTemplateVersionMapper() {
		return requestTemplateVersionMapper;
	}

	@Autowired
	@Qualifier("requestTemplateVersionMapper")
	public void setRequestTemplateVersionMapper(TemplateVersionMapper requestTemplateVersionMapper) {
		this.requestTemplateVersionMapper = requestTemplateVersionMapper;
	}

	public TemplateVersionMapper getResponseTemplateVersionMapper() {
		return responseTemplateVersionMapper;
	}

	@Autowired
	@Qualifier("responseTemplateVersionMapper")
	public void setResponseTemplateVersionMapper(TemplateVersionMapper responseTemplateVersionMapper) {
		this.responseTemplateVersionMapper = responseTemplateVersionMapper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public HPCSecurityManager getHpcSecurityManager() {
		return hpcSecurityManager;
	}

	@Autowired
	public void setHpcSecurityManager(HPCSecurityManager hpcSecurityManager) {
		this.hpcSecurityManager = hpcSecurityManager;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	@Autowired
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public DigitalSignatureManager getKeystoreManager() {
		return keystoreManager;
	}

	@Autowired
	public void setKeystoreManager(DigitalSignatureManager keystoreManager) {
		this.keystoreManager = keystoreManager;
	}

	public Templates getSaml1ToMeteorSAMLTemplate() {
		return saml1ToMeteorSAMLTemplate;
	}

	@Autowired
	@Qualifier("SAML1toMeteorSAMLTemplate")
	public void setSaml1ToMeteorSAMLTemplate(Templates saml1ToMeteorSAMLTemplate) {
		this.saml1ToMeteorSAMLTemplate = saml1ToMeteorSAMLTemplate;
	}

}
