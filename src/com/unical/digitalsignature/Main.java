/*  
 *  Java-Digital-Signature
 *  Java command line tool for digital signature with PKCS#11 token.
 *  Copyright (C) 2018,2019  Alessio Scarfone
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	 This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	 GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.unical.digitalsignature;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import com.beust.jcommander.ParameterException;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.unical.argparser.ArgsParser;
import com.unical.utils.PAdESProp;
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
	private static String resource_DriverPathWindows = "resources/driver/Windows/bit4xpki.dll";

	// TODO: currently they are not present in GitHub folder - Test on linux
	private static File driverPathLinux32 = new File(Utility.buildFilePath("driver", "Linux", "32", "libbit4xpki.so"));
	private static String resource_DriverPathLinux32 = "resources/driver/Linux/32/libbit4xpki.so";
	private static File driverPathLinux64 = new File(Utility.buildFilePath("driver", "Linux", "64", "libbit4xpki.so"));
	private static String resource_DriverPathLinux64 = "resources/driver/Linux/64/libbit4xpki.so";

	private static File currentDriverPath = null;

	private static SignFormat selectedSignFormat = SignFormat.PADES;

	public static void main(String[] args) {
		// hide warning for external library.
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, Level.OFF.toString());

		ArgsParser cmdr = ArgsParser.getInstance();
		try {
			cmdr.parseArgs(args);
		} catch (ParameterException | NullPointerException e) {
			System.err.println("Parameter Error.");
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
			showInfo(cmdr.showCertInfo(), cmdr.showKeyUsage());
			return;
		}

		// check selected sign format
		if (!checkSelectedSignFormat()) {
			// if there is no command shows help
			cmdr.showDefaultHelp();
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

		sign(inputFile);

	}

	private static boolean checkSelectedSignFormat() {
		ArgsParser cmdr = ArgsParser.getInstance();
		selectedSignFormat = cmdr.checkSelectedSignFormat();
		if (selectedSignFormat == null)
			return false;
		System.out.println("Selected Signature Format: " + selectedSignFormat.toString());
		return true;
	}

	private static void showInfo(boolean info, boolean keyusage) {
		char[] pass = getPassword();
		AbstractSignatureFactory factory = new CAdESSignatureFactory(null); // no file is needed
		Pkcs11SignatureToken token = factory.connectToToken(currentDriverPath, pass);
		List<DSSPrivateKeyEntry> keys;
		try {
			keys = token.getKeys();
			int count = 0;
			for (DSSPrivateKeyEntry dssPrivateKeyEntry : keys) {
				CertificateToken ct = dssPrivateKeyEntry.getCertificate();
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

	private static char[] getPassword() {
		ArgsParser cmdr = ArgsParser.getInstance();
		if (cmdr.getPassword() == null)
			return Utility.readPasswordFromConsole();
		else
			return cmdr.getPassword().toCharArray();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void sign(File inputFile) {
		ArgsParser cmdr = ArgsParser.getInstance();
		System.out.println("Start Signature Procedure");
		char[] pass = getPassword();

		System.out.println();

		AbstractSignatureFactory factory = null;
		if (selectedSignFormat == SignFormat.CADES) {
			factory = new CAdESSignatureFactory(inputFile);
		} else if (selectedSignFormat == SignFormat.PADES) {
			PAdESProp padesProp = cmdr.createPAdESProp();
			if (padesProp == null) {
				System.err.println("Error create PAdES Prop");
				return;
			}
			factory = new PAdESSignatureFactory(padesProp, inputFile);
		}

		Pkcs11SignatureToken token = factory.connectToToken(currentDriverPath, pass);
		List<DSSPrivateKeyEntry> keys;
		try {
			keys = token.getKeys();
		} catch (DSSException e) {
			System.err.println("Token access failed");
			// e.printStackTrace();
			return;
		}

		DSSPrivateKeyEntry signer = factory.getSigner(keys, cmdr.isChoice_certificate());

		if (signer == null) {
			System.err.println("Signature not performed");
			return;
		}

		System.out.print("Certificate to use:  ");
		CertificateToken ct = signer.getCertificate();
		String humanReadableSigner = DSSASN1Utils.getHumanReadableName(ct);
		System.out.println(humanReadableSigner);

		// Preparing parameters for the PAdES signature
		AbstractSignatureParameters parameters = factory.createParameter(signer);
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
		System.out.println("Start of signing process...");
		DSSDocument signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
		factory.createSignedFile(signedDocument);
		System.out.println("End of signing process.");

	}

	private static boolean checkFile(File inputFile) {
		if (!inputFile.exists()) {
			System.err.println("File not exist.");
			return false;
		}

//		if (selectedSignFormat == SignFormat.PADES && !Files.getFileExtension(inputFile.getName()).equals("pdf")) {
		if (selectedSignFormat == SignFormat.PADES && !isPDF(inputFile)) {
			System.err.println("File is not a pdf.");
			return false;
		}
		return true;
	}

	private static boolean isPDF(File inputFile) {
		byte[] fileContent;
		try {
			fileContent = Files.toByteArray(inputFile);
			if (fileContent != null && fileContent.length > 4 && fileContent[0] == 0x25 && // %
					fileContent[1] == 0x50 && // P
					fileContent[2] == 0x44 && // D
					fileContent[3] == 0x46 && // F
					fileContent[4] == 0x2d) { // -
				return true;
			}
		} catch (IOException e) {
			System.err.println("Unable to check if the file is a pdf.");
//			e.printStackTrace();
		}
		return false;
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
				URL win = Resources.getResource(resource_DriverPathWindows);

				// create folder
				File wf = new File(Utility.buildFilePath("driver", "Windows"));
				if (!wf.exists())
					wf.mkdirs();

				// extract resources
				byte[] bytes = Resources.toByteArray(win);
				Files.write(bytes, driverPathWin);
			}
			if (systype == SystemType.LINUX && !driverPathLinux32.exists()) {
				URL linux32 = Resources.getResource(resource_DriverPathLinux32);
				File lf = new File(Utility.buildFilePath("driver", "Linux", "32"));
				if (!lf.exists())
					lf.mkdirs();
				byte[] bytes = Resources.toByteArray(linux32);
				Files.write(bytes, driverPathLinux32);
			}
			if (systype == SystemType.LINUX && !driverPathLinux64.exists()) {
				URL linux64 = Resources.getResource(resource_DriverPathLinux64);
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
