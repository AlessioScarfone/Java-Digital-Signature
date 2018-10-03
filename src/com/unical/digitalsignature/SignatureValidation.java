package com.unical.digitalsignature;

import java.io.File;
import java.io.IOException;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.SimpleReport;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;

public class SignatureValidation {

	public boolean validation(File f) {

		// First, we need a Certificate verifier
		CertificateVerifier cv = new CommonCertificateVerifier();
		// We can inject several sources. eg: OCSP, CRL, AIA, trusted lists
		// Capability to download resources from AIA
		cv.setDataLoader(new CommonsDataLoader());

		TSLRepository tslRepository = new TSLRepository();
		TrustedListsCertificateSource certificateSource = new TrustedListsCertificateSource();
		tslRepository.setTrustedListsCertificateSource(certificateSource);

		TSLValidationJob job = new TSLValidationJob();
		job.setCheckLOTLSignature(true);
		job.setCheckTSLSignatures(true);
		job.setLotlUrl("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml");
		job.setLotlCode("EU");

		// This information is needed to be able to filter the LOTL pivots
		job.setLotlRootSchemeInfoUri("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl.html");
		// The keystore contains certificates referenced in the Official Journal Link
		// (OJ URL)
		KeyStoreCertificateSource keyStoreCertificateSource;
		try {
			keyStoreCertificateSource = new KeyStoreCertificateSource(new File("src/main/resources/keystore.p12"),
					"PKCS12", "dss-password");
			job.setOjContentKeyStore(keyStoreCertificateSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		job.setOjUrl("http://eur-lex.europa.eu/legalcontent/EN/TXT/?uri=uriserv:OJ.C_.2016.233.01.0001.01.ENG");

		job.setRepository(tslRepository);
		job.refresh();

		cv.setTrustedCertSource(certificateSource);

		// Here is the document to be validated (any kind of signature file)
		DSSDocument document = new FileDocument(f);
		// We create an instance of DocumentValidator
		// It will automatically select the supported validator from the classpath
		SignedDocumentValidator documentValidator = SignedDocumentValidator.fromDocument(document);

		Reports reports = documentValidator.validateDocument();

		// The simple report is a summary of the detailed report (more user-friendly)
		SimpleReport simpleReport = reports.getSimpleReport();
		
		System.out.println(simpleReport.toString());

		return false;
	}

}
