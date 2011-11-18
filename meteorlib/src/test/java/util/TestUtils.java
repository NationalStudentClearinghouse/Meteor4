/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.cxf.helpers.IOUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;

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
	public static String getFileString(Class thisClass, String fileName) throws IOException {
		File file = new File(thisClass.getResource(fileName).getFile());
		InputStream fileStream = new FileInputStream(file);
		try {
			return IOUtils.toString(fileStream, IOUtils.UTF8_CHARSET.displayName());
		} finally {
			fileStream.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static void printDifferences(String testMethod, Diff xmlDiff) {
		DetailedDiff detail = new DetailedDiff(xmlDiff);
		System.out.println(testMethod + ": Differences found: ");
		for (Difference diff : (List<Difference>) detail.getAllDifferences()) {
			if (diff.isRecoverable()) {
				continue;
			}
			System.out.println("\t" + diff.getDescription() + " ... expected: " + diff.getControlNodeDetail().getValue() + ", actual: " + diff.getTestNodeDetail().getValue());
		}
	}
}
