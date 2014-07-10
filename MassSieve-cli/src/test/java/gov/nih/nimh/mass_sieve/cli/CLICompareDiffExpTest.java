package gov.nih.nimh.mass_sieve.cli;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CLICompareDiffExpTest extends CLITestBase {
	private final String input_exp_1 = "./" + TestConstantsCli.DIR_DATA + "/" + TestConstantsCli.DEF_EXP_FILE_1;
	private final String input_exp_2 = "./" + TestConstantsCli.DIR_DATA + "/" + TestConstantsCli.DEF_EXP_FILE_2;
	private final String export_result_filename = "./" + TestConstantsCli.DIR_OUT + "/" + "cli_CDE_export_diff_exps";

	@Test
	public void test() {
		final String[] cmdParams = { "-do", CLI.ACTION_NAME_COMPARE, "-n", "CompareExperiment", "-add-experiment", input_exp_1, "-add-experiment", input_exp_2, "-ece", export_result_filename };

		final LauncherCLI launcher = new LauncherCLI();
		final int exitCode = launcher.run(cmdParams);

		assertEquals(0, exitCode);
		checkFileNotEmpty(export_result_filename);
	}

}
