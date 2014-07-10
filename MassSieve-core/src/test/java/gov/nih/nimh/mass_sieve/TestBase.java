package gov.nih.nimh.mass_sieve;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class TestBase {

	/**
	 * For each required file creates corresponding File object. If no required
	 * files provided, returns default test file. <br/>
	 * Each of required files must exist.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected File[] getSeqFiles(final String... requiredFiles) throws FileNotFoundException, IOException {
		final List<File> result = new ArrayList<File>();

		// add files to a list
		if (null == requiredFiles || requiredFiles.length == 0) {
			result.add(new File(TestConstants.DIR_DATA, TestConstants.DEF_TEST_FILE));
		} else {
			for (final String requiredFile : requiredFiles) {
				result.add(new File(TestConstants.DIR_DATA, requiredFile));
			}
		}

		// check if required files exists
		for (final File f : result) {

			if (!f.exists()) {
				fail("File doesn't exist:" + f.getAbsolutePath());
			}
		}

		return result.toArray(new File[result.size()]);
	}

}
