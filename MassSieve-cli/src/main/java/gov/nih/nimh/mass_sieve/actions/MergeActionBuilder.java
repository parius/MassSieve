package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.cli.CLIParseException;
import org.apache.commons.cli.CommandLine;

import static gov.nih.nimh.mass_sieve.cli.CLI.*;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class MergeActionBuilder extends ActionBuilder {
    
    @Override
    public MergeAction createAction(CommandLine cmd) throws CLIParseException {
        MergeAction action = new MergeAction();

        if (cmd.hasOption(OPT_NEW_EXPERIMENT)) {
            String expName = cmd.getOptionValue(OPT_NEW_EXPERIMENT);
            action.experimentName = expName;
        }

        if (cmd.hasOption(OPT_ADD_SEARCH_RESULTS)) {
            String[] searches = cmd.getOptionValues(OPT_ADD_SEARCH_RESULTS);
            if (null != searches) {
                for (String s : searches) {
                    action.addInputFileName(s);
                }
            }
        }

        ExportParams exportParams = parseExportParams(cmd);
        if (null == exportParams)
        {
            throw new CLIParseException("No export options specified.");
        }
        action.exportParams = exportParams;
        action.filter = parseFilterSettings(cmd);
        return action;
    }

}
