package gov.nih.nimh.mass_sieve.cli;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CLITestBase {

	protected File outputDir;

	@Before
	public void setUp() {
		outputDir = new File(TestConstantsCli.DIR_OUT);

		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

	@After
	public void tearDown() throws IOException {
		FileUtils.forceDelete(outputDir);
	}

	public void checkFileNotEmpty(final String filename) {
		final File realFile = new File(filename);
		final boolean exists = realFile.exists() && realFile.isFile();
		assertEquals("File must exist:" + realFile.getAbsolutePath(), true, exists);
		final boolean notEmpty = realFile.length() > 0;
		assertEquals("File must be not empty:" + realFile.getAbsolutePath(), true, notEmpty);
	}

}
