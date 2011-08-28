package org.meteornetwork.meteor.provider.access.service.adapter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.hpc.HPCManager;
import org.meteornetwork.meteor.common.hpc.HPCMessageParams;
import org.meteornetwork.meteor.common.util.TemplateVersionMapper;
import org.meteornetwork.meteor.common.util.XSLTransformManager;
import org.meteornetwork.meteor.common.ws.DataProviderHPCService;
import org.meteornetwork.meteor.common.xml.datarequest.AccessProvider;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorRsMsg;
import org.meteornetwork.meteor.common.xml.indexresponse.DataProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("hpcDataQueryAdapterImpl")
@Scope("prototype")
public class HPCDataQueryAdapterImpl implements DataQueryAdapter, ApplicationContextAware {

	private static final Log LOG = LogFactory.getLog(HPCDataQueryAdapterImpl.class);

	private DataProvider dataProvider;
	private AccessProvider accessProvider;
	private String ssn;
	private String meteorVersion;

	private HPCManager hpcManager;

	private XSLTransformManager xslTransformManager;
	private TemplateVersionMapper requestTemplateVersionMapper;

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
		
		HPCMessageParams messageParams = new HPCMessageParams();
		messageParams.setRecipientId(dataProvider.getEntityID());

		String hpcRequest = hpcManager.generateHPCMessage(transformedRequest, messageParams);

		JaxWsProxyFactoryBean hpcDataClientProxyFactory = (JaxWsProxyFactoryBean) applicationContext.getBean("hpcDataClientProxyFactory");
		hpcDataClientProxyFactory.setAddress(dataProvider.getEntityURL());

		DataProviderHPCService dataService = (DataProviderHPCService) hpcDataClientProxyFactory.create();
		String responseXml = dataService.submitHPC(hpcRequest);
		String unwrappedHpc = hpcManager.retrieveHPCContent(responseXml);

		MeteorRsMsg response = MeteorRsMsg.unmarshal(new StringReader(unwrappedHpc));
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
	public void setRequestTemplateVersionMapper(TemplateVersionMapper requestTemplateVersionMapper) {
		this.requestTemplateVersionMapper = requestTemplateVersionMapper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
