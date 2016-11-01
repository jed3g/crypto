package com.majeedhm.crypto;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bouncycastle.crypto.CryptoException;
import org.junit.Test;

import com.majeedhm.crypto.Encryptor;

public class EncryptorTest {

	private Encryptor encryptor = new Encryptor("majeedhm");
	
	@Test
	public void testEncryption() {
		InputStream in = EncryptorTest.class.getResourceAsStream("/FileToEncrypt.txt");
		byte[] bytes;
		try {
			bytes = new byte[in.available()];
			in.read(bytes);

			try {
				byte[] encryptedData = encryptor.encrypt(bytes);
				Files.write(new File("FileToEncrypt_EN.txt").toPath(), encryptedData);
			} catch (CryptoException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void testDecryption() {
		byte[] bytes;
		try {
			bytes = Files.readAllBytes(new File("FileToEncrypt_EN.txt").toPath());

			try {
				byte[] decryptedData = encryptor.decrypt(bytes);
				Files.write(new File("FileToEncrypt_DE.txt").toPath(), decryptedData);
			} catch (CryptoException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
