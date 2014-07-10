package gov.nih.nimh.mass_sieve.logic;

import gov.nih.nimh.mass_sieve.Experiment;
import gov.nih.nimh.mass_sieve.ProteinDB;
import java.util.List;

/**
 * ExperimentsBundle is a set of all opened experiments and protein database.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ExperimentsBundle {

    private final List<Experiment> exps;
    private final ProteinDB proteinDB;

    public ExperimentsBundle(List<Experiment> exps, ProteinDB proteinDB) {
        this.exps = exps;
        this.proteinDB = proteinDB;
    }

    public List<Experiment> getExperiments() {
        return exps;
    }

    public ProteinDB getProteinDB() {
        return proteinDB;
    }
}
