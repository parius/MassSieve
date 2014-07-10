package gov.nih.nimh.mass_sieve.cli;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CLIMergeTest extends CLITestBase {

	private final String inputSearchFile1 = "./" + TestConstantsCli.DIR_DATA + "/" + TestConstantsCli.DEF_TEST_FILE;
	private final String inputSearchFile2 = "./" + TestConstantsCli.DIR_DATA + "/" + TestConstantsCli.DEF_TEST_FILE_2;
	private final String outputExportDatabaseFilename = "./" + TestConstantsCli.DIR_OUT + "/" + "cli_MT_export_db";
	private final String outputExportResultsFilename = "./" + TestConstantsCli.DIR_OUT + "/" + "cli_MT_export_results";
	private final String outputExportPreferredFilename = "./" + TestConstantsCli.DIR_OUT + "/" + "cli_MT_export_pref";
	private final String outputExperimentFilename = "./" + TestConstantsCli.DIR_OUT + "/" + "cli_MT_experiment";
	private File outputDir;

	private void deleteResultingTestFiles() {
		deleteFileIfExists(outputExportDatabaseFilename);
		deleteFileIfExists(outputExportResultsFilename);
		deleteFileIfExists(outputExportPreferredFilename);
		deleteFileIfExists(outputExperimentFilename);
	}

	private void deleteFileIfExists(final String filename) {
		final File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void test() {
		final String[] cmdParams = { "-do", CLI.ACTION_NAME_MERGE, "-n", "MergeExperiment", "-a", inputSearchFile1, "-a", inputSearchFile2, "--export-experiments-database",
				outputExportDatabaseFilename, "--export-experiments-results", outputExportResultsFilename, "--export-preferred-proteins", outputExportPreferredFilename, "--save-experiment",
				outputExperimentFilename };

		final LauncherCLI launcher = new LauncherCLI();
		final int exitCode = launcher.run(cmdParams);

		assertEquals(0, exitCode);

		checkFileNotEmpty(outputExportDatabaseFilename);
		checkFileNotEmpty(outputExportResultsFilename);
		checkFileNotEmpty(outputExportPreferredFilename);
		checkFileNotEmpty(outputExperimentFilename);
	}
}
