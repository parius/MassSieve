package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.PeptideCollection;
import gov.nih.nimh.mass_sieve.actions.MergeAction;
import gov.nih.nimh.mass_sieve.AppConfig;
import gov.nih.nimh.mass_sieve.Experiment;
import gov.nih.nimh.mass_sieve.ExperimentData;
import gov.nih.nimh.mass_sieve.ExportProteinType;
import gov.nih.nimh.mass_sieve.actions.CompareExpDiffAction;
import gov.nih.nimh.mass_sieve.actions.CompareExpParsimonyAction;
import gov.nih.nimh.mass_sieve.actions.ExternalAction;
import gov.nih.nimh.mass_sieve.logic.ActionResult;
import gov.nih.nimh.mass_sieve.logic.CompareExperimentDiffData;
import gov.nih.nimh.mass_sieve.logic.DataStoreException;
import gov.nih.nimh.mass_sieve.logic.ExperimentManager;
import gov.nih.nimh.mass_sieve.logic.ExperimentsBundle;
import gov.nih.nimh.mass_sieve.util.LogStub;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ActionExecutor {

    public ActionResult perform(ExternalAction action) {
        if (action instanceof MergeAction) {
            return performMergeAction((MergeAction) action);
        } else if (action instanceof CompareExpDiffAction) {
            return performCompareExperiments((CompareExpDiffAction) action);
        } else if (action instanceof CompareExpParsimonyAction) {
            return performCompareExpParsimonies((CompareExpParsimonyAction) action);
        }

        return null;
    }

    private ActionResult performMergeAction(MergeAction action) {
        ExperimentManager man = new ExperimentManager();

        String expName = action.getExperimentName();
        ExperimentData exp = man.createNewExperiment(expName);
        exp.setFilterSettings(action.getFilterSettings());
        List<File> files = searchForFiles(action.getFileNames());
        for (File f : files) {
            man.addFileToExperiment(exp, f, null, null);
        }
        man.recomputeCutoff(exp);

        String exportExpDBfilename = action.getExportExpDBFilename();
        if (null != exportExpDBfilename) {
            File file = new File(exportExpDBfilename);
            man.exportDatabase(file, exp.getPepCollection());
        }

        String exportExpResFilename = action.getExportExpResFilename();
        if (null != exportExpResFilename) {
            File file = new File(exportExpResFilename);
            man.exportResults(file, exp.getPepCollection(), ExportProteinType.ALL);
        }

        String exportPrefProtFilename = action.getExportPrefProtFilename();
        if (null != exportPrefProtFilename) {
            File file = new File(exportPrefProtFilename);
            man.exportResults(file, exp.getPepCollection(), ExportProteinType.PREFERRED);
        }

        String saveExpFilename = action.getSaveExpFilename();
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

    private List<File> searchForFiles(List<String> fileNames) {
        List<File> result = new ArrayList<File>();
        if (null == fileNames) {
            return result;
        }

        for (String fileName : fileNames) {
            // try by absolute path
            File file = new File(fileName);
            if (!file.exists()) {
                // try current directory
                file = new File(".", fileName);
                if (!file.exists()) {
                    // try default directory
                    String root = AppConfig.getDefaultSearchFilesDirectory();
                    file = new File(root, fileName);
                }
            }

            if (file.exists()) {
                result.add(file);
            } else {
                LogStub.warn("Couldn't find file:" + fileName);
            }
        }

        return result;
    }

    private ActionResult performCompareExperiments(CompareExpDiffAction action) {
        ExperimentManager man = new ExperimentManager();
        List<File> expFiles = action.getExperimentFiles();

        try {
            List<ExperimentsBundle> bundles = new ArrayList<ExperimentsBundle>();
            for (File f : expFiles) {
                ExperimentsBundle eb = man.loadExperimentsBundle(f);
                bundles.add(eb);
            }
            CompareExperimentDiffData diffData = man.compareExperimentDiff(bundles);
            man.exportCompareExperimentDiffData(action.getFilename(), diffData);
        } catch (DataStoreException e) {
            LogStub.error(e);
            return ActionResult.FAILED;
        }
        return ActionResult.SUCCESS;
    }

    private ActionResult performCompareExpParsimonies(CompareExpParsimonyAction action)
    {
        ExperimentManager man = new ExperimentManager();

        List<Experiment> inputExperiments = new ArrayList<Experiment>();
        List<File> files = searchForFiles(action.getFileNames());
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

        Experiment comparison = man.compareExperimentsParsimonies(action.getExperimentName(), inputExperiments);
        final PeptideCollection compPepCol = comparison.getPepCollection();

        String exportDbFilename = action.getExportExpDBFilename();
        if (null != exportDbFilename)
        {
            File exportDbFile = new File(exportDbFilename);
            man.exportDatabase(exportDbFile, compPepCol);
        }

        String exportExpResFilename = action.getExportExpResFilename();
        if (null != exportExpResFilename) {
            File file = new File(exportExpResFilename);
            man.exportResults(file, compPepCol, ExportProteinType.ALL);
        }

        String exportPrefProtFilename = action.getExportPrefProtFilename();
        if (null != exportPrefProtFilename) {
            File file = new File(exportPrefProtFilename);
            man.exportResults(file, compPepCol, ExportProteinType.PREFERRED);
        }

        String saveExpFilename = action.getSaveExpFilename();
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
