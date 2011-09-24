package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.cxf.helpers.IOUtils;

public class TestUtils {

	private TestUtils() {
	}

	public static Date zeroTime(Date date) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date parseDate(String dateString, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
		return dateFormat.parse(dateString);
	}

	@SuppressWarnings("rawtypes")
	public static String getTestFile(Class thisClass, String fileName) throws IOException {
		File hpcRequestFile = new File(thisClass.getResource(fileName).getFile());
		InputStream hpcRequestStream = new FileInputStream(hpcRequestFile);
		return IOUtils.toString(hpcRequestStream, IOUtils.UTF8_CHARSET.displayName());
	}
}
