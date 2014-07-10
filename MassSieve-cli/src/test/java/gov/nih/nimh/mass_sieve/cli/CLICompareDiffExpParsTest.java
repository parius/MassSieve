package gov.nih.nimh.mass_sieve.cli;

import static gov.nih.nimh.mass_sieve.cli.TestConstantsCli.DEF_EXP_FILE_1;
import static gov.nih.nimh.mass_sieve.cli.TestConstantsCli.DEF_EXP_FILE_2;
import static gov.nih.nimh.mass_sieve.cli.TestConstantsCli.DIR_DATA;
import static gov.nih.nimh.mass_sieve.cli.TestConstantsCli.DIR_OUT;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CLICompareDiffExpParsTest extends CLITestBase {

	private final String input_exp_1 = "./" + DIR_DATA + "/" + DEF_EXP_FILE_1;
	private final String input_exp_2 = "./" + DIR_DATA + "/" + DEF_EXP_FILE_2;
	private final String outputExportDatabaseFilename = "./" + DIR_OUT + "/" + "cli_CDEP_export_db";
	private final String outputExportResultsFilename = "./" + DIR_OUT + "/" + "cli_CDEP_export_results";
	private final String outputExportPreferredFilename = "./" + DIR_OUT + "/" + "cli_CDEP_export_pref";
	private final String outputExperimentFilename = "./" + DIR_OUT + "/" + "cli_CDEP_experiment";

	@Test
	public void test() {
		final String[] cmdParams = { "-do", CLI.ACTION_NAME_COMPARE_PARSIMONY, "-n", "CompareExpParsimony", "-ae", input_exp_1, "-ae", input_exp_2, "--export-experiments-database",
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
