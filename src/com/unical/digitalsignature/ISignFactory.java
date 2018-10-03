/*Copyright 2018 Alessio Scarfone
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.unical.digitalsignature;

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.signature.AbstractSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.x509.CertificateToken;

public interface ISignFactory {

	public Pkcs11SignatureToken connectToToken(File driverPath, char[] pass);

	public DSSPrivateKeyEntry getSigner(List<DSSPrivateKeyEntry> keys,boolean choose_cert);
	
	public void showKeyUsage(CertificateToken ct);

	public void showCertificateData(CertificateToken ct);

	public AbstractSignatureParameters createParameter(DSSPrivateKeyEntry signer);

	public AbstractSignatureService<?> createService();

	public void createSignedFile(DSSDocument signedDocument);


}
