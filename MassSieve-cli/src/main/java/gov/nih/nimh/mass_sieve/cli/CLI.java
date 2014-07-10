package gov.nih.nimh.mass_sieve.cli;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public interface CLI {
    public static final String ACTION_NAME_MERGE = "merge";
    public static final String ACTION_NAME_COMPARE = "cmpExp";
    public static final String ACTION_NAME_COMPARE_PARSIMONY = "cmpExpPars";

    public static final String OPT_ACTION = "do";
    public static final String OPT_NEW_EXPERIMENT = "new-experiment";
    public static final String OPT_ADD_SEARCH_RESULTS = "add-search-results";
    public static final String OPT_EXPORT_EXPERIMENTS_DATABASE = "export-experiments-database";
    public static final String OPT_EXPORT_EXPERIMENTS_RESULTS = "export-experiments-results";
    public static final String OPT_EXPORT_PREFERRED_PROTEINS = "export-preferred-proteins";
    public static final String OPT_SAVE_EXPERIMENT = "save-experiment";
    public static final String FILTER_OMSSA_CUTOFF = "filter-omssa-cutoff";
    public static final String FILTER_XTANDEM_CUTOFF = "filter-xtandem-cutoff";
    public static final String FILTER_MASCOT_CUTOFF = "filter-mascot-cutoff";
    public static final String FILTER_SEQUEST_CUTOFF = "filter-sequest-cutoff";
    public static final String FILTER_PEP_PROPHET_CUTOFF = "filter-pep-prophet-cutoff";
    public static final String FILTER_ESTFDR_CUTOFF = "filter-estfdr-cutoff";
    public static final String FILTER_TEXT = "filter-text";
    public static final String FILTER_USE_ION_IDENT = "filter-use-ion-ident";
    public static final String FILTER_USE_PEP_PROPHET = "filter-use-pep-prophet";
    public static final String FILTER_USE_INDETERMINATES = "filter-use-indeterminates";
    public static final String FILTER_USE_PEPTIDES = "filter-use-peptides";
    public static final String FILTER_PEPHIT_CUTOFF = "filter-pephit-cutoff";
    public static final String FILTER_USE_PROTEINS = "filter-use-proteins";
    public static final String FILTER_PEPTIDE_CUTOFF = "filter-peptide-cutoff";
    public static final String FILTER_USE_PROTEINS_COVERAGE = "filter-use-proteins-coverage";
    public static final String FILTER_PROTEINS_COVERAGE = "filter-proteins-coverage";


    public static final String OPT_ADD_EXPERIMENT = "add-experiment";
    public static final String OPT_EXPORT_COMPARE_EXPS = "export-compare-exps";


}
