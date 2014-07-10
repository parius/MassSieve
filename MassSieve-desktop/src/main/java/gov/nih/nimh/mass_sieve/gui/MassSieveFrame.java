/*
 * MassSieveFrame.java
 *
 * Created on May 21, 2006, 6:16 PM
 */
package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.*;
import gov.nih.nimh.mass_sieve.logic.ActionResponse;
import gov.nih.nimh.mass_sieve.logic.ApplicationManager;
import gov.nih.nimh.mass_sieve.logic.DataStoreException;
import gov.nih.nimh.mass_sieve.logic.ExperimentManager;
import gov.nih.nimh.mass_sieve.logic.ExperimentsBundle;
import gov.nih.nimh.mass_sieve.tasks.MultiTaskListener;
import gov.nih.nimh.mass_sieve.util.IOUtils;
import gov.nih.nimh.mass_sieve.util.LogStub;
import gov.nih.nimh.mass_sieve.util.SystemInfo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;
import org.biojava.bio.BioException;
import org.biojavax.bio.db.ncbi.GenpeptRichSequenceDB;
import org.biojavax.bio.seq.RichSequence;

/**
 *
 * @author  slotta
 */
public class MassSieveFrame extends javax.swing.JFrame {

    private ApplicationManager applManager;
    private ExperimentManager expManager;

    // data fields
    private ExperimentPanel currentExperiment;
    private HashMap<String, ExperimentPanel> expSet;
    private boolean useDigest;
    private String digestName;

    // view related fields
    private GraphLayoutType glType;
    private boolean useMultiColumnSort;

    // ui components
    private BatchLoadDialog batchLoadDialog;
    private PreferencesDialog optDialog;
    private JTabbedPane jTabbedPaneMain;
    private StatusBar statusBar;

    // file filters
    private MSFileFilter msFilter;
    private MSVFileFilter msvFilter;
    private FastaFileFilter fastaFilter;
    private TextFileFilter txtFilter;

    private static class StatusBar extends JLabel {

        /** Creates a new instance of StatusBar */
        public StatusBar() {
            super();
            super.setPreferredSize(new Dimension(100, 16));
            setMessage("Ready");
        }

        public final void setMessage(String message) {
            setText(" " + message);
        }
    }

    /** Creates new form MassSieveFrame */
    public MassSieveFrame() {
        applManager = new ApplicationManager();
        expManager = new ExperimentManager();

        initComponents();
        jTabbedPaneMain = new JTabbedPane();
        this.setSize(1000, 750);
        getContentPane().add(jTabbedPaneMain, BorderLayout.CENTER);
        jTabbedPaneMain.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneMainStateChanged(evt);
            }
        });
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        jFileChooserLoad.setMultiSelectionEnabled(true);
        msFilter = new MSFileFilter();
        msvFilter = new MSVFileFilter();
        fastaFilter = new FastaFileFilter();
        jFileChooserLoad.addChoosableFileFilter(msFilter);
        jFileChooserLoad.addChoosableFileFilter(msvFilter);
        jFileChooserLoad.addChoosableFileFilter(fastaFilter);
        jMenuClose.setEnabled(false);
        jMenuClose.setText("Close");
        jMenuSaveExp.setEnabled(false);
        jMenuSaveExp.setText("Save...");
        jMenuSaveExpSet.setEnabled(false);
        jMenuExportDatabase.setText("Export Experiment Database...");
        jMenuExportDatabase.setEnabled(false);
        jMenuExportResults.setText("Export Experiment Results...");
        jMenuExportResults.setEnabled(false);
        jMenuAddSearchResults.setEnabled(false);
        jMenuOpenSeqDB.setEnabled(false);
        jMenuFilterPrefs.setEnabled(false);
        jMenuCompareDiff.setEnabled(false);
        jMenuCompareParsimony.setEnabled(false);
        jMenuExportSeqDB.setEnabled(false);
        jMenuShowSummary.setEnabled(false);
        useDigest = false;
        digestName = "Trypsin";
        useMultiColumnSort = false;
        glType = GraphLayoutType.NODE_LINK_TREE;
        optDialog = new PreferencesDialog(this);
        batchLoadDialog = new BatchLoadDialog(this);
        expSet = new HashMap<String, ExperimentPanel>();
        updateStatusMessage("Please create or load an experiment");
        Logger.getLogger("prefuse").setLevel(Level.WARNING);
        jMenuOpenGenbankDB.setEnabled(false);  // until fully implemented
    }

    public final void updateStatusMessage(String message) {
        long alloc = SystemInfo.getMemoryUsed(SystemInfo.StorageUnit.MegaBytes);
        long max = SystemInfo.getMemoryMax(SystemInfo.StorageUnit.MegaBytes);

        String mem = "Memory Usage: " + alloc + " of " + max + "MB         ";

        statusBar.setMessage(mem + message);
    }

    private void jTabbedPaneMainStateChanged(javax.swing.event.ChangeEvent evt) {
        if (jTabbedPaneMain.getSelectedComponent() instanceof ExperimentPanel) {
            if (currentExperiment != null && currentExperiment != (ExperimentPanel) jTabbedPaneMain.getSelectedComponent()) {
                currentExperiment.saveDockState();
            }
            currentExperiment = (ExperimentPanel) jTabbedPaneMain.getSelectedComponent();
            currentExperiment.loadDockState();
            jMenuSaveExp.setEnabled(true);
            jMenuSaveExp.setText("Save '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
            jMenuExportDatabase.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Database...");
            jMenuExportDatabase.setEnabled(true);
            jMenuExportResults.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Results...");
            jMenuExportResults.setEnabled(true);
        } else {
            jMenuSaveExp.setEnabled(false);
            jMenuExportDatabase.setEnabled(false);
            jMenuExportResults.setEnabled(false);
        }
        if (jTabbedPaneMain.getSelectedComponent() != null) {
            jMenuClose.setText("Close '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
        } else {
            jMenuSaveExp.setEnabled(false);
            jMenuSaveExpSet.setEnabled(false);
            jMenuExportDatabase.setEnabled(false);
            jMenuExportResults.setEnabled(false);
            jMenuClose.setEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupTreeSource = new javax.swing.ButtonGroup();
        jFileChooserLoad = new javax.swing.JFileChooser();
        jOptionPaneAbout = new javax.swing.JOptionPane();
        jMenuBarMain = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuNewExperiment = new javax.swing.JMenuItem();
        jMenuAddSearchResults = new javax.swing.JMenuItem();
        jMenuBatchLoad = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuOpenExp = new javax.swing.JMenuItem();
        jMenuClose = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuSaveExp = new javax.swing.JMenuItem();
        jMenuSaveExpSet = new javax.swing.JMenuItem();
        jMenuExportDatabase = new javax.swing.JMenuItem();
        jMenuExportResults = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuOpenSeqDB = new javax.swing.JMenuItem();
        jMenuOpenGenbankDB = new javax.swing.JMenuItem();
        jMenuExportSeqDB = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuQuit = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuFilterPrefs = new javax.swing.JMenuItem();
        jMenuShowSummary = new javax.swing.JMenuItem();
        jSeparatorCompare = new javax.swing.JSeparator();
        jMenuCompareDiff = new javax.swing.JMenuItem();
        jMenuCompareParsimony = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuOptions = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuGarbageCollect = new javax.swing.JMenuItem();
        jMenuResetLayout = new javax.swing.JMenuItem();
        jMenuAbout = new javax.swing.JMenuItem();

        jFileChooserLoad.setDialogTitle("Open Files");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MassSieve v1.11");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jMenuFile.setText("File");

        jMenuNewExperiment.setText("New Experiment");
        jMenuNewExperiment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuNewExperimentActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuNewExperiment);

        jMenuAddSearchResults.setText("Add Search Results...");
        jMenuAddSearchResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAddSearchResultsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuAddSearchResults);

        jMenuBatchLoad.setText("Batch Load Results...");
        jMenuBatchLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuBatchLoadActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuBatchLoad);
        jMenuFile.add(jSeparator1);

        jMenuOpenExp.setText("Open Experiment(s)...");
        jMenuOpenExp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOpenExpActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuOpenExp);

        jMenuClose.setText("Close Tab");
        jMenuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuClose);
        jMenuFile.add(jSeparator2);

        jMenuSaveExp.setText("Save Experiment...");
        jMenuSaveExp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveExpActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuSaveExp);

        jMenuSaveExpSet.setText("Save All Experiments...");
        jMenuSaveExpSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSaveExpSetActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuSaveExpSet);

        jMenuExportDatabase.setText("Export Experiment Database...");
        jMenuExportDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExportDatabaseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExportDatabase);

        jMenuExportResults.setText("Export Experiment Results...");
        jMenuExportResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExportResultsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExportResults);
        jMenuFile.add(jSeparator3);

        jMenuOpenSeqDB.setText("Import Fasta...");
        jMenuOpenSeqDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOpenSeqDBActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuOpenSeqDB);

        jMenuOpenGenbankDB.setText("Update from Genbank");
        jMenuOpenGenbankDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOpenGenbankDBActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuOpenGenbankDB);

        jMenuExportSeqDB.setText("Export Fasta File...");
        jMenuExportSeqDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuExportSeqDBActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuExportSeqDB);
        jMenuFile.add(jSeparator4);

        jMenuQuit.setText("Quit");
        jMenuQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuQuitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuQuit);

        jMenuBarMain.add(jMenuFile);

        jMenuTools.setText("Tools");

        jMenuFilterPrefs.setText("Change Filter...");
        jMenuFilterPrefs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuFilterPrefsActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuFilterPrefs);

        jMenuShowSummary.setText("Experiment Summary...");
        jMenuShowSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuShowSummaryActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuShowSummary);
        jMenuTools.add(jSeparatorCompare);

        jMenuCompareDiff.setText("Compare Experiment Differences");
        jMenuCompareDiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCompareDiffActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuCompareDiff);

        jMenuCompareParsimony.setText("Compare Experiments w/Parsimony");
        jMenuCompareParsimony.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuCompareParsimonyActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuCompareParsimony);
        jMenuTools.add(jSeparator5);

        jMenuOptions.setText("Preferences");
        jMenuOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuOptionsActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuOptions);

        jMenuBarMain.add(jMenuTools);

        jMenuHelp.setText("Help");

        jMenuGarbageCollect.setText("Compact Memory");
        jMenuGarbageCollect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuGarbageCollectActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuGarbageCollect);

        jMenuResetLayout.setText("Reset layout");
        jMenuResetLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuResetLayoutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuResetLayout);

        jMenuAbout.setText("About");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuAbout);

        jMenuBarMain.add(jMenuHelp);

        setJMenuBar(jMenuBarMain);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuOpenGenbankDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenGenbankDBActionPerformed
        GenpeptRichSequenceDB genbank = new GenpeptRichSequenceDB();
        ProteinDB proteinDB = expManager.getProteinDatabase();
        for (String pName : proteinDB.proteinNames()) {
            try {
                RichSequence seq = genbank.getRichSequence(pName);
                ProteinInfo pInfo = proteinDB.get(pName);
                pInfo.updateFromRichSequence(seq);
                System.out.println("Updated protein " + pName);
            } catch (BioException ex) {
                System.out.println("Unable to find protein " + pName);
            }
            break;
        }

    }//GEN-LAST:event_jMenuOpenGenbankDBActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (currentExperiment != null) {
            currentExperiment.saveDockState();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jMenuSaveExpSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveExpSetActionPerformed
        //FIXME: Inconsistent behaviour: When saving single experiment, "msv" extension automatically added.
        File selectedFile = chooseFileToCreate(msvFilter, null);
        if (null == selectedFile) {
            return;
        }

        List<Experiment> experiments = new ArrayList<Experiment>();
        for (Component c : jTabbedPaneMain.getComponents()) {
            if (c instanceof ExperimentPanel) {
                Experiment exp = ((ExperimentPanel) c).getPersistentExperiment();
                experiments.add(exp);
            }
        }
        saveExperiments(experiments, selectedFile);
    }//GEN-LAST:event_jMenuSaveExpSetActionPerformed

    private void saveExperiments(List<Experiment> experiments, File file) {
        try {
            //FIXME: move all to controller
            ProteinDB proteinDB = expManager.getProteinDatabase();
            ExperimentsBundle eb = new ExperimentsBundle(experiments, proteinDB);
            expManager.saveExperimentsBundle(eb, file);
        } catch (DataStoreException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jMenuOpenExpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenExpActionPerformed
        jFileChooserLoad.setFileFilter(msvFilter);
        int status = jFileChooserLoad.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooserLoad.getSelectedFile();
            try {
                ExperimentsBundle eb = expManager.loadExperimentsBundle(selectedFile);
                List<Experiment> experiments = eb.getExperiments();

                for (Experiment exp : experiments) {
                    if (this.createExperiment(exp.getName())) {
                        currentExperiment.reloadData(exp);
                    }
                }
            } catch (DataStoreException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuOpenExpActionPerformed

    private void jMenuSaveExpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSaveExpActionPerformed
        File selectedFile = chooseFileToCreate(msvFilter, ".msv");
        if (null == selectedFile) {
            return;
        }

        Experiment exp = currentExperiment.getPersistentExperiment();
        saveExperiments(Collections.singletonList(exp), selectedFile);
    }//GEN-LAST:event_jMenuSaveExpActionPerformed

    private void jMenuShowSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuShowSummaryActionPerformed
        Component comp = jTabbedPaneMain.getSelectedComponent();
        if (comp instanceof ExperimentPanel) {
            ((ExperimentPanel) comp).showSummary();
        }
    }//GEN-LAST:event_jMenuShowSummaryActionPerformed

    private void jMenuExportSeqDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExportSeqDBActionPerformed
        File selectedFile = chooseFileToCreate(fastaFilter, null);
        if (null == selectedFile) {
            return;
        }
        System.out.println("Exporting " + selectedFile.getName() + " as a FASTA formated file");

        Set<String> minProteins = new HashSet<String>();
        for (ExperimentPanel exp : expSet.values()) {
            minProteins.addAll(exp.getProteins().keySet());
        }

        ActionResponse result = expManager.exportSeqDB(selectedFile, minProteins);
        if (result.isFailed()) {
            JOptionPane.showMessageDialog(this, result.message, "File Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, result.message);
        }
    }//GEN-LAST:event_jMenuExportSeqDBActionPerformed

    private void jMenuBatchLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuBatchLoadActionPerformed
        batchLoadDialog.setVisible(true);
    }//GEN-LAST:event_jMenuBatchLoadActionPerformed

    private void jMenuGarbageCollectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuGarbageCollectActionPerformed
        System.gc();
    }//GEN-LAST:event_jMenuGarbageCollectActionPerformed

    private void jMenuOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOptionsActionPerformed
        optDialog.setUseDigestBox(useDigest);
        optDialog.setProteaseCombo(digestName);
        optDialog.setGraphLayout(glType);
        optDialog.setVisible(true);
    }//GEN-LAST:event_jMenuOptionsActionPerformed

    private void jMenuCompareParsimonyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCompareParsimonyActionPerformed
        Component comps[] = jTabbedPaneMain.getComponents();
        List<Experiment> expsToCompare = new ArrayList<Experiment>();
        for (Component comp : comps) {
            if (comp instanceof ExperimentPanel) {
                ExperimentPanel exp = (ExperimentPanel) comp;
                expsToCompare.add(exp.getPersistentExperiment());
            }
        }

        String name = "Parsimony Comparison";
        Experiment comparison = expManager.compareExperimentsParsimonies(name, expsToCompare);
        currentExperiment = new ExperimentPanel(this, name);
        currentExperiment.reloadData(comparison);

        expSet.put(name, currentExperiment);
        jTabbedPaneMain.add(currentExperiment);
        jTabbedPaneMain.setSelectedComponent(currentExperiment);
    }//GEN-LAST:event_jMenuCompareParsimonyActionPerformed

    private void jMenuCompareDiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCompareDiffActionPerformed
        ListPanel cPanel = new ListPanel();
        cPanel.addProteinList(expSet);
        JScrollPane compare = cPanel.createTable();
        compare.setName("Differences Comparison");
        jTabbedPaneMain.add(compare);
        jTabbedPaneMain.setSelectedComponent(compare);
    }//GEN-LAST:event_jMenuCompareDiffActionPerformed

    private void jMenuNewExperimentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuNewExperimentActionPerformed
        //javax.swing.JOptionPane optPane = new javax.swing.JOptionPane();
        String s = JOptionPane.showInputDialog(this, "Experiment Name");
        if (s != null && s.length() > 0) {
            createExperiment(s);
            jMenuClose.setEnabled(true);
            jMenuClose.setText("Close '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
            jMenuSaveExp.setEnabled(true);
            jMenuSaveExp.setText("Save '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
            jMenuExportDatabase.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Database...");
            jMenuExportDatabase.setEnabled(true);
            jMenuExportResults.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Results...");
            jMenuExportResults.setEnabled(true);
        }
    }//GEN-LAST:event_jMenuNewExperimentActionPerformed

    public boolean createExperiment(String name) {
        if (expSet.containsKey(name)) {
            JOptionPane.showMessageDialog(MassSieveFrame.this, "There is already an experiment named " + name);
            return false;
        } else {
            this.createExperimentPanel(name);
            return true;
        }
    }

    public void createExperimentPanel(String name) {
        ExperimentPanel expPanel = new ExperimentPanel(this, name);
        currentExperiment = expPanel;
        jTabbedPaneMain.add(currentExperiment);
        jTabbedPaneMain.setSelectedComponent(currentExperiment);
        jMenuAddSearchResults.setEnabled(true);
        jMenuOpenSeqDB.setEnabled(true);
        jMenuFilterPrefs.setEnabled(true);
        jMenuShowSummary.setEnabled(true);
        jMenuClose.setEnabled(true);
        jMenuClose.setText("Close '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
        jMenuSaveExp.setEnabled(true);
        jMenuSaveExp.setText("Save '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
        jMenuSaveExpSet.setEnabled(true);
        jMenuExportDatabase.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Database...");
        jMenuExportDatabase.setEnabled(true);
        jMenuExportResults.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Results...");
        jMenuExportResults.setEnabled(true);
        expSet.put(expPanel.getName(), currentExperiment);
        if (expSet.size() >= 2) {
            jMenuCompareDiff.setEnabled(true);
            jMenuCompareParsimony.setEnabled(true);
        }
    }

    private void jMenuFilterPrefsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuFilterPrefsActionPerformed
        currentExperiment = (ExperimentPanel) jTabbedPaneMain.getSelectedComponent();
        currentExperiment.showPreferences();
    }//GEN-LAST:event_jMenuFilterPrefsActionPerformed

    private void jMenuOpenSeqDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuOpenSeqDBActionPerformed
        jFileChooserLoad.setFileFilter(fastaFilter);
        int status = jFileChooserLoad.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFiles[] = jFileChooserLoad.getSelectedFiles();
            addSeqDBfiles(selectedFiles);
        }
        jMenuExportSeqDB.setEnabled(true);
    }//GEN-LAST:event_jMenuOpenSeqDBActionPerformed

    private void addSeqDBfiles(final File files[]) {
        MultiTaskListener listener = new MultiTaskListener() {

            private int tasksNum;
            private ProgressMonitor mon;

            public void onTaskStarted(String taskName, int taskSize) {
                mon = new ProgressMonitor(MassSieveFrame.this, "Loading " + taskName, "", 0, taskSize);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            public void onProgress(int taskStep) throws InterruptedIOException {
                checkMonitorCancelled();
                mon.setProgress(taskStep);
            }

            public void onTaskFinished() {
                tasksNum++;
                setCursor(null);
            }

            public void onMultiTaskFinished() {
                //jOptionPaneAbout.showMessageDialog(MassSieveFrame.this, "Imported " + seqCount + " sequences, with " + dupCount + " duplicates.");
                JOptionPane.showMessageDialog(MassSieveFrame.this, "Imported " + tasksNum + " sequences");
            }

            public void onTaskCancelled() {
                setCursor(null);
            }

            public void onTaskFailed() {
                setCursor(null);
            }

            private void checkMonitorCancelled() throws InterruptedIOException {
                if (mon.isCanceled()) {
                    throw new InterruptedIOException("progress");
                }
            }
        };
        expManager.addSeqDBfiles(files, listener);
    }

    private void jMenuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuCloseActionPerformed
        Component current = jTabbedPaneMain.getSelectedComponent();
        jTabbedPaneMain.remove(current);
        if (current instanceof ExperimentPanel) {
            expSet.remove(current.getName());
            if (expSet.size() < 2) {
                jMenuCompareDiff.setEnabled(false);
                jMenuCompareParsimony.setEnabled(false);
            }
        }
        if (!(jTabbedPaneMain.getSelectedComponent() instanceof ExperimentPanel)) {
            jMenuAddSearchResults.setEnabled(false);
            jMenuOpenSeqDB.setEnabled(false);
            jMenuFilterPrefs.setEnabled(false);
            jMenuShowSummary.setEnabled(false);
        }
        if (jTabbedPaneMain.getTabCount() < 1) {
            jMenuClose.setEnabled(false);
            jMenuClose.setText("Close");
            jMenuSaveExp.setEnabled(false);
            jMenuSaveExp.setText("Save...");
            jMenuSaveExpSet.setEnabled(false);
            jMenuExportDatabase.setText("Export Experiment Database...");
            jMenuExportDatabase.setEnabled(false);
            jMenuExportResults.setText("Export Experiment Results...");
            jMenuExportResults.setEnabled(false);
        } else {
            jMenuClose.setText("Close " + jTabbedPaneMain.getSelectedComponent().getName());
            jMenuSaveExp.setText("Save '" + jTabbedPaneMain.getSelectedComponent().getName() + "'");
            jMenuExportDatabase.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Database...");
            jMenuExportResults.setText("Export '" + jTabbedPaneMain.getSelectedComponent().getName() + "' Results...");
        }
    }//GEN-LAST:event_jMenuCloseActionPerformed

    private void jMenuAddSearchResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAddSearchResultsActionPerformed
        jFileChooserLoad.setFileFilter(msFilter);
        int status = jFileChooserLoad.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            new Thread(new Runnable() {

                public void run() {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                    File selectedFiles[] = jFileChooserLoad.getSelectedFiles();
                    currentExperiment = (ExperimentPanel) jTabbedPaneMain.getSelectedComponent();
                    currentExperiment.addFiles(selectedFiles);
                    setCursor(null);
                }
            }).start();
        }
    }//GEN-LAST:event_jMenuAddSearchResultsActionPerformed

    public void addExperimentAndFiles(ExperimentPanel defExp, String exp, File files[]) {
        if (expSet.containsKey(exp)) {
            currentExperiment = expSet.get(exp);
        } else {
            createExperiment(exp);
        }
        currentExperiment.getFilterSettings().cloneFilterSettings(defExp.getFilterSettings());
        currentExperiment.addFiles(files);
    }

    private void jMenuQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuQuitActionPerformed
        if (currentExperiment != null) {
            currentExperiment.saveDockState();
        }
        applManager.onQuit();
    }//GEN-LAST:event_jMenuQuitActionPerformed

    private void jMenuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuAboutActionPerformed
        String eol = "\n";
        StringBuilder msg = new StringBuilder(this.getTitle());
        
        msg.append("\nLNT/NIMH/NIH\nCreated by Douglas J. Slotta\n");
        msg.append("Mass Spec. proficiency by Melinda A. McFarland\n");
        msg.append(eol);
        msg.append(SystemInfo.checkAllocatedMem()).append(eol);
        msg.append(SystemInfo.checkAvailMem()).append(eol);
        msg.append(SystemInfo.checkMaxMem()).append(eol).append(eol);
        msg.append(SystemInfo.getSystemInfo());

        JOptionPane.showMessageDialog(MassSieveFrame.this, msg.toString());
    }//GEN-LAST:event_jMenuAboutActionPerformed

    private void jMenuResetLayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuResetLayoutActionPerformed
        Component current = jTabbedPaneMain.getSelectedComponent();
        if (current instanceof ExperimentPanel) {
            currentExperiment = (ExperimentPanel) current;
            currentExperiment.resetDockModel();
        }
    }//GEN-LAST:event_jMenuResetLayoutActionPerformed

    private void jMenuExportDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExportDatabaseActionPerformed
        Component current = jTabbedPaneMain.getSelectedComponent();
        if (current instanceof ExperimentPanel) {
            currentExperiment = (ExperimentPanel) current;

            jFileChooserLoad.setSelectedFile(new File(currentExperiment.getName() + "_results.txt"));
            File selectedFile = chooseFileToCreate(txtFilter, null);
            if (null == selectedFile) {
                return;
            }
            System.out.print("Exporting records into " + selectedFile.getName() + "...");
            ActionResponse result = currentExperiment.exportDatabase(selectedFile);
            if (result.isFailed()) {
                JOptionPane.showMessageDialog(this, result.message, "File Error", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("completed!");
        }
    }//GEN-LAST:event_jMenuExportDatabaseActionPerformed

    private void jMenuExportResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuExportResultsActionPerformed
        Component current = jTabbedPaneMain.getSelectedComponent();
        if (current instanceof ExperimentPanel) {
            currentExperiment = (ExperimentPanel) current;

            ExportProteinType proteinType = askForExportProteinType();
            if (null == proteinType) {
                return;
            }
            jFileChooserLoad.setSelectedFile(new File(currentExperiment.getName() + "_results.txt"));
            File selectedFile = chooseFileToCreate(txtFilter, null);
            if (null == selectedFile) {
                return;
            }
            
            System.out.print("Exporting results into " + selectedFile.getName() + "...");
            ActionResponse result = currentExperiment.exportResults(selectedFile, proteinType);
            if (result.isFailed()) {
                JOptionPane.showMessageDialog(this, result.message, "File Error", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("completed!");
        }
    }//GEN-LAST:event_jMenuExportResultsActionPerformed

    /**
     * Asks user to select type of exported set of proteins.
     * Available "preferred only" and "All proteins" cases.
     * @return export protein type or <b>null</b> if dialog was closed.
     */
    private ExportProteinType askForExportProteinType() {
        Object[] options = {
            "Preferred only",
            "All proteins"};
        int type = JOptionPane.showOptionDialog(this,
                "From which set of proteins should the results be derived?",
                "Select Protein Set",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (type) {
            case 0:
                System.out.println("Preferred proteins selected");
                break;
            case 1:
                System.out.println("All proteins selected");
                break;
            case JOptionPane.CLOSED_OPTION:
                System.out.println("Dialog closed");
                return null;
            default:
                throw new IllegalStateException("Not implemented");
        }

        return ExportProteinType.parse(type);
    }

    private File chooseFileToCreate(FileFilter filter, String extension) {
        File selectedFile = null;
        jFileChooserLoad.setFileFilter(filter);
        int status = jFileChooserLoad.showSaveDialog(this);

        if (status == JFileChooser.APPROVE_OPTION) {
            selectedFile = jFileChooserLoad.getSelectedFile();
            try {
                selectedFile = IOUtils.ensureHasExtension(selectedFile, extension);
                if (!selectedFile.createNewFile()) {
                    status = JOptionPane.showConfirmDialog(this,
                            selectedFile.getName() + " exists, are you sure you wish to overwrite it?",
                            "Overwrite?", JOptionPane.YES_NO_OPTION);
                    if (status != JOptionPane.OK_OPTION) {
                        return null;
                    }
                }
            }  catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to export records", "File Error", JOptionPane.ERROR_MESSAGE);
                LogStub.error(ex);
            }
        }

        return selectedFile;
    }

    public void setUseDigest(boolean b) {
        useDigest = b;
    }

    public void setDigestName(String s) {
        digestName = s;
    }

    public void setUseMultiColumnSort(boolean b) {
        useMultiColumnSort = b;
    }

    public boolean getUseDigest() {
        return useDigest;
    }

    public String getDigestName() {
        return digestName;
    }

    public boolean getUseMultiColumnSort() {
        return useMultiColumnSort;
    }

    public void setGraphLayout(GraphLayoutType glt) {
        glType = glt;
    }

    public GraphLayoutType getGraphLayout() {
        return glType;
    }

    public ExperimentManager getManager() {
        return expManager;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MassSieveFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupTreeSource;
    private javax.swing.JFileChooser jFileChooserLoad;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuItem jMenuAddSearchResults;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JMenuItem jMenuBatchLoad;
    private javax.swing.JMenuItem jMenuClose;
    private javax.swing.JMenuItem jMenuCompareDiff;
    private javax.swing.JMenuItem jMenuCompareParsimony;
    private javax.swing.JMenuItem jMenuExportDatabase;
    private javax.swing.JMenuItem jMenuExportResults;
    private javax.swing.JMenuItem jMenuExportSeqDB;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuFilterPrefs;
    private javax.swing.JMenuItem jMenuGarbageCollect;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuNewExperiment;
    private javax.swing.JMenuItem jMenuOpenExp;
    private javax.swing.JMenuItem jMenuOpenGenbankDB;
    private javax.swing.JMenuItem jMenuOpenSeqDB;
    private javax.swing.JMenuItem jMenuOptions;
    private javax.swing.JMenuItem jMenuQuit;
    private javax.swing.JMenuItem jMenuResetLayout;
    private javax.swing.JMenuItem jMenuSaveExp;
    private javax.swing.JMenuItem jMenuSaveExpSet;
    private javax.swing.JMenuItem jMenuShowSummary;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JOptionPane jOptionPaneAbout;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparatorCompare;
    // End of variables declaration//GEN-END:variables
}
