package org.meteornetwork.meteor.common.util;

import java.util.StringTokenizer;

public class Version {
	
	public enum Sequence {
		MAJOR, MINOR, RELEASE
	}

	private Integer major;
	private Integer minor;
	private Integer release;

	public Version() {
		major = 0;
		minor = 0;
		release = 0;
	}

	/**
	 * @param version
	 *            accepts version string in the form of "0.0.0". Allows
	 *            shorthand versions.
	 * 
	 *            Examples: "1", "1.2", "1.2.2"
	 */
	public Version(String version) {
		setVersion(version);
	}

	public Version(Version version) {
		this.major = version.major;
		this.minor = version.minor;
		this.release = version.release;
	}

	public void setVersion(String version) {
		major = 0;
		minor = 0;
		release = 0;

		StringBuilder reverse = new StringBuilder(version).reverse();
		StringTokenizer tokenizer = new StringTokenizer(reverse.toString(), ".");
		switch (tokenizer.countTokens()) {
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
		default:
		}
	}

	/**
	 * Creates a Version object with more specific version sequences set to 0.
	 * Useful for comparing versions based on higher level sequences (e.g. just
	 * compare major and minor version numbers, ignoring release numbers).
	 * 
	 * Example: 4.2.3 diluteVersion(Version.Sequence.MINOR) becomes 4.2.0
	 * 
	 * @param granularity
	 *            the sequence level to dilute the version to
	 * @return this version with sequences more specific than the granularity
	 *         set to 0
	 */
	public Version diluteVersion(Sequence granularity) {
		Version diluted = new Version(this);

		switch (granularity) {
		case MAJOR:
			diluted.setVersion(Sequence.MINOR, 0);
		case MINOR:
			diluted.setVersion(Sequence.RELEASE, 0);
		default:
		}

		return diluted;
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
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(major).append(".").append(minor).append(".").append(release);
		return strBuilder.toString();
	}

	/**
	 * Determines if version matches the specified pattern
	 * 
	 * @param pattern
	 *            version pattern to match. Use 'x' as a wildcard. Must specify
	 *            all version sequences - shorthand versions are not allowed
	 *            (e.g. "6.x" is not allowed, but "6.x.x" is)
	 * 
	 *            Example: "3.x.x" matches a version with a major number of 3
	 *            and any minor and release number
	 * @return
	 */
	public boolean matches(String pattern) {
		StringTokenizer tokenizer = new StringTokenizer(pattern, ".");

		String token = tokenizer.nextToken();
		if (!match(token, major)) {
			return false;
		}

		token = tokenizer.nextToken();
		if (!match(token, minor)) {
			return false;
		}

		token = tokenizer.nextToken();
		if (!match(token, release)) {
			return false;
		}

		return true;
	}

	private boolean match(String token, Integer versionNumber) {
		return token.equalsIgnoreCase("x") || Integer.valueOf(token).equals(versionNumber);
	}

}
