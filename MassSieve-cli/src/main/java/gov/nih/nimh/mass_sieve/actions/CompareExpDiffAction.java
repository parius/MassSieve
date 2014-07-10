package gov.nih.nimh.mass_sieve.actions;

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
public class CompareExpDiffAction extends ExternalAction {

    private static final String name = "CompareExpDiff";
    private List<File> files = new ArrayList<File>();
    private String exportFilename;
    ExportParams exportParams;

    public CompareExpDiffAction()
    {
    }

    public void addExperimentFile(String filename) {
        File f = new File(filename);
        files.add(f);
    }

    public String getExportExpDBFilename() {
        return exportParams.exportExpDBFilename;
    }

    public void setExportTableFilename(String exportFilename) {
        this.exportFilename = exportFilename;
    }

    public List<File> getExperimentFiles() {
        return Collections.unmodifiableList(files);
    }

    public String getFilename() {
        return exportFilename;
    }

    public ActionResult perform() {
        ExperimentManager man = new ExperimentManager();
        List<File> expFiles = getExperimentFiles();

        try {
            List<ExperimentsBundle> bundles = new ArrayList<ExperimentsBundle>();
            for (File f : expFiles) {
                ExperimentsBundle eb = man.loadExperimentsBundle(f);
                bundles.add(eb);
            }
            CompareExperimentDiffData diffData = man.compareExperimentDiff(bundles);
            man.exportCompareExperimentDiffData(getFilename(), diffData);
        } catch (DataStoreException e) {
            LogStub.error(e);
            return ActionResult.FAILED;
        }
        return ActionResult.SUCCESS;
    }
}
