package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.cli.CLI;
import gov.nih.nimh.mass_sieve.cli.CLIParseException;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CompareExpDiffActionBuilder extends ActionBuilder{

    @Override
    public ExternalAction createAction(CommandLine cmd) throws CLIParseException {
        CompareExpDiffAction action = new CompareExpDiffAction();

        String exportFilename = null;
        if (cmd.hasOption(CLI.OPT_EXPORT_COMPARE_EXPS))
        {
             exportFilename = cmd.getOptionValue(CLI.OPT_EXPORT_COMPARE_EXPS);
        }
        action.setExportTableFilename(exportFilename);

        ExportParams exportParams = parseExportParams(cmd);
        action.exportParams = exportParams;

        String[] exps = cmd.getOptionValues(CLI.OPT_ADD_EXPERIMENT);
        if (null != exps) {
            for (String expFile : exps) {
                action.addExperimentFile(expFile);
            }
        }

        return action;
    }

}
