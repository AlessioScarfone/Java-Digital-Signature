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


package com.unical.argparser;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;
import com.unical.digitalsignature.SignFormat;
import com.unical.utils.PAdESProp;

import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentHorizontal;
import eu.europa.esig.dss.pades.SignatureImageParameters.VisualSignatureAlignmentVertical;

public class ArgsParser {

	public final static String cadesCommandLabel = "cades";
	public final static String padesCommandLabel = "pades";

	private JCommander jCommander;

	// ------------------------
	// ---- CLI Parameters ----
	// ------------------------
	@Parameter(names = { "-h", "--help" }, description = "display help", help = true, order = 0)
	private boolean help = false;

	@Parameter(names = { "-u", "--key-usage" }, description = "show key usage", order = 2)
	private boolean showKeyUsage;

	@Parameter(names = { "-i", "--info-certificates" }, description = "show certificates info", order = 3)
	private boolean showCertInfo;

	@Parameter(names = { "-d",
			"--driver" }, converter = FileConverter.class, description = "PKCS#11 Driver", required = false, arity = 1, order = 1)
	private File driver;

	@Parameter(names = { "-p",
			"--password" }, description = "Pass password in command line (USE WITH CAUTION)", required = false, arity = 1, order = 1)
	private String password;

	private CAdESCommand cadesCommand;
	private PAdESCommand padesCommand;

	// _______________________________________

	// ** Singleton **

	private static ArgsParser instance = null;

	private ArgsParser() {
		jCommander = new JCommander(this);
		jCommander.setProgramName("PKCS#11 Digital Signature Tool");
		cadesCommand = new CAdESCommand();
		padesCommand = new PAdESCommand();
		jCommander.addCommand(cadesCommandLabel, cadesCommand);
		jCommander.addCommand(padesCommandLabel, padesCommand);
	}

	public static ArgsParser getInstance() {
		if (instance == null)
			instance = new ArgsParser();
		return instance;
	}

	// ******

	public boolean showCertInfo() {
		return showCertInfo;
	}

	public boolean showKeyUsage() {
		return showKeyUsage;
	}

	public String getPassword() {
		return password;
	}

	public String parseArgs(String[] args) {
		jCommander.parse(args);
		return jCommander.getParsedCommand();
	}

	public File getFileToSign() {
		String command = jCommander.getParsedCommand();
		if (command.equals(cadesCommandLabel))
			return getCadesCommand().getFileToSign();
		else if (command.equals(padesCommandLabel))
			return getPadesCommand().getFileToSign();
		return null;
	}

	public PAdESProp createPAdESProp() {
		if (isPAdES())
			return new PAdESProp(useVisibleSignature(), getUseVisibleSignatureImage(), getNameFieldToSign(),
					getHorizontalAlignment(), getVerticalAlignment(), getPage(), getSkipFieldToUseSelection());
		return null;
	}

	public boolean useVisibleSignature() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel)) {
			if (getPadesCommand().useTextVisibleSignature() || getPadesCommand().getVisibleSignatureImage() != null)
				return true;
		}
		return false;
	}

	public File getUseVisibleSignatureImage() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getVisibleSignatureImage();
		return null;
	}

	public VisualSignatureAlignmentVertical getVerticalAlignment() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getPosVertical();
		return null;
	}

	public VisualSignatureAlignmentHorizontal getHorizontalAlignment() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getPosHorizontal();
		return null;
	}

	public String getNameFieldToSign() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getNameFieldToSign();
		return null;
	}
	
	public boolean getSkipFieldToUseSelection() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getSkipFieldToUseSelection();
		return false;
	}

	public String getOutputDirectory() {
		return getCommand().getOutputDirectory();
	}

	public String getNameNewFile() {
		return getCommand().getNameNewFile();
	}

	public int getPage() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel))
			return getPadesCommand().getPage();
		return 1;
	}

	public SignFormat checkSelectedSignFormat() {
		if (isCAdES() == false && isPAdES() == false)
			return null;
		else if (isCAdES()) {
			return SignFormat.CADES;
		} else if (isPAdES()) {
			return SignFormat.PADES;
		}
		return null;
	}

	public boolean isHelp() {
		if (help)
			return true;
		if (getCommand() != null)
			return getCommand().isHelp();
		return false;
	}

	public boolean isChoice_certificate() {
		if (getCommand() != null)
			return getCommand().isChoice_certificate();
		return false;
	}

	public File getDriver() {
		// if (getCommand() != null)
		// return getCommand().getDriver();
		// else
		return driver;
	}
	
	public void showDefaultHelp() {
		jCommander.usage();
	}

	public void showHelp() {
		List<ParameterDescription> parameters = null;
		if (help) {
			jCommander.usage();
			return;
		} else if (isCAdES() && getCommand().isHelp()) {
			parameters = jCommander.getCommands().get(cadesCommandLabel).getParameters();
		} else if (isPAdES() && getCommand().isHelp()) {
			parameters = jCommander.getCommands().get(padesCommandLabel).getParameters();
		}
		Comparator<? super ParameterDescription> parameterDescriptionComparator = jCommander
				.getParameterDescriptionComparator();
		parameters.sort(parameterDescriptionComparator);
		System.out.println("Usage:" + jCommander.getProgramDisplayName());
		System.out.println(" Options:");
		for (ParameterDescription parameterDescription : parameters) {
			System.out.println(" " + parameterDescription.getNames().toString());
			System.out.println("\t" + parameterDescription.getDescription().toString());
		}
	}

	// ---------------------------------------------------------------------------------

	private CAdESCommand getCadesCommand() {
		return cadesCommand;
	}

	private PAdESCommand getPadesCommand() {
		return padesCommand;
	}

	private CommonParam getCommand() {
		String command = jCommander.getParsedCommand();
		if (command != null) {
			if (command.equals(cadesCommandLabel))
				return getCadesCommand();
			else if (command.equals(padesCommandLabel))
				return getPadesCommand();
		}
		return null;
	}

	private boolean isCAdES() {
		String command = jCommander.getParsedCommand();
		if (command != null && command.equals(ArgsParser.cadesCommandLabel))
			return true;
		return false;
	}

	private boolean isPAdES() {
		String command = jCommander.getParsedCommand();
		if (command != null && command.equals(ArgsParser.padesCommandLabel))
			return true;
		return false;
	}

	// Converter
	// create a file from a string
	public class FileConverter implements IStringConverter<File> {
		@Override
		public File convert(String value) {
			System.out.println(value);
			return new File(value);
		}
	}

}
