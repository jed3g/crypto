package com.jed3g.crypto;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Scanner;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.bouncycastle.crypto.CryptoException;

/**
 * Goal which decrypt source files.
 *
 */
@Mojo(name = "decrypt", defaultPhase = LifecyclePhase.VALIDATE)
public class Decrypt extends AbstractEncrypt {
	
	public void execute() throws MojoExecutionException {

		if (encryptedDirectory.exists()) {
			if (passwordFile.exists()) {
				String password = "";
				
				try (Scanner scanner = new Scanner(passwordFile)) {
					if (scanner.hasNext()){
						password = scanner.nextLine();
					}
				} catch (IOException e) {
					getLog().error("Error reading password File: " + passwordFile.getAbsolutePath(), e);
				}
				
				if(password.trim().isEmpty()) {
					getLog().warn("Password File: " + passwordFile.getAbsolutePath() + " empty and no password found!");
				}
				else {
					encryptor = new Encryptor(password);
					
					Path inputDirectoryPath = encryptedDirectory.toPath();
					Path outputDirectoryPath;
					if (decryptedDirectory.exists()) {
						outputDirectoryPath = Paths.get(decryptedDirectory.getName() + "2");
						if(outputDirectoryPath.toFile().exists()) {
							removeDirectory(outputDirectoryPath.toFile());
						}
					}
					else {
						outputDirectoryPath = decryptedDirectory.toPath();
					}
					outputDirectoryPath.toFile().mkdirs();
					getLog().info("**** " + inputDirectoryPath + " decrypted into " + outputDirectoryPath);
					try {
						Files.walkFileTree(inputDirectoryPath, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
								new SimpleFileVisitor<Path>() {
									@Override
									public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
											throws IOException {
										Path targetdir = outputDirectoryPath.resolve(inputDirectoryPath.relativize(dir));
										try {
											Files.copy(dir, targetdir);
										} catch (FileAlreadyExistsException e) {
											if (!Files.isDirectory(targetdir))
												throw e;
										}
										return FileVisitResult.CONTINUE;
									}

									@Override
									public FileVisitResult visitFile(Path inFile, BasicFileAttributes attrs)
											throws IOException {
										//Files.copy(file, outputDirectoryPath.resolve(inputDirectoryPath.relativize(file)));
										byte[] decryptedData;
										try {
											decryptedData = encryptor.decrypt(Files.readAllBytes(inFile));
										} catch (CryptoException e) {
											decryptedData = new byte[0];
											getLog().error("Error encoding source File: " + inFile, e);
										}
										
										Files.write(outputDirectoryPath.resolve(inputDirectoryPath.relativize(inFile)), decryptedData);
										
										return FileVisitResult.CONTINUE;
									}
								});
					} catch (IOException e) {
						throw new MojoExecutionException("Error encoding source files!", e);
					} finally {
					}
				}
			}
			else {
				getLog().warn("Password File: " + passwordFile.getAbsolutePath() + " does not exist!");
			}
		}
		else {
			getLog().warn("Encrypted Directory: " + encryptedDirectory.getAbsolutePath() + " does not exist!");
		}
	}
	
}
