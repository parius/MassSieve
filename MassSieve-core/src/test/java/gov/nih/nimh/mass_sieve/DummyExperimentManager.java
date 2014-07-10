package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.logic.ActionResponse;
import gov.nih.nimh.mass_sieve.logic.DataStoreException;
import gov.nih.nimh.mass_sieve.logic.ExperimentManager;
import gov.nih.nimh.mass_sieve.logic.ExperimentsBundle;
import gov.nih.nimh.mass_sieve.tasks.DeterminedTaskListener;
import gov.nih.nimh.mass_sieve.tasks.InputStreamObserver;
import gov.nih.nimh.mass_sieve.tasks.InputStreamProgressObserver;
import gov.nih.nimh.mass_sieve.tasks.TaskListener;
import gov.nih.nimh.mass_sieve.util.IOUtils;
import gov.nih.nimh.mass_sieve.util.LogStub;
import java.io.File;
import java.util.List;

/**
 * Simplifies interface of ExperimentManager by eliminating progress listeners.<br>
 * Class delegates all work to real ExperimentManager with additionally provided
 * do-nothing progress listeners.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class DummyExperimentManager {
    private ExperimentManager man;
    
    private DeterminedTaskListener dtl;

    public DummyExperimentManager() {
        man = new ExperimentManager();

        dtl = new DeterminedTaskListener() {
            private int progress;

            public void onChangeStepName(String stepName) {
            }

            public void onProgress(int curValue, int totalValue) {
                int curProgress = totalValue / curValue;
                if (curProgress - progress >= 10 || progress == 0) {
                    LogStub.trace(curProgress+"%");
                    progress = curProgress;
                }
            }

            public void onFinish() {
            }
        };
    }

    public List<ProteinInfo> addFilesToExperiment(ExperimentData expData, File f) {
        TaskListener taskListener = new DummyTaskListener();
        taskListener.onTaskStarted("Add file "+f.getName(), (int)f.length());
        InputStreamObserver inObserver = new InputStreamProgressObserver(taskListener);
        return man.addFileToExperiment(expData, f, inObserver, dtl);
    }

    public ExperimentData createNewExperiment(String name) {
        return man.createNewExperiment(name);
    }

    public ActionResponse exportResults(File file, PeptideCollection pepCollection, ExportProteinType type) {
        return man.exportResults(file, pepCollection, type);
    }

    void recomputeCutoff(ExperimentData expData) {
        man.recomputeCutoff(expData);
    }

    public void saveExperimentsBundle(ExperimentsBundle eb, File f) throws DataStoreException {
        man.saveExperimentsBundle(eb, f);
    }

    public Experiment getPersistentExperiment(ExperimentData expData) {
        return man.getPersistentExperiment(expData);
    }

    public ExperimentsBundle loadExperimentsBundle(File f) throws DataStoreException {
        return man.loadExperimentsBundle(f);
    }

    public ActionResponse exportDatabase(File file, PeptideCollection pepCollection) {
        return man.exportDatabase(file, pepCollection);
    }

    public ProteinDB getProteinDatabase() {
        return man.getProteinDatabase();
    }

    /**
     * Imports data files
     * @param files
     * @param expData
     * @return
     */
    // TODO write test for this method, as we rely on it in other tests
    protected ProteinDB importData(File[] files,
                                   ExperimentData expData) {
        for (File f : files) {
            addFilesToExperiment(expData, f);
        }
        recomputeCutoff(expData);

        return getProteinDatabase();
    }
}
