package com.unical.utils;

import java.io.File;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.unical.digitalsignature.SignFormat;

public class ArgsParser {

	private JCommander jCommander;

	public final static String cadesCommandLabel = "cades";
	public final static String padesCommandLabel = "pades";

	/************************
	 **** CLI Parameters ****
	 ************************/
	@Parameter(names = { "-h", "--help" }, description = "show usage", help = true, order = 0)
	private boolean help = false;
	
	/*
	 * Class that contain the common parameters of all command
	 */
	private class CommonParam {
		@Parameter(names = { "-h", "--help" }, description = "show usage", help = true, order = 0)
		private boolean help = false;

		@Parameter(names = { "-d",
				"--driver" }, converter = FileConverter.class, description = "PKCS#11 Driver", required = false, arity = 1, order = 1)
		private File driver;

		@Parameter(names = { "-u", "--key-usage" }, description = "show key usage", order = 2)
		private boolean showKeyUsage;

		@Parameter(names = { "-i", "--info-certificates" }, description = "show certificates info", order = 3)
		private boolean showCertInfo;

		public boolean isHelp() {
			return help;
		}

		public File getDriver() {
			return driver;
		}

		public boolean showCertInfo() {
			return showCertInfo;
		}

		public boolean showKeyUsage() {
			return showKeyUsage;
		}
	}

	private CAdESCommand cadesCommand;

	@Parameters(commandDescription = "CAdES sign format")
	public class CAdESCommand extends CommonParam {
		@Parameter(description = "FileToSign", converter = FileConverter.class)
		private File fileToSign;

		public File getFileToSign() {
			return fileToSign;
		}
	}

	private PAdESCommand padesCommand;

	@Parameters(commandDescription = "PAdES sign format")
	public class PAdESCommand extends CommonParam {

		@Parameter(description = "FileToSign", converter = FileConverter.class, arity = 1, order = 5)
		private File fileToSign;

		@Parameter(names = { "-v",
				"--visible-signature" }, description = "add visible signature - only text ", arity = 0, order = 6)
		private boolean useVisibleSignature;

		@Parameter(names = { "-vi",
				"--visible-signature-image" }, description = "add visible signature - text and image ", arity = 1, order = 7)
		private File visibleSignatureImage;

		public File getFileToSign() {
			return fileToSign;
		}

		public boolean getVisibleSignature() {
			return useVisibleSignature;
		}

		public File getVisibleSignatureImage() {
			return visibleSignatureImage;
		}

	}

	public CAdESCommand getCadesCommand() {
		return cadesCommand;
	}

	public PAdESCommand getPadesCommand() {
		return padesCommand;
	}

	public ArgsParser() {
		jCommander = new JCommander(this);
		jCommander.setProgramName("PKCS#11 Digital Signature Tool");
		cadesCommand = new CAdESCommand();
		padesCommand = new PAdESCommand();
		jCommander.addCommand(cadesCommandLabel, cadesCommand);
		jCommander.addCommand(padesCommandLabel, padesCommand);
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
			return new PAdESProp(getUseVisibleSignature(), getUseVisibleSignatureImage());
		return null;
	}

	public boolean getUseVisibleSignature() {
		String command = jCommander.getParsedCommand();
		if (command.equals(padesCommandLabel)) {
			if (getPadesCommand().getVisibleSignature() || getPadesCommand().getVisibleSignatureImage() != null)
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

	private CommonParam getCommand() {
		String command = jCommander.getParsedCommand();
		if (command.equals(cadesCommandLabel))
			return getCadesCommand();
		else if (command.equals(padesCommandLabel))
			return getPadesCommand();
		return null;
	}

	private boolean isCAdES() {
		String command = jCommander.getParsedCommand();
		if (command.equals(ArgsParser.cadesCommandLabel))
			return true;
		return false;
	}

	private boolean isPAdES() {
		String command = jCommander.getParsedCommand();
		if (command.equals(ArgsParser.padesCommandLabel))
			return true;
		return false;
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
		return (help || getCommand().isHelp() );
	}

	public File getDriver() {
		return getCommand().getDriver();
	}

	public boolean showCertInfo() {
		return getCommand().showCertInfo();
	}

	public boolean showKeyUsage() {
		return getCommand().showKeyUsage();
	}

	public void showHelp() {
		jCommander.usage();
		// List<ParameterDescription> parameters = jCommander.getParameters();
		// Comparator<? super ParameterDescription> parameterDescriptionComparator =
		// jCommander
		// .getParameterDescriptionComparator();
		// parameters.sort(parameterDescriptionComparator);
		// System.out.println("Usage:" + jCommander.getProgramDisplayName());
		// System.out.println(" Options:");
		// for (ParameterDescription parameterDescription : parameters) {
		// System.out.println(" " + parameterDescription.getNames().toString());
		// System.out.println("\t" + parameterDescription.getDescription().toString());
		// }
	}

	/************************
	 ****** Converter *******
	 ************************/

	// create a file from a string
	public class FileConverter implements IStringConverter<File> {
		@Override
		public File convert(String value) {
			System.out.println(value);
			return new File(value);
		}
	}

}
