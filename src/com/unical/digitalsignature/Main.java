package com.unical.digitalsignature;

import java.io.File;
import java.util.List;

import com.beust.jcommander.ParameterException;
import com.google.common.io.Files;
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

	private static File driverPath = null;

	public static void main(String[] args) {
		ArgsParser cmdr = new ArgsParser();;
		try {
			cmdr.parseArgs(args);
		} catch (ParameterException e) {
			System.err.println("Missing Parameter");
			return;
//			e.printStackTrace();
		}
		if (cmdr.isHelp()) {
			cmdr.showHelp();
			return;
		}
		if (cmdr.getDriver() != null)
			setDriver(cmdr.getDriver());
		else
			useDefaultDriver();
		String pass = cmdr.getPassword();
		File inputFile = cmdr.getFileToSign();

		if (!checkFile(inputFile))
			return;
		
		Pkcs11SignatureToken token = SignService.connectToToken(driverPath, pass);
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
		PAdESSignatureParameters parameters = SignService.setPAdESSParameter(signer);
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
		SignService.createSignedPDF(signedDocument,inputFile);
		System.out.println("END");
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
			driverPath = file;
			System.out.println("Use driver located in: " + driverPath);
		} else {
			useDefaultDriver();
		}
	}

	private static void useDefaultDriver() {
		String separator = System.getProperty("file.separator");
		String os = System.getProperty("os.name").toLowerCase();
		String arch = checkOSArchitecture();
		if (os.contains("win")) {
			driverPath = new File("driver" + separator + "Windows" + separator + "bit4xpki.dll");
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			String basePath = "driver" + separator + "Linux" + separator;
			if (arch.equals("64"))
				driverPath = new File(basePath + "64" + separator + "libbit4xpki.so");
			else
				driverPath = new File(basePath + "32" + separator + "libbit4xpki.dll");
		} else if (os.contains("mac")) {
			// TODO ??
		}
		System.out.println("Use the default driver located in: " + driverPath);

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

}
