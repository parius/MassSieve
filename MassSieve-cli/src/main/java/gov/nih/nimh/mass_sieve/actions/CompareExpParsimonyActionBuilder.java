package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.Const;
import gov.nih.nimh.mass_sieve.cli.CLIParseException;
import org.apache.commons.cli.CommandLine;

import static gov.nih.nimh.mass_sieve.cli.CLI.*;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CompareExpParsimonyActionBuilder extends ActionBuilder {

    @Override
    public ExternalAction createAction(CommandLine cmd) throws CLIParseException {
        CompareExpParsimonyAction action = new CompareExpParsimonyAction();

        if (cmd.hasOption(OPT_NEW_EXPERIMENT)) {
            action.experimentName = cmd.getOptionValue(OPT_NEW_EXPERIMENT);
        } else {
            action.experimentName = Const.DEFAULT_CMP_PARSIMONIES_EXP_NAME;
        }

        if (cmd.hasOption(OPT_ADD_EXPERIMENT)) {
            String[] searches = cmd.getOptionValues(OPT_ADD_EXPERIMENT);
            if (null != searches) {
                for (String s : searches) {
                    action.addInputFileName(s);
                }
            }
        }

        ExportParams exportParams = parseExportParams(cmd);
        if (null == exportParams) {
            throw new CLIParseException("No export options specified.");
        }
        action.exportParams = exportParams;
        action.filter = parseFilterSettings(cmd);
        return action;
    }
}
