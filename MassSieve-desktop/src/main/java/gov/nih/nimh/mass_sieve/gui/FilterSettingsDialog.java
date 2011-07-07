/*
 * PreferencesPanel.java
 *
 * Created on July 31, 2006, 3:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import com.jhlabs.awt.*;
import gov.nih.nimh.mass_sieve.FilterSettings;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author slotta
 */
public class FilterSettingsDialog extends JDialog {
    private ExperimentPanel    experiment;
    private int                width = 550;
    private int                height = 600;
    private int                spinWidth = 2;
    private JTextField         pepFilterField, mascotCutoff, omssaCutoff, xtandemCutoff, sequestCutoff, peptideProphetCutoff;
    private JTextField         estimatedFdrCutoff;
    private JCheckBox          useIonIdentBox, useIndeterminatesBox, usePepProphetBox, filterPeptidesBox, filterProteinsBox, filterCoverageBox;
    private JSpinner           pepHitSpinner, peptideSpinner, coverageSpinner;
    private SpinnerNumberModel pepHitCount, peptideCount, coverageAmount;
    private JButton            okButton, cancelButton;
    private boolean            useIonIdent, useIndeterminates, usePepProphet, filterPeptides, filterProteins, filterCoverage;
    private JLabel             usePepProphetLabel;
    private FilterSettings     filterSettings;

    /** Creates a new instance of PreferencesDialog */
    public FilterSettingsDialog(ExperimentPanel exp) {
        super(exp.getParentFrame(),true);
        experiment = exp;
        filterSettings = exp.getFilterSettings();
        setBounds(50,50,width, height);
        setTitle(exp.getName() + " Filter Settings");
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new ParagraphLayout());
        pepFilterField = new JTextField("o+m+x+s+p", 20);
        mascotCutoff = new JTextField("0.5", 5);
        omssaCutoff = new JTextField("0.5", 5);
        xtandemCutoff = new JTextField("0.5", 5);
        sequestCutoff = new JTextField("0.0", 5);
        peptideProphetCutoff = new JTextField("0.95", 5);
        estimatedFdrCutoff = new JTextField("0.66", 5); //todo
        
        useIonIdentBox = new JCheckBox("Use ION >= Ident");
        useIonIdentBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useIonIdentBoxStateChanged(evt);
            }
        });
        
        usePepProphetBox = new JCheckBox("Prefer peptide prophet score");
        usePepProphetBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                usePepProphetBoxStateChanged(evt);
            }
        });
 
        useIndeterminatesBox = new JCheckBox("Discard indeterminate peptide hits.");
        useIndeterminatesBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                useIndeterminatesBoxStateChanged(evt);
            }
        });
        
        filterPeptidesBox = new JCheckBox("Discard peptides with less than");
        filterPeptidesBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterPeptidesBoxStateChanged(evt);
            }
        });
        pepHitCount = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        pepHitSpinner = new JSpinner(pepHitCount);
        setSpinnerColumns(pepHitSpinner);
        
        filterProteinsBox = new JCheckBox("Discard proteins with less than");
        filterProteinsBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterProteinsBoxStateChanged(evt);
            }
        });
        peptideCount = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        peptideSpinner = new JSpinner(peptideCount);
        setSpinnerColumns(peptideSpinner);
        
        filterCoverageBox = new JCheckBox("Discard proteins with less than");
        filterCoverageBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterCoverageBoxStateChanged(evt);
            }
        });
        coverageAmount = new SpinnerNumberModel(1, 1, 100, 1);
        coverageSpinner = new JSpinner(coverageAmount);
        setSpinnerColumns(coverageSpinner);
        
        okButton = new JButton("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        centerPanel.add(new JLabel("Peptide filter:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(pepFilterField);
        JPanel pepFilterPanel = new JPanel(new GridLayoutPlus(4,5,15,2,10,5));
        pepFilterPanel.setBorder(BorderFactory.createTitledBorder("Usage"));
        pepFilterPanel.add(new JLabel("O,o")); pepFilterPanel.add(new JLabel("OMSSA"));
        pepFilterPanel.add(new JLabel(" "));
        pepFilterPanel.add(new JLabel("-"));   pepFilterPanel.add(new JLabel("Difference"));
        pepFilterPanel.add(new JLabel("M,m")); pepFilterPanel.add(new JLabel("Mascot"));
        pepFilterPanel.add(new JLabel(" "));
        pepFilterPanel.add(new JLabel("+"));   pepFilterPanel.add(new JLabel("Union"));
        pepFilterPanel.add(new JLabel("X,x")); pepFilterPanel.add(new JLabel("X!Tandem"));
        pepFilterPanel.add(new JLabel(" "));
        pepFilterPanel.add(new JLabel("&"));   pepFilterPanel.add(new JLabel("Intersection"));
        pepFilterPanel.add(new JLabel("S,s")); pepFilterPanel.add(new JLabel("Sequest"));
        pepFilterPanel.add(new JLabel(" "));
        pepFilterPanel.add(new JLabel("~"));   pepFilterPanel.add(new JLabel("Complement"));
        pepFilterPanel.add(new JLabel("P,p")); pepFilterPanel.add(new JLabel("Generic PepXML"));
        pepFilterPanel.add(new JLabel(" "));
        pepFilterPanel.add(new JLabel("( )")); pepFilterPanel.add(new JLabel("Precedence"));
        centerPanel.add(pepFilterPanel, ParagraphLayout.NEW_LINE);
        
        //centerPanel.add(new JLabel(), ParagraphLayout.NEW_PARAGRAPH);
        //centerPanel.add(new JLabel("The expectation score must be less than:"));
        centerPanel.add(new JLabel("OMSSA cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel("<="));
        centerPanel.add(omssaCutoff);
        centerPanel.add(new JLabel("X!Tandem cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel("<="));
        centerPanel.add(xtandemCutoff);
        centerPanel.add(new JLabel("Mascot cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel("<="));
        centerPanel.add(mascotCutoff);
        centerPanel.add(useIonIdentBox);
        centerPanel.add(new JLabel("Sequest cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel("<="));
        centerPanel.add(sequestCutoff);
        
        //centerPanel.add(new JLabel(), ParagraphLayout.NEW_PARAGRAPH);
        //centerPanel.add(new JLabel("The probability score must be greater than:"));
        centerPanel.add(new JLabel("Peptide Prophet cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel(">="));
        centerPanel.add(peptideProphetCutoff);
        centerPanel.add(usePepProphetBox);

        centerPanel.add(new JLabel("Estimated FDR cutoff:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(new JLabel(("<=")));
        centerPanel.add(estimatedFdrCutoff);

        usePepProphetLabel = new JLabel("<html>NB: If this score exists for a given peptide hit<p>" +
                                         "then this cutoff takes precedence over the<p>" +
                                         "individual expectation cutoffs.");
        centerPanel.add(usePepProphetLabel, ParagraphLayout.NEW_LINE);
        usePepProphetLabel.setVisible(false);
        centerPanel.add(new JLabel("Peptide Hits:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(useIndeterminatesBox);
        centerPanel.add(new JLabel("Peptides:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(filterPeptidesBox);
        centerPanel.add(pepHitSpinner);
        centerPanel.add(new JLabel("peptide hits."));
        centerPanel.add(new JLabel("Proteins:"), ParagraphLayout.NEW_PARAGRAPH);
        centerPanel.add(filterProteinsBox);
        centerPanel.add(peptideSpinner);
        centerPanel.add(new JLabel("peptides."));
        centerPanel.add(filterCoverageBox, ParagraphLayout.NEW_LINE);
        centerPanel.add(coverageSpinner);
        centerPanel.add(new JLabel("% coverage."));
        
        
        add(centerPanel,BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(okButton);
        add(buttonPanel,BorderLayout.SOUTH);
        mascotCutoff.setEnabled(false);
        peptideProphetCutoff.setEnabled(false);
        pepHitSpinner.setEnabled(false);
        peptideSpinner.setEnabled(false);
        coverageSpinner.setEnabled(false);
    }
    
    private void setSpinnerColumns(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField jtf = ((JSpinner.DefaultEditor)editor).getTextField();
            jtf.setColumns(spinWidth);
        }
    }
       
    private void useIonIdentBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            useIonIdent = true;
            mascotCutoff.setEnabled(false);
        } else {
            useIonIdent = false;
            mascotCutoff.setEnabled(true);
        }
    }
    private void usePepProphetBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            usePepProphet = true;
            peptideProphetCutoff.setEnabled(true);
            usePepProphetLabel.setVisible(true);
        } else {
            usePepProphet = false;
            peptideProphetCutoff.setEnabled(false);
            usePepProphetLabel.setVisible(false);
        }
    }
    private void useIndeterminatesBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            useIndeterminates = false;
        } else {
            useIndeterminates = true;
        }
    }
    private void filterPeptidesBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            filterPeptides = true;
            pepHitSpinner.setEnabled(true);
        } else {
            filterPeptides = false;
            pepHitSpinner.setEnabled(false);
        }
    }
    private void filterProteinsBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            filterProteins = true;
            peptideSpinner.setEnabled(true);
        } else {
            filterProteins = false;
            peptideSpinner.setEnabled(false);
        }
    }
    private void filterCoverageBoxStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            filterCoverage = true;
            coverageSpinner.setEnabled(true);
        } else {
            filterCoverage = false;
            coverageSpinner.setEnabled(false);
        }
    }
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        filterSettings.setOmssaCutoff(omssaCutoff.getText());
        filterSettings.setXtandemCutoff(xtandemCutoff.getText());
        filterSettings.setMascotCutoff(mascotCutoff.getText());
        filterSettings.setSequestCutoff(sequestCutoff.getText());
        filterSettings.setPeptideProphetCutoff(peptideProphetCutoff.getText());
        filterSettings.setEstimatedFdrCutoff(estimatedFdrCutoff.getText());
        filterSettings.setUseIonIdent(useIonIdent);
        filterSettings.setUsePepProphet(usePepProphet);
        filterSettings.setFilterText(pepFilterField.getText());
        filterSettings.setUseIndeterminates(useIndeterminates);
        filterSettings.setFilterPeptides(filterPeptides);
        setVisible(false);
        if (filterPeptides) filterSettings.setPepHitCutoffCount(pepHitCount.getNumber().intValue());
        filterSettings.setFilterProteins(filterProteins);
        if (filterProteins) filterSettings.setPeptideCutoffCount(peptideCount.getNumber().intValue());
        filterSettings.setFilterCoverage(filterCoverage);
        if (filterCoverage) filterSettings.setCoverageCutoffAmount(coverageAmount.getNumber().intValue());
        experiment.reloadFiles();
    }
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }
    
    public void updateFilterDisplay() {
        this.setOmssaCutoff(filterSettings.getOmssaCutoff());
        this.setXtandemCutoff(filterSettings.getXtandemCutoff());
        this.setMascotCutoff(filterSettings.getMascotCutoff());
        this.setSequestCutoff(filterSettings.getSequestCutoff());
        this.setPeptideProphetCutoff(filterSettings.getPeptideProphetCutoff());
        this.setEstimatedFdrCutoff(filterSettings.getEstimatedFdrCutoff());
        this.setUseIonIdent(filterSettings.getUseIonIdent());
        this.setUsePepProphet(filterSettings.getUsePepProphet());
        this.setPepFilterField(filterSettings.getFilterText());
        this.setUseIndeterminates(filterSettings.getUseIndeterminates());
        this.setFilterPeptides(filterSettings.getFilterPeptides());
        this.setPepHitCount(filterSettings.getPepHitCutoffCount());
        this.setFilterProteins(filterSettings.getFilterProteins());
        this.setPeptideCount(filterSettings.getPeptideCutoffCount());
    }

    private void setEstimatedFdrCutoff(double expectCutoffValue) {
        estimatedFdrCutoff.setText(String.valueOf(expectCutoffValue));
    }

    public void setPepFilterField(String s) {
        pepFilterField.setText(s);
    }
    
    public void setMascotCutoff(Double d) {
        mascotCutoff.setText(d.toString());
    }
    
    public void setOmssaCutoff(Double d) {
        omssaCutoff.setText(d.toString());
    }
    
    public void setXtandemCutoff(Double d) {
        xtandemCutoff.setText(d.toString());
    }
    
    public void setSequestCutoff(Double d) {
        sequestCutoff.setText(d.toString());
    }
    
    public void setPeptideProphetCutoff(Double d) {
        peptideProphetCutoff.setText(d.toString());
    }
    
    public void setUseIonIdent(boolean b) {
        if (b) {
            useIonIdent = true;
            mascotCutoff.setEnabled(false);
            useIonIdentBox.setSelected(true);
        } else {
            useIonIdent = false;
            mascotCutoff.setEnabled(true);
            useIonIdentBox.setSelected(false);
        }
    }
    
    public void setUsePepProphet(boolean b) {
        if (b) {
            usePepProphet = true;
            peptideProphetCutoff.setEnabled(true);
            usePepProphetBox.setSelected(true);
        } else {
            usePepProphet = false;
            peptideProphetCutoff.setEnabled(false);
            usePepProphetBox.setSelected(false);
        }
    }

    public void setUseIndeterminates(boolean b) {
        if (b) {
            useIndeterminates = true;
            useIndeterminatesBox.setSelected(false);
        } else {
            useIndeterminates = false;
            useIndeterminatesBox.setSelected(true);
        }
    }
    
    public void setFilterPeptides(boolean b) {
        if (b) {
            filterPeptides = true;
            filterPeptidesBox.setSelected(true);
        } else {
            filterPeptides = false;
            filterPeptidesBox.setSelected(false);
        }
    }
    
    public void setFilterProteins(boolean b) {
        if (b) {
            filterProteins = true;
            filterProteinsBox.setSelected(true);
        } else {
            filterProteins = false;
            filterProteinsBox.setSelected(false);
        }
    }
    
    public void setFilterCoverage(boolean b) {
        if (b) {
            filterCoverage = true;
            filterCoverageBox.setSelected(true);
        } else {
            filterCoverage = false;
            filterCoverageBox.setSelected(false);
        }
    }
    
    public void setPepHitCount(int i) {
        pepHitCount.setValue(i);
    }
    
    public void setPeptideCount(int i) {
        peptideCount.setValue(i);
    }
    
    public void setCoverageCount(int i) {
        coverageAmount.setValue(i);
    }
}