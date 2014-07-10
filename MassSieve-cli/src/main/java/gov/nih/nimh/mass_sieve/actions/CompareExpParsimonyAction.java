package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.Experiment;
import gov.nih.nimh.mass_sieve.ExportProteinType;
import gov.nih.nimh.mass_sieve.FilterSettings;
import gov.nih.nimh.mass_sieve.PeptideCollection;
import gov.nih.nimh.mass_sieve.logic.*;
import gov.nih.nimh.mass_sieve.util.LogStub;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CompareExpParsimonyAction extends ExternalAction {
    ExportParams exportParams;
    FilterSettings filter;
    String experimentName;
    private List<String> inputFileNames = new ArrayList<String>();

    public CompareExpParsimonyAction()
    {
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void addInputFileName(String fileName)
    {
        inputFileNames.add(fileName);
    }

    public List<String> getFileNames()
    {
        return Collections.unmodifiableList(inputFileNames);
    }

    public String getExportExpDBFilename() {
        return exportParams.exportExpDBFilename;
    }

    public String getExportExpResFilename() {
        return exportParams.exportExpResFilename;
    }

    public String getExportPrefProtFilename()
    {
        return exportParams.exportPrefProtFilename;
    }

    public String getSaveExpFilename()
    {
        return exportParams.saveExpFilename;
    }

    public ActionResult perform()
    {
        ExperimentManager man = new ExperimentManager();

        List<Experiment> inputExperiments = new ArrayList<Experiment>();
        List<File> files = searchForFiles(getFileNames());
        try {
            for (int i = 0; i < files.size(); i++) {
                ExperimentsBundle eb = man.loadExperimentsBundle(files.get(i));
                for (Experiment e : eb.getExperiments()) {
                    inputExperiments.add(e);
                }
            }
        } catch (DataStoreException ex) {
            LogStub.error(ex);
            return ActionResult.FAILED;
        }

        Experiment comparison = man.compareExperimentsParsimonies(getExperimentName(), inputExperiments);
        final PeptideCollection compPepCol = comparison.getPepCollection();

        String exportDbFilename = getExportExpDBFilename();
        if (null != exportDbFilename)
        {
            File exportDbFile = new File(exportDbFilename);
            man.exportDatabase(exportDbFile, compPepCol);
        }

        String exportExpResFilename = getExportExpResFilename();
        if (null != exportExpResFilename) {
            File file = new File(exportExpResFilename);
            man.exportResults(file, compPepCol, ExportProteinType.ALL);
        }

        String exportPrefProtFilename = getExportPrefProtFilename();
        if (null != exportPrefProtFilename) {
            File file = new File(exportPrefProtFilename);
            man.exportResults(file, compPepCol, ExportProteinType.PREFERRED);
        }

        String saveExpFilename = getSaveExpFilename();
        if (null != saveExpFilename) {
            File file = new File(saveExpFilename);
            try {
                man.saveExperiment(comparison, file);
            } catch (DataStoreException ex) {
                LogStub.error(ex);
                return ActionResult.FAILED;
            }
        }

        return ActionResult.SUCCESS;
    }
}
