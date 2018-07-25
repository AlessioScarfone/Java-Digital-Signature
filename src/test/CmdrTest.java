package test;

import org.junit.jupiter.api.Test;

import com.unical.utils.ArgsParser;

class CmdrTest {

	@Test
	void test() {
		String [] args = { "pades","-v","okok.ods"};
		System.out.println("OK");
		ArgsParser cmdr = new ArgsParser();
		String parseArgs = cmdr.parseArgs(args);
		System.out.println(cmdr.getCadesCommand().getFileToSign());
		cmdr.showHelp();
	}

}
