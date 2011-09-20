package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.cxf.helpers.IOUtils;
import org.apache.xpath.XPathAPI;
import org.meteornetwork.meteor.common.util.AbstractXMLTransformInterceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts HPC soap request to rpc encoding by hand. RPC Encoding is deprecated
 * and not supported by JAX-WS, or any web services framework that implememts
 * the WS-I basic profile
 * 
 * @author jlazos
 * 
 */
public class HPCRequestRPCEncodingInterceptor extends AbstractXMLTransformInterceptor {

	@Override
	protected String transformMessage(String soapMessage) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();

		Document doc = docBuilder.parse(new ByteArrayInputStream(soapMessage.getBytes(IOUtils.UTF8_CHARSET)));
		Element root = doc.getDocumentElement();
		root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

		Element rawHPCMessage = (Element) XPathAPI.selectSingleNode(root, "//rawHPCMessage");
		rawHPCMessage.setAttribute("xsi:type", "xsd:string");

		return domToString(doc);
	}

}
