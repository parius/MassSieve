package gov.nih.nimh.mass_sieve;

import static org.junit.Assert.assertTrue;
import gov.nih.nimh.mass_sieve.logic.DataStoreException;
import gov.nih.nimh.mass_sieve.logic.ExperimentsBundle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class PersistExperimentTest extends TestBase {

	private DummyExperimentManager man;

	@Before
	public void setUp() {
		man = new DummyExperimentManager();
	}

	@Test
	public void testSaveExperiment() throws DataStoreException, FileNotFoundException, IOException {
		final ExperimentData exp1 = createExperiment("test_1");
		final ExperimentData exp2 = createExperiment("test_2");

		final List<Experiment> experiments = new ArrayList<Experiment>();
		experiments.add(man.getPersistentExperiment(exp1));
		experiments.add(man.getPersistentExperiment(exp2));
		final ExperimentsBundle eb = new ExperimentsBundle(experiments, man.getProteinDatabase());

		final String outFileName = "save_experiments.bin";
		final File outFile = new File(TestConstants.DIR_OUT, outFileName);
		man.saveExperimentsBundle(eb, outFile);
                
                testLoadExperiment(outFile);
	}

	private void testLoadExperiment(File expFile) throws DataStoreException {

		assertTrue("Test file must exist: " + expFile.getAbsolutePath(), expFile.exists());

		final ExperimentsBundle eb = man.loadExperimentsBundle(expFile);
		final List<Experiment> experiments = eb.getExperiments();
		final ProteinDB proteinDB = eb.getProteinDB();

		assertTrue("Loaded empty experiments file.", !experiments.isEmpty());
		assertTrue("Loaded empty protein database.", !proteinDB.isEmpty());
	}

	private ExperimentData createExperiment(final String expName) throws FileNotFoundException, IOException {
		final File[] files = getSeqFiles();
		final ExperimentData expData = man.createNewExperiment(expName);
		expData.setFilterSettings(new FilterSettings());

		for (final File f : files) {
			man.addFilesToExperiment(expData, f);
		}

		return expData;
	}
}
