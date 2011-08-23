package org.meteornetwork.meteor.provider.data.adapter;

import java.io.StringReader;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meteornetwork.meteor.common.AccessProvider;
import org.meteornetwork.meteor.common.hpc.HPCManager;
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
public class Version334AdapterImpl implements TranslationAdapter {

	private static final Log LOG = LogFactory.getLog(Version334AdapterImpl.class);

	private Exception processingException;

	private String rawHPCMessage;
	private String responseHPCMessage;

	private Templates requestTransformerTemplate;

	private HPCManager hpcManager;
	private XSLTransformManager xslTransformManager;

	@Override
	public RequestWrapper getRequest() {
		String contentXml;
		try {
			contentXml = hpcManager.retrieveContent(hpcManager.parseHPCEnvelope(rawHPCMessage));
			LOG.debug("HPC content XML:\n" + contentXml);
		} catch (Exception e) {
			// TODO: how does HPC log bad requests? Exceptions at this stage?
			LOG.error("Could not handle HPC request: " + e.getMessage());
			LOG.debug("Could not handle HPC request", e);
			processingException = e;
			return null;
		}

		String transformedContentXml;
		try {
			transformedContentXml = xslTransformManager.transformXML(contentXml, requestTransformerTemplate);
			LOG.debug("Transformed content XML:\n" + transformedContentXml);
		} catch (TransformerException e) {
			LOG.error("Could not transform HPC content: " + e.getMessage());
			LOG.debug("Could not transform HPC content", e);
			processingException = e;
			return null;
		}

		RequestWrapper request = new RequestWrapper();
		MeteorDataRequest meteorDataRequest;
		try {
			meteorDataRequest = MeteorDataRequest.unmarshal(new StringReader(transformedContentXml));
		} catch (Exception e) {
			LOG.error("Could not parse meteor data request: " + e.getMessage());
			LOG.debug("Could not parse meteor data request", e);
			processingException = e;
			return null;
		}

		request.setAccessProvider(new AccessProvider(meteorDataRequest.getAccessProvider()));
		request.setSsn(meteorDataRequest.getSSN());
		return request;
	}

	@Override
	public void setResponse(ResponseWrapper response) {
		// TODO set meteor data response
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

	public Templates getRequestTransformerTemplate() {
		return requestTransformerTemplate;
	}

	@Autowired
	@Qualifier("AP334toDP400RequestTemplate")
	public void setRequestTransformerTemplate(Templates requestTransformerTemplate) {
		this.requestTransformerTemplate = requestTransformerTemplate;
	}

	@Override
	public Exception getProcessingException() {
		return processingException;
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

}
