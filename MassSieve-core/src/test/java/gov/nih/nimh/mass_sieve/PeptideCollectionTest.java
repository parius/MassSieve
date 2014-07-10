package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;
import org.junit.Test;

import static gov.nih.nimh.mass_sieve.CustomAssertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Jul 21, 2011 3:03:37 PM
 *
 * @author Alexey Tigarev
 */
public class PeptideCollectionTest {
    @Test
    public void testConstructor() {
        PeptideCollection pc = new PeptideCollection();
        assertEquals(0, pc.getMinPeptides().size());
        assertEquals(0, pc.getPeptideHits().size());
        assertEquals(0, pc.getExperimentSet().size());
        assertEquals(-1, pc.getClusterNum());
        assertEquals(null, pc.countablesCount);
    }

    @Test
    public void testAddPeptideHitNewPeptide() {
        // set up
        PeptideCollection pc = new PeptideCollection();
        PeptideHit ph = new PeptideHit();
        ph.setExperiment("testExperiment");
        ph.setSequence("testSequence");
        ph.setSourceType(AnalysisProgramType.OMSSA);

        // excersize
        pc.addPeptideHit(ph);

        // verify
        assertsContainsOnlyElement(pc.getPeptideHits(), ph);
        assertSetEquals(pc.getExperimentSet(), "testExperiment");
        assertContainsOnlyKey(pc.getMinPeptides(), "testSequence");
        Peptide peptide = pc.getMinPeptides().get("testSequence");
        assertsContainsOnlyElement(peptide.getPeptideHits(), ph);
    }

    @Test
    public void testAddPeptideHitExistingPeptide() {
        // set up
        PeptideCollection pc = new PeptideCollection();
        PeptideHit ph1 = new PeptideHit();
        ph1.setExperiment("testExperiment");
        ph1.setSequence("testSequence1");
        ph1.setSourceType(AnalysisProgramType.OMSSA);
        pc.addPeptideHit(ph1);

        PeptideHit ph2 = new PeptideHit();
        ph2.setExperiment("testExperiment");
        ph2.setSequence("testSequence2");
        ph2.setSourceType(AnalysisProgramType.MASCOT);

        // excersize
        pc.addPeptideHit(ph2);

        // verify
        assertListEquals(pc.getPeptideHits(), ph1, ph2);
        assertSetEquals(pc.getExperimentSet(), "testExperiment");
        assertEquals(2, pc.getMinPeptides().size());
        assertTrue(pc.getMinPeptides().containsKey("testSequence1"));
        assertTrue(pc.getMinPeptides().containsKey("testSequence2"));
        Peptide peptide2 = pc.getMinPeptides().get("testSequence2");
        assertsContainsOnlyElement(peptide2.getPeptideHits(), ph2);
    }

    @Test
    public void testAddPeptideHitNull() {
        // set up
        PeptideCollection pc = new PeptideCollection();

        // excersize
        pc.addPeptideHit(null); // nothing happens

        // verify
        assertEquals(0, pc.getMinPeptides().size());
        assertEquals(0, pc.getPeptideHits().size());
        assertEquals(0, pc.getExperimentSet().size());
        assertEquals(-1, pc.getClusterNum());
        assertEquals(null, pc.countablesCount);
    }

    @Test
    public void testAddPeptideGroup() { // TODO
        // set up
        PeptideCollection pc = new PeptideCollection();

        // excersize
        // verify
    }
}
