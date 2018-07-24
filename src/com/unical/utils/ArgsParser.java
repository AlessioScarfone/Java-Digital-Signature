package com.unical.utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterDescription;

public class ArgsParser {

	private JCommander jCommander;

	/************************
	 **** CLI Parameters ****
	 ************************/
	@Parameter(description = "File to sign", converter = FileConverter.class)
	private File fileToSign;

	@Parameter(names = { "-c", "--cades" }, description = "CAdES sign format", required = false, order = 0)
	private boolean cades;

	@Parameter(names = { "-p", "--pades" }, description = "PAdES sign format", required = false, order = 1)
	private boolean pades;
	
	@Parameter(names = {"-v","--visible-signature"},description = "Add Visible signature ",order = 2)
	private boolean useVisibleSignature;

	@Parameter(names = { "-d",
			"--driver" }, converter = FileConverter.class, description = "PKCS#11 Driver", required = false, arity = 1, order = 3)
	private File driver;
	
	@Parameter(names = {"-i","--info-certificates"},description="show certificates info",order = 4)
	private boolean showCertInfo;
	
	@Parameter(names = {"-u","--key-usage"},description="show key usage",order = 5)
	private boolean showKeyUsage;

	@Parameter(names = { "-h", "--help" }, help = true, order = 6)
	private boolean help = false;

	public File getFileToSign() {
		return fileToSign;
	}

	public boolean isCAdES() {
		return cades;
	}

	public boolean isPAdES() {
		return pades;
	}

	public boolean isHelp() {
		return help;
	}

	public File getDriver() {
		return driver;
	}
	
	public boolean getUseVisibleSignature() {
		return useVisibleSignature;
	}
	
	public boolean showCertInfo() {
		return showCertInfo;
	}
	
	public boolean showKeyUsage() {
		return showKeyUsage;
	}

	public ArgsParser() {
		jCommander = new JCommander(this);
		jCommander.setProgramName("PKCS#11 Digital Signature Tool");
	}

	public void parseArgs(String[] args) {
		jCommander.parse(args);
	}

	public void showHelp() {
		// jCommander.usage();
		List<ParameterDescription> parameters = jCommander.getParameters();
		Comparator<? super ParameterDescription> parameterDescriptionComparator = jCommander
				.getParameterDescriptionComparator();
		parameters.sort(parameterDescriptionComparator);
		System.out.println("Usage:" + jCommander.getProgramDisplayName());
		System.out.println("  Options:");
		for (ParameterDescription parameterDescription : parameters) {
			System.out.println("    " + parameterDescription.getNames().toString());
			System.out.println("\t" + parameterDescription.getDescription().toString());
		}
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
