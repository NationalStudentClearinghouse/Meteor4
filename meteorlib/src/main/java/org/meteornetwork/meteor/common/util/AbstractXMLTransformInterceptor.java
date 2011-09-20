package org.meteornetwork.meteor.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Node;

/**
 * Concrete implementations of this class can manipulate raw outgoing CXF
 * messages by implementing transformMessage() and placing them in an endpoint's
 * interceptor stack
 * 
 * @author jlazos
 * 
 */
public abstract class AbstractXMLTransformInterceptor extends AbstractPhaseInterceptor<Message> {
	public AbstractXMLTransformInterceptor() {
		super(Phase.PRE_STREAM);
		addBefore(StaxOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		OutputStream outputStream = message.getContent(OutputStream.class);
		CachedStream cachedStream = new CachedStream();
		message.setContent(OutputStream.class, cachedStream);

		message.getInterceptorChain().add(new XMLTransformEndingInterceptor(outputStream, cachedStream));
	}

	public class XMLTransformEndingInterceptor extends AbstractPhaseInterceptor<Message> {

		private OutputStream originalOutStream;
		private CachedOutputStream cachedStream;

		public XMLTransformEndingInterceptor(OutputStream originalOutStream, CachedOutputStream cachedStream) {
			super(XMLTransformEndingInterceptor.class.getName(), Phase.PRE_STREAM_ENDING);
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

				String transformedMessage = transformMessage(soapMessage);
				originalOutStream.write(transformedMessage.getBytes(IOUtils.UTF8_CHARSET));

				cachedStream.close();
				originalOutStream.flush();

				message.setContent(OutputStream.class, originalOutStream);
			} catch (Exception e) {
				throw new Fault(e);
			}
		}

	}

	protected abstract String transformMessage(String soapMessage) throws Exception;

	protected String domToString(Node dom) throws IOException {
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
