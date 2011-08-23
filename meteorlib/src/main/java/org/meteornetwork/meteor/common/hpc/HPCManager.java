package org.meteornetwork.meteor.common.hpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.helpers.IOUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.xml.hpc.Envelope;

/**
 * Provides support for legacy HPC communication protocol
 * 
 * @author jlazos
 */
// TODO: fully support HPC (e.g. abstract encoding and decoding based on specified encoding/compression type)
public class HPCManager {

	private static final Log LOG = LogFactory.getLog(HPCManager.class);
	
	public Envelope parseHPCEnvelope(String rawHPCMessage) throws MarshalException, ValidationException {
		Envelope hpcEnvelope = Envelope.unmarshal(new StringReader(rawHPCMessage));
		return hpcEnvelope;
	}
	
	public String retrieveContent(Envelope hpcEnvelope) throws Base64Exception, IOException {
		return decompress(decode(hpcEnvelope.getPackage().getContent().getContent()));
	}
	
	private byte[] decode(String encodedContent) throws Base64Exception {
		return Base64Utility.decode(encodedContent);
	}

	private String encode(byte[] decodedContent) {
		return Base64Utility.encode(decodedContent);
	}

	private String decompress(byte[] compressedContent) throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(compressedContent);
		InflaterInputStream inflater = new InflaterInputStream(input);

		return IOUtils.toString(inflater, IOUtils.UTF8_CHARSET.displayName());
	}

	private byte[] compress(String decompressedContent) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DeflaterOutputStream deflater = new DeflaterOutputStream(output);

		deflater.write(decompressedContent.getBytes(IOUtils.UTF8_CHARSET));
		deflater.close();

		return output.toByteArray();
	}
}
