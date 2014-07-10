package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;
import org.junit.Test;

import static org.junit.Assert.*;
import static gov.nih.nimh.mass_sieve.CustomAssertions.*;

/**
 * Test for peptide group.
 *
 * @author Alexey Tigarev
 */
public class PeptideTest {

    @Test
    public void testAddPeptideHitCorrectGroup() {
        // setup
        PeptideHit ph1 = new PeptideHit();
        ph1.setSequence("testSequence");
        ph1.setSourceType(AnalysisProgramType.OMSSA);
        ph1.setExperiment("testExperiment");
        ph1.setRawFile("testRawFile");


        PeptideHit ph2 = new PeptideHit();
        ph2.setSequence("testSequence"); // peptide hit from the same peptide group
        ph2.setSourceType(AnalysisProgramType.MASCOT);
        ph2.setExperiment("testExperiment");
        ph2.setRawFile("testRawFile");

        Peptide peptide = new Peptide(ph1);

        // excersize
        peptide.addPeptideHit(ph2);

        // verify
        assertEquals("testSequence", peptide.getSequence());
        assertListEquals(peptide.getPeptideHits(), ph1, ph2);

        assertListEquals(peptide.getOmssa(), ph1);
        assertListEmpty(peptide.getXTandem());
        assertListEquals(peptide.getMascot(), ph2);
        assertListEmpty(peptide.getSequest());
        assertListEmpty(peptide.getPepxml());

        assertSetEmpty(peptide.getProteins());
        assertSetEquals(peptide.getExperimentSet(), "testExperiment");
        assertSetEquals(peptide.getFileSet(), "testRawFile");

        assertEquals(new Double(-1), peptide.getTheoreticalMass());
        assertEquals(PeptideIndeterminacyType.NONE, peptide.getIndeterminateType());

    }

    @Test
    public void testConstructor() {
        // setup
        PeptideHit ph = new PeptideHit();
        ph.setSequence("testSequence");
        ph.setSourceType(AnalysisProgramType.OMSSA);
        ph.setExperiment("testExperiment");
        ph.setRawFile("testRawFile");
        
        // excersize
        Peptide peptide = new Peptide(ph);

        // verify
        assertEquals("testSequence", peptide.getSequence());
        assertListEquals(peptide.getPeptideHits(), ph);

        assertListEquals(peptide.getOmssa(), ph);
        assertListEmpty(peptide.getXTandem());
        assertListEmpty(peptide.getMascot());
        assertListEmpty(peptide.getSequest());
        assertListEmpty(peptide.getPepxml());

        assertSetEmpty(peptide.getProteins());
        assertSetEquals(peptide.getExperimentSet(), "testExperiment");
        assertSetEquals(peptide.getFileSet(), "testRawFile");
        
        assertEquals(new Double(-1), peptide.getTheoreticalMass());
        assertEquals(PeptideIndeterminacyType.NONE, peptide.getIndeterminateType());
    }

}
