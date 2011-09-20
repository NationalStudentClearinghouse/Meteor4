package org.meteornetwork.meteor.common.util;

public class PrivateKeyParams {

	private String keystoreType;
	private String keystoreFile;
	private String keystorePass;
	private String privateKeyAlias;
	private String privateKeyPass;

	public String getKeystoreType() {
		return keystoreType;
	}

	public void setKeystoreType(String keystoreType) {
		this.keystoreType = keystoreType;
	}

	public String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getKeystorePass() {
		return keystorePass;
	}

	public void setKeystorePass(String keystorePass) {
		this.keystorePass = keystorePass;
	}

	public String getPrivateKeyAlias() {
		return privateKeyAlias;
	}

	public void setPrivateKeyAlias(String privateKeyAlias) {
		this.privateKeyAlias = privateKeyAlias;
	}

	public String getPrivateKeyPass() {
		return privateKeyPass;
	}

	public void setPrivateKeyPass(String privateKeyPass) {
		this.privateKeyPass = privateKeyPass;
	}
}
