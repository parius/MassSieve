package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.ExperimentData;
import gov.nih.nimh.mass_sieve.ExportProteinType;
import gov.nih.nimh.mass_sieve.FilterSettings;
import gov.nih.nimh.mass_sieve.logic.ActionResult;
import gov.nih.nimh.mass_sieve.logic.DataStoreException;
import gov.nih.nimh.mass_sieve.logic.ExperimentManager;
import gov.nih.nimh.mass_sieve.util.LogStub;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class MergeAction extends ExternalAction{
    private String name = "Merge";

    String experimentName;
    private List<String> inputFileNames = new ArrayList<String>();
    ExportParams exportParams = new ExportParams();
    FilterSettings filter;

    public String getActionName()
    {
        return name;
    }

    public String getExperimentName()
    {
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

    public FilterSettings getFilterSettings()
    {
        return filter;
    }

    public ActionResult perform() {
        ExperimentManager man = new ExperimentManager();

        String expName = getExperimentName();
        ExperimentData exp = man.createNewExperiment(expName);
        exp.setFilterSettings(getFilterSettings());
        List<File> files = searchForFiles(getFileNames());
        for (File f : files) {
            man.addFileToExperiment(exp, f, null, null);
        }
        man.recomputeCutoff(exp);

        String exportExpDBfilename = getExportExpDBFilename();
        if (null != exportExpDBfilename) {
            File file = new File(exportExpDBfilename);
            man.exportDatabase(file, exp.getPepCollection());
        }

        String exportExpResFilename = getExportExpResFilename();
        if (null != exportExpResFilename) {
            File file = new File(exportExpResFilename);
            man.exportResults(file, exp.getPepCollection(), ExportProteinType.ALL);
        }

        String exportPrefProtFilename = getExportPrefProtFilename();
        if (null != exportPrefProtFilename) {
            File file = new File(exportPrefProtFilename);
            man.exportResults(file, exp.getPepCollection(), ExportProteinType.PREFERRED);
        }

        String saveExpFilename = getSaveExpFilename();
        if (null != saveExpFilename) {
            File file = new File(saveExpFilename);
            try {
                man.saveExperiment(exp, file);
            } catch (DataStoreException ex) {
                LogStub.error(ex);
                return ActionResult.FAILED;
            }
        }

        return ActionResult.SUCCESS;
    }
}
