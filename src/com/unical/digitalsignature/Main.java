package com.unical.digitalsignature;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.beust.jcommander.ParameterException;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.unical.utils.ArgsParser;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;

public class Main {
	private static String separator = System.getProperty("file.separator");
	private static File driverPathWin = new File(buildFilePath("driver", "Windows", "bit4xpki.dll"));
	private static File driverPathLinux32 = new File(buildFilePath("driver", "Linux", "32", "libbit4xpki.so"));
	private static File driverPathLinux64 = new File(buildFilePath("driver", "Linux", "64", "libbit4xpki.so"));

	private static File currentDriverPath = null;

	public static void main(String[] args) {
		ArgsParser cmdr = new ArgsParser();
		try {
			cmdr.parseArgs(args);
		} catch (ParameterException e) {
			System.err.println("Missing Parameter");
			return;
			// e.printStackTrace();
		}
		if (cmdr.isHelp()) {
			cmdr.showHelp();
			return;
		}
		if (cmdr.getDriver() != null)
			setDriver(cmdr.getDriver());
		else
			useDefaultDriver();

		File inputFile = cmdr.getFileToSign();

		if (inputFile == null) {
			System.err.println("No File input");
			return;
		}

		if (!checkFile(inputFile))
			return;

		 padesSign(inputFile);

	}

	private static void padesSign(File inputFile) {
		System.out.println("Start Signature Procedure");
		char[] pass = getPassword();
		
		Pkcs11SignatureToken token = SignService.connectToToken(currentDriverPath, pass);
		List<DSSPrivateKeyEntry> keys;
		try {
			keys = token.getKeys();
		} catch (DSSException e) {
			System.err.println("Token access failed.");
//			e.printStackTrace();
			return;
		}
		DSSPrivateKeyEntry signer = SignService.getSigner(keys);
		// Preparing parameters for the PAdES signature
		PAdESSignatureParameters parameters = SignService.setPAdESParameter(signer);
		PAdESService service = SignService.createPAdESService();
		DSSDocument toSignDocument = new FileDocument(inputFile);
		// Get the SignedInfo segment that need to be signed.
		ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
		// This function obtains the signature value for signed information using the
		// private key and specified algorithm
		// NOTA: You must use the same algorithm selected in PAdES Parameters
		DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
		SignatureValue signatureValue = token.sign(dataToSign, digestAlgorithm, signer);
		// We invoke the padesService to sign the document with the signature value
		// obtained the previous step.
		DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
		System.out.println("Creation of signed file");
		SignService.createSignedPDF(signedDocument, inputFile);
		System.out.println("END");

	}

	private static char[] getPassword() {
		Console cnsl = null;

		try {
			// creates a console object
			cnsl = System.console();
			// if console is not null
			if (cnsl != null) {
				char[] pwd = cnsl.readPassword("Password: ");
				return pwd;
			}
		} catch (Exception ex) {

			// if any error occurs
			ex.printStackTrace();
		}
		return null;
	}

	private static boolean checkFile(File inputFile) {
		if (!inputFile.exists()) {
			System.err.println("File not exist.");
			return false;
		}
		if (!Files.getFileExtension(inputFile.getName()).equals("pdf")) {
			System.err.println("File is not a pdf.");
			return false;
		}
		return true;
	}

	private static void setDriver(File file) {
		if (file.exists()) {
			currentDriverPath = file;
			System.out.println("Use driver located in: " + currentDriverPath);
		} else {
			useDefaultDriver();
		}
	}

	private static void useDefaultDriver() {
		extractDrivers();
		String os = System.getProperty("os.name").toLowerCase();
		String arch = checkOSArchitecture();
		if (os.contains("win")) {
			currentDriverPath = driverPathWin;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			if (arch.equals("64"))
				currentDriverPath = driverPathLinux64;
			else
				currentDriverPath = driverPathLinux32;
		} else if (os.contains("mac")) {
			// TODO ??
		}
		System.out.println("Use the default driver located in: " + currentDriverPath);

	}

	private static String checkOSArchitecture() {
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
		String realArch = null;
		if (arch != null && arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64"))
			realArch = "64";
		else
			realArch = "32";
		return realArch;
	}

	private static void extractDrivers() {
		try {
			if (!driverPathWin.exists()) {
				// load resources
				URL win = Resources.getResource("resources/driver/Windows/bit4xpki.dll");
				
				// create folder
				File wf = new File(buildFilePath("driver", "Windows"));
				if (!wf.exists())
					wf.mkdirs();
				
				// extract resources
				byte[] bytes = Resources.toByteArray(win);
				Files.write(bytes, driverPathWin);
			}
			if (!driverPathLinux32.exists()) {
				URL linux32 = Resources.getResource("resources/driver/Linux/32/libbit4xpki.so");
				File lf = new File(buildFilePath("driver", "Linux", "32"));
				if (!lf.exists())
					lf.mkdirs();
				byte[] bytes = Resources.toByteArray(linux32);
				Files.write(bytes, driverPathLinux32);
			}
			if (!driverPathLinux64.exists()) {
				URL linux64 = Resources.getResource("resources/driver/Linux/64/libbit4xpki.so");
				File lf64 = new File(buildFilePath("driver", "Linux", "64"));
				if (!lf64.exists())
					lf64.mkdirs();
				byte[] bytes = Resources.toByteArray(linux64);
				Files.write(bytes, driverPathLinux64);
			}
		} catch (IOException e) {
			System.err.println("Error in default driver extractaction");
			e.printStackTrace();
		}
	}

	private static String buildFilePath(String... strings) {
		String path = "";
		for (int i = 0; i < strings.length; i++) {
			path = path + strings[i];
			if (i != strings.length - 1)
				path = path + separator;
		}
		return path;
	}

}
