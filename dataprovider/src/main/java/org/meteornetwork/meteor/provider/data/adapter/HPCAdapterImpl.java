package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.hpc.HPCManager;
import org.meteornetwork.meteor.common.util.LoggingUtil;
import org.meteornetwork.meteor.common.util.TemplateVersionMapper;
import org.meteornetwork.meteor.common.util.XSLTransformManager;
import org.meteornetwork.meteor.common.xml.datarequest.MeteorDataRequest;
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
@Component
@Scope("prototype")
public class HPCAdapterImpl implements VersionAdapter {

	private static final Log LOG = LogFactory.getLog(HPCAdapterImpl.class);

	private String rawHPCMessage;
	private String responseHPCMessage;
	private transient String meteorVersion;

	private TemplateVersionMapper requestTemplateVersionMapper;
	private TemplateVersionMapper responseTemplateVersionMapper;

	private HPCManager hpcManager;
	private XSLTransformManager xslTransformManager;

	private Properties meteorProps;

	@Override
	public RequestWrapper getRequest() {
		String contentXml;
		try {
			contentXml = hpcManager.retrieveContent(rawHPCMessage);
			LOG.debug("Request XML from HPC:\n" + contentXml);
		} catch (Exception e) {
			LoggingUtil.logError("Could not handle HPC request", e, LOG);
			return null;
		}

		String transformedContentXml;
		try {
			meteorVersion = xslTransformManager.getMeteorVersion(contentXml);
			LOG.debug("Transforming request XML from meteor version " + meteorVersion + " to " + meteorProps.getProperty("meteor.version"));
			transformedContentXml = xslTransformManager.transformXML(contentXml, requestTemplateVersionMapper.getTemplateForVersions(meteorVersion, meteorProps.getProperty("meteor.version")));
			LOG.debug("Transformed request XML:\n" + transformedContentXml);
		} catch (Exception e) {
			LoggingUtil.logError("Could not transform request XML", e, LOG);
			return null;
		}

		RequestWrapper request = new RequestWrapper();
		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(transformedContentXml));
		} catch (Exception e) {
			LoggingUtil.logError("Could not parse meteor data request", e, LOG);
			return null;
		}

		request.setAccessProvider(meteorDataRequest.getAccessProvider());
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(ResponseWrapper response) {
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
			LoggingUtil.logError("Could not marshal meteor response", e, LOG);
			return;
		}

		String transformedResponseXml;
		try {
			LOG.debug("Transforming response XML from meteor version " + meteorProps.getProperty("meteor.version") + " to " + meteorVersion);
			transformedResponseXml = xslTransformManager.transformXML(marshalledResponse, responseTemplateVersionMapper.getTemplateForVersions(meteorProps.getProperty("meteor.version"), meteorVersion));
			LOG.debug("Transformed response XML:\n" + transformedResponseXml);
		} catch (Exception e) {
			LOG.error("Could not transform response XML: " + e.getMessage());
			LoggingUtil.logError("Could not transform response XML", e, LOG);
			return;
		}

		try {
			responseHPCMessage = hpcManager.generateHPCResponse(transformedResponseXml);
		} catch (Exception e) {
			LoggingUtil.logError("Could not generate HPC response", e, LOG);
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

}
