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
package org.meteornetwork.meteor.common.util;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Defines a four-sequence version number ("x.x.x.x")
 * 
 * @author jlazos
 */
public class Version implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5701725419322932038L;
	
	private static final String DELIMITER = ".";
	private static final String WILDCARD = "x";

	public enum Sequence {
		MAJOR, MINOR, RELEASE, MAINTENANCE
	}

	private Integer major;
	private Integer minor;
	private Integer release;
	private Integer maintenance;

	public Version() {
		major = 0;
		minor = 0;
		release = 0;
		maintenance = 0;
	}

	/**
	 * @param version
	 *            accepts version string in the form of "0.0.0.0". Allows
	 *            shorthand versions.
	 * 
	 *            Examples: "1", "1.2", "1.2.2", "3.4.5.6"
	 */
	public Version(String version) {
		setVersion(version);
	}

	/**
	 * copy constructor
	 * 
	 * @param version
	 */
	public Version(Version version) {
		this.major = version.major;
		this.minor = version.minor;
		this.release = version.release;
		this.maintenance = version.maintenance;
	}

	public void setVersion(String version) {
		major = 0;
		minor = 0;
		release = 0;
		maintenance = 0;

		StringBuilder reverse = new StringBuilder(version).reverse();
		StringTokenizer tokenizer = new StringTokenizer(reverse.toString(), DELIMITER);
		switch (tokenizer.countTokens()) {
		case 4:
			maintenance = Integer.valueOf(tokenizer.nextToken());
		case 3:
			release = Integer.valueOf(tokenizer.nextToken());
		case 2:
			minor = Integer.valueOf(tokenizer.nextToken());
		case 1:
			major = Integer.valueOf(tokenizer.nextToken());
		default:
		}
	}

	public void setVersion(Sequence sequence, Integer version) {
		switch (sequence) {
		case MAJOR:
			major = version;
			break;
		case MINOR:
			minor = version;
			break;
		case RELEASE:
			release = version;
			break;
		case MAINTENANCE:
			maintenance = version;
		default:
		}
	}

	/**
	 * @param sequence
	 *            the sequence to get the version number of
	 * @return the version number of the specified sequence
	 */
	public Integer getVersionSequence(Sequence sequence) {
		switch (sequence) {
		case MAJOR:
			return major;
		case MINOR:
			return minor;
		case RELEASE:
			return release;
		case MAINTENANCE:
			return maintenance;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(major).append(DELIMITER).append(minor).append(DELIMITER).append(release).append(DELIMITER).append(maintenance);
		return strBuilder.toString();
	}

	/**
	 * Determines if version matches the specified pattern
	 * 
	 * @param pattern
	 *            version pattern to match. Use 'x' as a wildcard. Short hand
	 *            versions assume other values are set to 'x'.
	 * 
	 *            Example: "3.x.x" matches a version with a major number of 3
	 *            and any minor, release, and maintenance number
	 * @return
	 */
	public boolean matches(String pattern) {
		StringTokenizer tokenizer = new StringTokenizer(pattern, DELIMITER);

		String token = tokenizer.nextToken();
		if (!match(token, major)) {
			return false;
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!match(token, minor)) {
				return false;
			}
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!match(token, release)) {
				return false;
			}
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!match(token, maintenance)) {
				return false;
			}
		}

		return true;
	}

	private boolean match(String token, Integer versionNumber) {
		return token.equalsIgnoreCase(WILDCARD) || Integer.valueOf(token).equals(versionNumber);
	}

	public Integer getMajor() {
		return major;
	}

	public void setMajor(Integer major) {
		this.major = major;
	}

	public Integer getMinor() {
		return minor;
	}

	public void setMinor(Integer minor) {
		this.minor = minor;
	}

	public Integer getRelease() {
		return release;
	}

	public void setRelease(Integer release) {
		this.release = release;
	}

	public Integer getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(Integer maintenance) {
		this.maintenance = maintenance;
	}

}
