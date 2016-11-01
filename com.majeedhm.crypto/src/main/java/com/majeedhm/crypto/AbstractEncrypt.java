package com.majeedhm.crypto;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;


public abstract class AbstractEncrypt extends AbstractMojo {
	/**
	 * Location of the source files to be encrypted.
	 */
	@Parameter(defaultValue = "src", property = "decryptedDirectory")
	protected File decryptedDirectory;

	/**
	 * Location of the encrypted source files.
	 */
	@Parameter(defaultValue = "src_en", property = "encryptedDirectory")
	protected File encryptedDirectory;

	/**
	 * Location of the encrypted source files.
	 */
	@Parameter(defaultValue = "password.txt", property = "passwordFile")
	protected File passwordFile;

	protected Encryptor encryptor;
	
	
	public void removeDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					removeDirectory(aFile);
				}
			}
			dir.delete();
		} else {
			dir.delete();
		}
	}
}
