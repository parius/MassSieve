package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.logic.ActionResponse;
import java.io.File;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ExportExperimentsResultsTest extends TestBase {

    private DummyExperimentManager man;
    private ExperimentData expData;

    @Before
    public void setUp() {
        man = new DummyExperimentManager();
        expData = man.createNewExperiment("test");
        expData.setFilterSettings(new FilterSettings());

        importData(man, expData);
    }

    @Test
    public void exportExperimentsTest_Preferred() {
        // export
        ExportProteinType type = ExportProteinType.PREFERRED;
        String outFilename = "export_exp_results_preferred.txt";
        File outFile = new File(TestConstants.OUT_DIR, outFilename);

        ActionResponse responce = man.exportResults(outFile, expData.getPepCollection(), type);

        // check
        assertTrue("Export results failed", !responce.isFailed());
        assertTrue("Exported file doesn't exist:" + outFile.getAbsolutePath(), outFile.exists());
    }

    @Test
    public void exportExperimentsTest_All() {
        // export
        ExportProteinType type = ExportProteinType.ALL;
        String outFilename = "export_exp_results_all.txt";
        File outFile = new File(TestConstants.OUT_DIR, outFilename);

        ActionResponse responce = man.exportResults(outFile, expData.getPepCollection(), type);

        // check
        assertTrue("Export results failed", !responce.isFailed());
        assertTrue("Exported file doesn't exist:" + outFile.getAbsolutePath(), outFile.exists());
    }

    private void importData(DummyExperimentManager man, ExperimentData expData) {
        File[] files = getSeqFiles();
        for (File f : files) {
            List<ProteinInfo> proteinInfos = man.addFilesToExperiment(expData, f);
            for (ProteinInfo info : proteinInfos) {
                ProteinDB.Instance.add(info);
            }
        }
        man.recomputeCutoff(expData);
    }
}
