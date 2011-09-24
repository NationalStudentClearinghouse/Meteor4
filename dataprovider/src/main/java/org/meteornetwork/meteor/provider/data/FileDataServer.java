package org.meteornetwork.meteor.provider.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.meteornetwork.meteor.common.xml.dataresponse.MeteorDataProviderInfo;

/**
 * This sample data provider reads the directory specified by the property
 * 'filedataserver.directory' in the file 'dataprovider.properties'. It then
 * looks in that directory for files with the format specified in
 * 'filedataserver.format'
 * 
 * This class will return the first file it finds that matches these criteria.
 * 
 * @since Apr 3, 2003
 */
public class FileDataServer implements DataServerAbstraction {

	private static transient final Log log = LogFactory.getLog(FileDataServer.class);

	private ResourceBundle dataProviderProperties;

	public FileDataServer() {
		dataProviderProperties = ResourceBundle.getBundle("dataprovider");
	}

	@Override
	public MeteorDataResponseWrapper getData(MeteorContext context, String ssn) {
		log.warn("Warning, you are using the class " + this.getClass().getName() + ".  This was designed for testing purposes and is not suitable for production use.");

		log.debug("Current directory is " + new File(".").getAbsolutePath());
		MeteorDataResponseWrapper returnResp = new MeteorDataResponseWrapper();

		String format = dataProviderProperties.getString("filedataserver.format");
		String dirName = dataProviderProperties.getString("filedataserver.directory");

		File dir = new File(dirName);

		if (!dir.exists()) {
			log.error("The value '" + dirName + "' does not exist");
			return returnResp;
		}

		if (!dir.isDirectory()) {
			log.error("The value '" + dirName + "' is not a directory so I can't read files from there");
			return returnResp;
		}

		File[] files = dir.listFiles(new MeteorFileFilter(format, ssn));

		String xml = null;

		for (int i = 0; i < files.length; i++) {
			byte[] bytes;

			String name = files[i].getAbsolutePath();
			try {
				log.debug("Reading file: " + name);
				bytes = readFile(name);
			} catch (FileNotFoundException ex) {
				log.error("File '" + files[i].getName() + "' not found: " + ex.getMessage());
				return returnResp;
			}
			xml = new String(bytes);
			if (xml != null && xml.length() > 0) {
				try {
					MeteorDataResponseWrapper tmpResp = new MeteorDataResponseWrapper(xml);
					MeteorDataProviderInfo[] mdpi = tmpResp.getResponse().getMeteorDataProviderInfo();

					log.info("File " + files[i].getName() + " has " + mdpi.length + " MDPI " + (mdpi.length == 1 ? "section" : "sections"));
					for (int j = 0; j < mdpi.length; j++) {
						log.debug("Adding a MeteorDataProviderInfo object");
						returnResp.getResponse().addMeteorDataProviderInfo(mdpi[j]);
					}
				} catch (ValidationException ex) {
					log.error("Validation Exception: " + ex.getMessage());
				} catch (MarshalException e) {
					log.error("Marshal Exception: " + e.getMessage());
				}
			}
		}

		return returnResp;
	}

	private byte[] readFile(String filename) throws FileNotFoundException {

		log.debug("Reading File: " + filename);

		File file = new File(filename);
		int filelength = (int) file.length();
		FileInputStream in = null;
		// StringBuffer contents = new StringBuffer(filelength);
		byte[] buffer = null;

		log.debug("File length: " + filelength);

		try {
			in = new FileInputStream(filename);

			buffer = new byte[filelength];

			int bytesRead = in.read(buffer);

			log.debug("Bytes read: " + bytesRead);
			// contents.append(buffer);

			in.close();

		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (IOException e) {
			System.out.println("I/O Exception: " + e.getMessage());
			return null;
		} catch (OutOfMemoryError e) {
			log.error("Out of memory reading file: " + filename);
			return null;
		}

		log.debug("Finshed reading file: " + filename);
		return buffer;
	}

	private class MeteorFileFilter implements FileFilter {
		private String format;

		public MeteorFileFilter(String fmt, String ssn) {
			this.format = fmt;

			int index = this.format.indexOf("{");
			if (index >= 0) {
				String substrLength = format.substring(index + 1, index + 2);
				ssn = ssn.substring(0, Integer.parseInt(substrLength));
			}

			index = this.format.indexOf("%");
			String tmpBefore = this.format.substring(0, index);

			int index2 = this.format.indexOf("}");

			String newFormat = tmpBefore + ssn;

			if (index2 >= 0) {
				newFormat += this.format.substring(index2 + 1);
			} else {
				newFormat += this.format.substring(index + 1);
			}

			this.format = newFormat.toLowerCase();

			log.debug("Looking for files that match '" + this.format + "'");

		}

		public boolean accept(File pathname) {
			String name = pathname.getName();

			if (name == null) {
				return false;
			}

			name = name.toLowerCase();

			if (name.indexOf(format) == -1) {
				return false;
			}

			if (name.endsWith(".xml")) {
				return true;
			}

			return false;
		}
	}
}
