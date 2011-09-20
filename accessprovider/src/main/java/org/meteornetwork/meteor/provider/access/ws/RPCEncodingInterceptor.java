package org.meteornetwork.meteor.provider.access.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Converts HPC soap request to rpc encoding by hand. RPC Encoding is deprecated
 * and not supported by JAX-WS, or any web services framework that implememts
 * the WS-I basic profile
 * 
 * @author jlazos
 * 
 */
public class RPCEncodingInterceptor extends AbstractPhaseInterceptor<Message> {

	public RPCEncodingInterceptor() {
		super(Phase.PRE_STREAM);
		addBefore(StaxOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		OutputStream outputStream = message.getContent(OutputStream.class);
		CachedStream cachedStream = new CachedStream();
		message.setContent(OutputStream.class, cachedStream);

		message.getInterceptorChain().add(new RPCEncodingEndingInterceptor(outputStream, cachedStream));
	}

	public class RPCEncodingEndingInterceptor extends AbstractPhaseInterceptor<Message> {

		private OutputStream originalOutStream;
		private CachedOutputStream cachedStream;

		public RPCEncodingEndingInterceptor(OutputStream originalOutStream, CachedOutputStream cachedStream) {
			super(RPCEncodingEndingInterceptor.class.getName(), Phase.PRE_STREAM_ENDING);
			this.originalOutStream = originalOutStream;
			this.cachedStream = cachedStream;
		}

		@Override
		public void handleMessage(Message message) throws Fault {
			try {
				cachedStream.flush();
				CachedOutputStream cachedOutStream = (CachedOutputStream) message.getContent(OutputStream.class);

				InputStream inputStream = cachedOutStream.getInputStream();
				String soapMessage = IOUtils.toString(inputStream);
				inputStream.close();

				String transformedMessage = convertToRPCEncoding(soapMessage);
				originalOutStream.write(transformedMessage.getBytes(IOUtils.UTF8_CHARSET));

				cachedStream.close();
				originalOutStream.flush();

				message.setContent(OutputStream.class, originalOutStream);
			} catch (Exception e) {
				throw new Fault(e);
			}
		}

	}

	private String convertToRPCEncoding(String soapMessage) throws ParserConfigurationException, SAXException, IOException, TransformerException {
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

	private static String domToString(Node dom) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		XMLUtils.outputDOM(dom, outputStream);
		String xml = outputStream.toString();
		outputStream.close();
		return xml;
	}

	private class CachedStream extends CachedOutputStream {
		public CachedStream() {
			super();
		}

		protected void doFlush() throws IOException {
			currentStream.flush();
		}

		protected void doClose() throws IOException {
		}

		protected void onWrite() throws IOException {
		}
	}
}
