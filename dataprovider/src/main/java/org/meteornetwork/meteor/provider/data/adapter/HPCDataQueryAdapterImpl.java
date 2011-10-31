package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.meteornetwork.meteor.common.hpc.HPCManager;
import org.meteornetwork.meteor.common.hpc.HPCMessageParams;
import org.meteornetwork.meteor.common.hpc.HPCSecurityManager;
import org.meteornetwork.meteor.common.security.RequestInfo;
import org.meteornetwork.meteor.common.util.TemplateVersionMapper;
import org.meteornetwork.meteor.common.util.XSLTransformManager;
import org.meteornetwork.meteor.common.util.exception.MeteorSecurityException;
import org.meteornetwork.meteor.common.util.message.MeteorMessage;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
import org.meteornetwork.meteor.provider.data.MeteorDataResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Adapts Meteor 3.3.4 HPC request to Meteor 4.0 data provider
 * 
 * @author jlazos
 * 
 */
@Component("HPCDataQueryAdapterImpl")
@Scope("prototype")
public class HPCDataQueryAdapterImpl implements DataQueryAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(HPCDataQueryAdapterImpl.class);

	private String rawHPCMessage;
	private String responseHPCMessage;

	private transient String meteorVersion;
	private transient String requesterId;

	private TemplateVersionMapper requestTemplateVersionMapper;
	private TemplateVersionMapper responseTemplateVersionMapper;

	private HPCManager hpcManager;
	private HPCSecurityManager hpcSecurityManager;
	private XSLTransformManager xslTransformManager;

	private Properties meteorProps;

	private RequestInfo requestInfo;

	@Override
	public RequestWrapper getRequest() throws DataQueryAdapterException {
		String contentXml;
		try {
			contentXml = hpcManager.retrieveHPCContent(rawHPCMessage);
			LOG.debug("Request XML from HPC:\n" + contentXml);
		} catch (Exception e) {
			LOG.debug("Could not handle HPC request", e);
			throw new DataQueryAdapterException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE);
		}

		try {
			hpcSecurityManager.validateRequest(contentXml);
		} catch (MeteorSecurityException e1) {
			LOG.debug("Could not validate HPC request", e1);
			throw new DataQueryAdapterException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE);
		}

		try {
			requestInfo.setSecurityToken(hpcSecurityManager.getSecurityToken(contentXml));
		} catch (MeteorSecurityException e) {
			LOG.debug("Could not parse SecurityToken from HPC request", e);
			throw new DataQueryAdapterException(MeteorMessage.SECURITY_INVALID_TOKEN);
		}

		String transformedContentXml;
		try {
			meteorVersion = xslTransformManager.getMeteorVersion(contentXml);
			LOG.debug("Transforming request XML from meteor version " + meteorVersion + " to " + meteorProps.getProperty("meteor.version"));
			transformedContentXml = xslTransformManager.transformXML(contentXml, requestTemplateVersionMapper.getTemplateForVersions(meteorVersion, meteorProps.getProperty("meteor.version")));
			LOG.debug("Transformed request XML:\n" + transformedContentXml);
		} catch (Exception e) {
			LOG.error("Could not transform request XML", e);
			return null;
		}

		RequestWrapper request = new RequestWrapper();
		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(transformedContentXml));
		} catch (Exception e) {
			LOG.error("Could not parse meteor data request", e);
			throw new DataQueryAdapterException(MeteorMessage.ACCESS_INVALID_MESSAGE_SIGNATURE);
		}

		requesterId = meteorDataRequest.getAccessProvider().getMeteorInstitutionIdentifier();
		request.setAccessProvider(meteorDataRequest.getAccessProvider());
		request.setSsn(meteorDataRequest.getSSN());

		return request;
	}

	@Override
	public void setResponse(MeteorDataResponseWrapper response) {
		if (response == null) {
			responseHPCMessage = null;
			return;
		}

		String marshalledResponse;
		try {
			StringWriter marshalledResponseWriter = new StringWriter();
			response.getResponse().marshal(marshalledResponseWriter);
			marshalledResponse = marshalledResponseWriter.toString();
			LOG.debug("Marshalled response XML:\n" + marshalledResponse);
		} catch (Exception e) {
			LOG.error("Could not marshal meteor response", e);
			return;
		}

		String transformedResponseXml;
		try {
			LOG.debug("Transforming response XML from meteor version " + meteorProps.getProperty("meteor.version") + " to " + meteorVersion);
			transformedResponseXml = xslTransformManager.transformXML(marshalledResponse, responseTemplateVersionMapper.getTemplateForVersions(meteorProps.getProperty("meteor.version"), meteorVersion));
			LOG.debug("Transformed response XML:\n" + transformedResponseXml);
		} catch (Exception e) {
			LOG.error("Could not transform response XML", e);
			return;
		}

		HPCMessageParams messageParams = new HPCMessageParams();
		messageParams.setRecipientId(requesterId);

		try {
			responseHPCMessage = hpcManager.generateHPCMessage(transformedResponseXml, messageParams);
		} catch (Exception e) {
			LOG.error("Could not generate HPC response", e);
			return;
		}
	}

	public String getRawHPCMessage() {
		return rawHPCMessage;
	}

	/**
	 * @param rawHPCMessage
	 *            The raw HPC message to decode, decompress, and translate into
	 *            RequestWrapper.
	 */
	public void setRawHPCMessage(String rawHPCMessage) {
		this.rawHPCMessage = rawHPCMessage;
	}

	/**
	 * @return Response data converted to compressed and encoded HPC response
	 */
	public String getResponseHPCMessage() {
		return responseHPCMessage;
	}

	public void setResponseHPCMessage(String responseHPCMessage) {
		this.responseHPCMessage = responseHPCMessage;
	}

	public HPCManager getHpcManager() {
		return hpcManager;
	}

	@Autowired
	public void setHpcManager(HPCManager hpcManager) {
		this.hpcManager = hpcManager;
	}

	public HPCSecurityManager getHpcSecurityManager() {
		return hpcSecurityManager;
	}

	@Autowired
	public void setHpcSecurityManager(HPCSecurityManager hpcSecurityManager) {
		this.hpcSecurityManager = hpcSecurityManager;
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

	public Properties getMeteorProps() {
		return meteorProps;
	}

	@Autowired
	@Qualifier("MeteorProperties")
	public void setMeteorProps(Properties meteorProps) {
		this.meteorProps = meteorProps;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	@Autowired
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

}
