package com.unical.digitalsignature;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import com.google.common.io.Files;
import com.unical.utils.PAdESProp;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureRotation;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.x509.CertificateToken;

public class PAdESSignFactory extends AbstractSignFactory {

	private PAdESProp prop;

	public PAdESSignFactory(PAdESProp padesProp, File inputFile) {
		super(inputFile);
		prop = padesProp;
	}

	public AbstractSignatureParameters createParameter(DSSPrivateKeyEntry signer) {
		PAdESSignatureParameters parameters = new PAdESSignatureParameters();

		// We choose the level of the signature (-B, -T, -LT, -LTA).
		parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
		// We set the digest algorithm to use with the signature algorithm.
		// You must use the same parameter when you invoke the method sign on the token.
		// The default value is SHA256
		parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
		// We set the signing certificate
		parameters.setSigningCertificate(signer.getCertificate());
		boolean useField = selectSignatureField(parameters);
		if (prop.useVisibleSign()) {
			CertificateToken ct = signer.getCertificate();
			String humanReadableSigner = DSSASN1Utils.getHumanReadableName(ct);
			humanReadableSigner = humanReadableSigner + "\n" + getDateAndTimeString();
			parameters.setSignatureImageParameters(addVisibleSignatureParameters(humanReadableSigner, useField));
		}

		return parameters;
	}

	private String getDateAndTimeString() {
		LocalDateTime ldt = LocalDateTime.now(Clock.systemUTC());
		LocalTime localTime = ldt.toLocalTime().truncatedTo(ChronoUnit.SECONDS);
		LocalDate localDate = ldt.toLocalDate();
		String s = localDate.toString() + " " + localTime.toString() + " UTC";
		return s;

	}

	private SignatureImageParameters addVisibleSignatureParameters(String humanReadableSigner, boolean useField) {
		// Initialize visual signature
		SignatureImageParameters imageParameters = new SignatureImageParameters();
		imageParameters.setRotation(VisualSignatureRotation.AUTOMATIC);

		if (useField == false) {
			imageParameters.setPage(prop.getPage());
			// the origin is the left and top corner of the page
			// System.out.println(prop.toString());
			imageParameters.setAlignmentHorizontal(prop.getPosHorizontal());
			imageParameters.setAlignmentVertical(prop.getPosVertical());
			// imageParameters.setxAxis(0);
			// imageParameters.setyAxis(0);
		}

		if (prop.getImageFile() != null) {
			if (prop.getImageFile().exists()) {
				// TODO: check if the file is a png,jpg ecc
				imageParameters.setImage(new FileDocument(prop.getImageFile()));
			} else {
				System.err.println("Image not exist. Sign only with text.\n");
			}
		}

		// Initialize text to generate for visual signature
		SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
		textParameters.setFont(new Font("serif", Font.PLAIN, 13));
		textParameters.setTextColor(Color.BLACK);
		textParameters.setText(humanReadableSigner);
		imageParameters.setTextParameters(textParameters);

		return imageParameters;
	}

	private boolean selectSignatureField(PAdESSignatureParameters parameters) {
		PDDocument doc;
		try {
			doc = PDDocument.load(inputFile);
			PDDocumentCatalog pdCatalog = doc.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
			if (pdAcroForm != null) {
				PDPageTree allPages = doc.getDocumentCatalog().getPages();
				// get all fields
				List<PDField> fields = pdAcroForm.getFields();
				// System.out.println("Total Fields number:" + fields.size() + "\n");
				List<PDField> fields_empty = new ArrayList<PDField>();
				int c = 0;
				for (PDField pdField : fields) {
					// show only empty fields
					if (pdField.getValueAsString().isEmpty()) {
						fields_empty.add(pdField);
						String fieldName = pdField.getFullyQualifiedName();
						PDPage currentPage = pdField.getWidgets().get(0).getPage();
						int pageNumber = allPages.indexOf(currentPage) + 1;
						System.out.println("[" + c + "] - page:" + pageNumber + " - " + fieldName);
						c++;
					}
				}
				if (fields_empty.size() != 0) {
					System.out.println("Select Field to use (-1 or Enter for skip):");
					int n = -1;
					String readLine = System.console().readLine();
					if (isInteger(readLine))
						n = Integer.parseInt(readLine);
					while (n >= fields_empty.size() || n < -1) { // if n is out of bound, read again
						System.out.println("Input Not Valid");
						System.out.println("Select Field to use (-1 or Enter for skip):");
						readLine = System.console().readLine();
						if (isInteger(readLine))
							n = Integer.parseInt(readLine);
						if (readLine.isEmpty())
							n = -1;
					}
					if (n != -1)
						parameters.setSignatureFieldId(fields_empty.get(n).getFullyQualifiedName());
					else {
						System.out.println("No Field selected.");
						return false;
					}
					return true;
				} else {
					System.out.println("No available field in the pdf.");
					return false;
				}
			}
			return false;

		} catch (IOException e) {
			System.err.println("Error to read input");

		}
		return false;

	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AbstractSignatureService createService() {
		// Create common certificate verifier
		CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
		// Create PAdESService for signature
		PAdESService service = new PAdESService(commonCertificateVerifier);
		return service;
	}

	@Override
	public void createSignedFile(DSSDocument signedDocument) {
		String newfilename = Files.getNameWithoutExtension(inputFile.getName()) + "-signed.pdf";
		int c = 1;
		while (new File(newfilename).exists()) {
			newfilename = Files.getNameWithoutExtension(inputFile.getName()) + "-signed(" + c + ").pdf";
			c++;
		}

		String dir = inputFile.getParent();
		if (dir == null)
			dir = ".";

		writeFile(dir, newfilename, signedDocument);
	}

}
