package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.FileInformation;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Class performs functional testing regarding use-case "add search results".
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class AddSearchResultsTest extends TestBase {

    final String expName = "test";
    private DummyExperimentManager man;

    @Before
    public void setUp() {
        man = new DummyExperimentManager();
    }

    @Test
    public void testAddSearchResults_NewExperiment() throws FileNotFoundException, IOException {
        File[] inFiles = getSeqFiles(); //TODO Replace with a reference to a real file
        ExperimentData experimentData = createExperiment();
        ProteinDB loadedProteins = man.importData(inFiles, experimentData);

        // validate
        List<File> files = experimentData.getFiles();
        PeptideCollection pepColl = experimentData.getPepCollection();
        PeptideCollection pepCollOrig = experimentData.getPepCollectionOriginal();
        assertFalse("Loaded proteins are empty", loadedProteins.isEmpty());
        assertArrayEquals("Files mismatch.", inFiles, files.toArray(new File[0]));
        assertEquals("Experiment name changed.", expName, experimentData.getName());
        assertTrue("Peptide collection is empty", checkNotEmpty(pepColl));

        // original peptide collection has no computed proteins, so check only for
        // presence of peptides.
        int pepNumber = pepCollOrig.getMinPeptides().size();
        assertTrue("Original peptide collection is empty", pepNumber > 0);
    }

    @Test
    public void testAddSearchResults_ExistingExperiment() throws FileNotFoundException, IOException {
        // create new experiment and populate it with some results.
        File[] initFiles = getSeqFiles();
        ExperimentData expData = createExperiment();
        ProteinDB initProteinDB = man.importData(initFiles, expData);
        Map<String, ProteinInfo> initProteins = initProteinDB.getMap();

        // fetch current state of experiment:
        List<FileInformation> initFileInfos = new ArrayList<FileInformation>(expData.getFileInfos());

        // add search results to existing experiment:
        File[] inFiles = getSeqFiles(TestConstants.DEF_TEST_FILE_2);
        ProteinDB resultProteinDB = man.importData(inFiles, expData);
        Map<String, ProteinInfo> resultProteins = resultProteinDB.getMap();

        // validate
        assertEquals("Added files mismatch.", 2, expData.getFilesNumber());

        // check that all previosly imported proteins are still present in protein database.
        for (String initKey : initProteinDB.proteinNames()) {
            ProteinInfo initInfo = initProteinDB.get(initKey);
            ProteinInfo newInfo = resultProteinDB.get(initKey);

            assertEquals("Protein info is missing after adding new results for:"+initInfo,
                    initInfo, newInfo);
        }

        // check if any protein were added to protein database:
        int proteinDbSizeDiff = resultProteins.size() - initProteins.size();
        assertTrue("No new proteins were added.", proteinDbSizeDiff > 0);

        // check new FileInfo present
        List<FileInformation> resFileInfos = new ArrayList<FileInformation>(expData.getFileInfos());
        boolean removed = resFileInfos.remove(initFileInfos.get(0));
        assertTrue("Old file info not found", removed);
        assertTrue("No new file info found", resFileInfos.size()  == 1);
    }

    protected boolean checkNotEmpty(PeptideCollection pepColl) {
        boolean result = true;
        if (null == pepColl) {
            return false;
        }

        result &= (0 < pepColl.getMinProteins().size());
        result &= (0 < pepColl.getMinPeptides().size());
        result &= (0 < pepColl.getPeptideNames().size());

        return result;
    }

    private ExperimentData createExperiment() {
        ExperimentData expData = man.createNewExperiment(expName);
        expData.setFilterSettings(new FilterSettings());

        assertNull(expData.getPepCollection());
        // TODO Write a separate test instead

        return expData;
    }
}
