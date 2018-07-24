package com.unical.digitalsignature;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.beust.jcommander.ParameterException;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.unical.utils.ArgsParser;
import com.unical.utils.Utility;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.x509.CertificateToken;

public class Main {

	private enum SystemType {
		WINDOWS, LINUX, MAC
	}

	private static File driverPathWin = new File(Utility.buildFilePath("driver", "Windows", "bit4xpki.dll"));
	private static File driverPathLinux32 = new File(Utility.buildFilePath("driver", "Linux", "32", "libbit4xpki.so"));
	private static File driverPathLinux64 = new File(Utility.buildFilePath("driver", "Linux", "64", "libbit4xpki.so"));

	private static File currentDriverPath = null;
	
	private static SignFormat selectedSignFormat = SignFormat.PADES;

	public static void main(String[] args) {

		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		// TODO hide warning also for dss.

		ArgsParser cmdr = new ArgsParser();
		try {
			cmdr.parseArgs(args);
		} catch (ParameterException e) {
			System.err.println("Missing Parameter");
			return;
		}
		// Show help
		if (cmdr.isHelp()) {
			cmdr.showHelp();
			return;
		}

		// use custom or default driver
		if (cmdr.getDriver() != null) {
			if (setDriver(cmdr.getDriver()) == false) {
				System.err.println("Error setting driver");
				return;
			}
		} else
			useDefaultDriver();

		// show certificates info and key usage
		if (cmdr.showCertInfo() || cmdr.showKeyUsage()) {
			showInfo(cmdr.showCertInfo(),cmdr.showKeyUsage());
			return;
		}

		// check selected sign format
		if (!checkSelectedSignFormat(cmdr)) {
			System.err.println("PAdES and CAdES are mutually exclusive. Select only one please.");
			return;
		}

		File inputFile = cmdr.getFileToSign();

		if (inputFile == null) {
			System.err.println("No File input");
			return;
		}
		// check file to sign format
		if (!checkFile(inputFile))
			return;

		sign(inputFile, cmdr.getUseVisibleSignature());

	}

	private static boolean checkSelectedSignFormat(ArgsParser cmdr) {
		if (cmdr.isCAdES() && cmdr.isPAdES())
			return false;
		else if (cmdr.isCAdES()) {
			selectedSignFormat = SignFormat.CADES;
		} else if (cmdr.isPAdES()) {
			selectedSignFormat = SignFormat.PADES;
		} else {
			selectedSignFormat = SignFormat.CADES;
			System.out.println("Sign Format not provided. Use default format");
		}

		System.out.println("Selected Sign Format: " + selectedSignFormat.toString());
		return true;
	}

	private static void showInfo(boolean info, boolean keyusage) {
		char[] pass = Utility.getPassword();
		AbstractSignFactory factory = new CAdESSignFactory();
		Pkcs11SignatureToken token = factory.connectToToken(currentDriverPath, pass);
		List<DSSPrivateKeyEntry> keys;
		try {
			keys = token.getKeys();
			int count = 0;
			for (DSSPrivateKeyEntry dssPrivateKeyEntry : keys) {
				CertificateToken ct= dssPrivateKeyEntry.getCertificate();
				System.out.println(DSSASN1Utils.getHumanReadableName(ct));
				
				System.out.println("Certificate:" + count);
				if (info == true) {
					System.out.println("Info:");
					factory.showCertificateData(ct);
					System.out.println();
				}
				if (keyusage = true) {
					System.out.println("Key Usage:");
					factory.showKeyUsage(ct);
					System.out.println();
				}
				System.out.println("---------");
				count++;
			}
		} catch (DSSException e) {
			System.err.println("Token access failed.");
			// e.printStackTrace();
			return;
		}

	}

	private static void sign(File inputFile, boolean useVisibleSignature) {
		System.out.println("Start Signature Procedure");
		char[] pass = Utility.getPassword();

		System.out.println();

		AbstractSignFactory factory = null;
		if (selectedSignFormat == SignFormat.CADES) {
			factory = new CAdESSignFactory();
		} else if (selectedSignFormat == SignFormat.PADES) {
			factory = new PAdESSignFactory(useVisibleSignature);
		}

		Pkcs11SignatureToken token = factory.connectToToken(currentDriverPath, pass);
		List<DSSPrivateKeyEntry> keys;
		try {
			keys = token.getKeys();
		} catch (DSSException e) {
			System.err.println("Token access failed.");
			// e.printStackTrace();
			return;
		}
		DSSPrivateKeyEntry signer = factory.getSigner(keys);
		// Preparing parameters for the PAdES signature
		AbstractSignatureParameters parameters = factory.setParameter(signer);
		AbstractSignatureService service = factory.createService();
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
		factory.createSignedFile(signedDocument, inputFile);
		System.out.println("END");

	}

	private static boolean checkFile(File inputFile) {
		if (!inputFile.exists()) {
			System.err.println("File not exist.");
			return false;
		}
		if (selectedSignFormat == SignFormat.PADES && !Files.getFileExtension(inputFile.getName()).equals("pdf")) {
			System.err.println("File is not a pdf.");
			return false;
		}
		return true;
	}

	private static boolean setDriver(File file) {
		if (file.exists()) {
			currentDriverPath = file;
			System.out.println("Use driver located in: " + currentDriverPath);
			return true;
		} else {
			return useDefaultDriver();
		}
	}

	private static boolean useDefaultDriver() {
		SystemType s = null;
		String os = System.getProperty("os.name").toLowerCase();
		String arch = Utility.checkOSArchitecture();
		if (os.contains("win")) {
			currentDriverPath = driverPathWin;
			s = SystemType.WINDOWS;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			s = SystemType.LINUX;
			if (arch.equals("64"))
				currentDriverPath = driverPathLinux64;
			else
				currentDriverPath = driverPathLinux32;
		} else if (os.contains("mac")) {
			// TODO ??
		}
		// extract only needed driver
		if (!extractDrivers(s))
			return false;
		System.out.println("Use the default driver located in: " + currentDriverPath);
		return true;

	}

	private static boolean extractDrivers(SystemType systype) {
		try {
			if (systype == SystemType.WINDOWS && !driverPathWin.exists()) {
				// load resources
				URL win = Resources.getResource("resources/driver/Windows/bit4xpki.dll");

				// create folder
				File wf = new File(Utility.buildFilePath("driver", "Windows"));
				if (!wf.exists())
					wf.mkdirs();

				// extract resources
				byte[] bytes = Resources.toByteArray(win);
				Files.write(bytes, driverPathWin);
			}
			if (systype == SystemType.LINUX && !driverPathLinux32.exists()) {
				URL linux32 = Resources.getResource("resources/driver/Linux/32/libbit4xpki.so");
				File lf = new File(Utility.buildFilePath("driver", "Linux", "32"));
				if (!lf.exists())
					lf.mkdirs();
				byte[] bytes = Resources.toByteArray(linux32);
				Files.write(bytes, driverPathLinux32);
			}
			if (systype == SystemType.LINUX && !driverPathLinux64.exists()) {
				URL linux64 = Resources.getResource("resources/driver/Linux/64/libbit4xpki.so");
				File lf64 = new File(Utility.buildFilePath("driver", "Linux", "64"));
				if (!lf64.exists())
					lf64.mkdirs();
				byte[] bytes = Resources.toByteArray(linux64);
				Files.write(bytes, driverPathLinux64);
			}

			// TODO add MAC driver
		} catch (IOException e) {
			System.err.println("Error in default driver extractaction");
			return false;
			// e.printStackTrace();
		}
		return true;
	}

}
