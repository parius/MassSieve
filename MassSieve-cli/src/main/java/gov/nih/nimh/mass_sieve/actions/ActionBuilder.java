package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.cli.CLIParseException;
import gov.nih.nimh.mass_sieve.FilterSettings;
import org.apache.commons.cli.CommandLine;

import static gov.nih.nimh.mass_sieve.cli.CLI.*;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public abstract class ActionBuilder {

    public abstract ExternalAction createAction(CommandLine cmd) throws CLIParseException;

    protected FilterSettings parseFilterSettings(CommandLine cmd) {
        FilterSettings filter = new FilterSettings();

        if (cmd.hasOption(FILTER_OMSSA_CUTOFF)) {
            String paramOmssaCutoff = cmd.getOptionValue(FILTER_OMSSA_CUTOFF);
            filter.setOmssaCutoff(paramOmssaCutoff);
        }
        if (cmd.hasOption(FILTER_XTANDEM_CUTOFF)) {
            String paramXTandemCutoff = cmd.getOptionValue(FILTER_XTANDEM_CUTOFF);
            filter.setXtandemCutoff(paramXTandemCutoff);
        }
        if (cmd.hasOption(FILTER_MASCOT_CUTOFF)) {
            String paramMascotCutoff = cmd.getOptionValue(FILTER_MASCOT_CUTOFF);
            filter.setMascotCutoff(paramMascotCutoff);
        }
        if (cmd.hasOption(FILTER_SEQUEST_CUTOFF)) {
            String paramSequestCutoff = cmd.getOptionValue(FILTER_SEQUEST_CUTOFF);
            filter.setSequestCutoff(paramSequestCutoff);
        }
        if (cmd.hasOption(FILTER_PEP_PROPHET_CUTOFF)) {
            String paramPepProCutoff = cmd.getOptionValue(FILTER_PEP_PROPHET_CUTOFF);
            filter.setPeptideProphetCutoff(paramPepProCutoff);
        }

        if (cmd.hasOption(FILTER_ESTFDR_CUTOFF)) {
            String paramEstFdrCutoff = cmd.getOptionValue(FILTER_ESTFDR_CUTOFF);
            filter.setEstimatedFdrCutoff(paramEstFdrCutoff);
        }
        if (cmd.hasOption(FILTER_TEXT)) {
            String paramFilterText = cmd.getOptionValue(FILTER_TEXT);
            filter.setFilterText(paramFilterText);
        }
        if (cmd.hasOption(FILTER_USE_ION_IDENT)) {
            String paramUseIonIdent = cmd.getOptionValue(FILTER_USE_ION_IDENT);
            filter.setUseIonIdent(!"0".equals(paramUseIonIdent));
        }
        if (cmd.hasOption(FILTER_USE_PEP_PROPHET)) {
            String paramUsePepProphet = cmd.getOptionValue(FILTER_USE_PEP_PROPHET);
            filter.setUsePepProphet(!"0".equals(paramUsePepProphet));
        }
        if (cmd.hasOption(FILTER_USE_INDETERMINATES)) {
            String paramUseIndet = cmd.getOptionValue(FILTER_USE_INDETERMINATES);
            filter.setUseIndeterminates(!"0".equals(paramUseIndet));
        }

        if (cmd.hasOption(FILTER_USE_PEPTIDES)) {
            String paramFilterPeptides = cmd.getOptionValue(FILTER_USE_PEPTIDES);
            filter.setFilterPeptides(!"0".equals(paramFilterPeptides));
        }
        if (cmd.hasOption(FILTER_PEPHIT_CUTOFF)) {
            String paramPepHitCutoff = cmd.getOptionValue(FILTER_PEPHIT_CUTOFF);
            filter.setPepHitCutoffCount(Integer.parseInt(paramPepHitCutoff));
        }
        if (cmd.hasOption(FILTER_USE_PROTEINS)) {
            String paramFilterProteins = cmd.getOptionValue(FILTER_USE_PROTEINS);
            filter.setFilterProteins(!"0".equals(paramFilterProteins));
        }
        if (cmd.hasOption(FILTER_PEPTIDE_CUTOFF)) {
            String paramPeptideCutoff = cmd.getOptionValue(FILTER_PEPTIDE_CUTOFF);
            filter.setPeptideCutoffCount(Integer.parseInt(paramPeptideCutoff));
        }
        if (cmd.hasOption(FILTER_USE_PROTEINS_COVERAGE)) {
            String paramFilterCoverage = cmd.getOptionValue(FILTER_USE_PROTEINS_COVERAGE);
            filter.setFilterCoverage(!"0".equals(paramFilterCoverage));
        }
        if (cmd.hasOption(FILTER_PROTEINS_COVERAGE)) {
            String paramPeptideCoverage = cmd.getOptionValue(FILTER_PEPTIDE_CUTOFF);
            filter.setCoverageCutoffAmount(Integer.parseInt(paramPeptideCoverage));
        }
        return filter;

    }

    protected ExportParams parseExportParams(CommandLine cmd) {
        ExportParams exportParams = new ExportParams();
        boolean exportOptionSpecified = false;
        if (cmd.hasOption(OPT_EXPORT_EXPERIMENTS_DATABASE)) {
            String exportExpDBFilename = cmd.getOptionValue(OPT_EXPORT_EXPERIMENTS_DATABASE);
            exportParams.exportExpDBFilename = exportExpDBFilename;
            exportOptionSpecified = true;
        }
        if (cmd.hasOption(OPT_EXPORT_EXPERIMENTS_RESULTS)) {
            String exportExpResFilesname = cmd.getOptionValue(OPT_EXPORT_EXPERIMENTS_RESULTS);
            exportParams.exportExpResFilename = exportExpResFilesname;
            exportOptionSpecified = true;
        }
        if (cmd.hasOption(OPT_EXPORT_PREFERRED_PROTEINS)) {
            String exportPrefProtFilename = cmd.getOptionValue(OPT_EXPORT_PREFERRED_PROTEINS);
            exportParams.exportPrefProtFilename = exportPrefProtFilename;
            exportOptionSpecified = true;
        }
        if (cmd.hasOption(OPT_SAVE_EXPERIMENT)) {
            String saveExpFilename = cmd.getOptionValue(OPT_SAVE_EXPERIMENT);
            exportParams.saveExpFilename = saveExpFilename;
            exportOptionSpecified = true;
        }
        return exportOptionSpecified ? exportParams : null;
    }
}
