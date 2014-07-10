/*
 * PeptidePanel.java
 *
 * Created on August 7, 2006, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.*;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

/**
 *
 * @author slotta
 */
public class PeptideHitPanel extends JPanel {
    private Peptide pep;
    private JScrollPane tableScrollPane;
    private ExperimentPanel expPanel;
    
    /** Creates a new instance of PeptidePanel */
    public PeptideHitPanel(Peptide p, ExperimentPanel ePanel) {
        pep = p;
        expPanel = ePanel;
        setLayout(new BorderLayout());
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        //JCheckBox showProteins = new JCheckBox("Show Proteins");
        //showProteins.addItemListener(new java.awt.event.ItemListener() {
        //    public void itemStateChanged(java.awt.event.ItemEvent evt) {
        //        showProteinsStateChanged(evt);
        //    }
        //});
        //showProteins.setSelected(false);
        toolbar.add(new JLabel("Seq: " + pep.getSequence()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Peptide hit count: " + pep.getNumPeptideHits()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Proteins: " + pep.getNumProteins()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Theoretical Mass: " + pep.getTheoreticalMass()));
        toolbar.addSeparator();
        toolbar.add(new JLabel("Cluster: " + pep.getCluster()));
        
        //toolbar.add(Box.createHorizontalGlue());
        //toolbar.add(showProteins);
        add(toolbar, BorderLayout.NORTH);

        // create table with peptide hits
        tableScrollPane = createPeptideHitsTable(ePanel);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    /**
     * Returns a table containing a list of all PeptideHits
     * @param expPanel The current experiment panel
     * @return A table containing the list of PeptideHits
     */
    private JScrollPane createPeptideHitsTable(ExperimentPanel expPanel) {
        PeptideHitListPanel lp = new PeptideHitListPanel(expPanel);
        List<PeptideHit> peptideHits = pep.getPeptideHits();
        lp.addPeptideHitList(peptideHits);
        return lp.createTable();
    }
    
//    private void showProteinsStateChanged(java.awt.event.ItemEvent evt) {
//        if (evt.getStateChange() == ItemEvent.SELECTED) {
//            //tableScrollPane.setViewportView(pep.getJTableProteins(expPanel));
//            tableScrollPane.setViewportView(createPeptideHitsTable(expPanel));
//        } else {
//            tableScrollPane.setViewportView(createPeptideHitsTable(expPanel));
//        }
//    }
}
