package org.meteornetwork.meteor.common.util;

import org.apache.commons.logging.Log;

public class LoggingUtil {

	private LoggingUtil() {
	}

	public static void logError(String message, Exception exception, Log log) {
		log.error(message + ": " + exception.getMessage());
		log.debug(message, exception);
	}
}
