/*
 * Peptide.java
 *
 * Created on May 10, 2006, 10:59 AM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;

import java.io.Serializable;
import java.util.*;

/**
 * Holds all the information pertaining to a given peptide. Including all of its
 * peptie information pertaining tode hits and proteins it belongs to. A peptide
 * can be viewed as a collection of peptide hits.
 */
public class Peptide implements Serializable, Comparable<Peptide> {
    /**
     *
     */
    private static final long serialVersionUID = -1728381921099752415L;
    private final String sequence;
    private final ArrayList<PeptideHit> peptideHits;
    private final HashSet<String> uniqueScanNumbers;
    private final ArrayList<PeptideHit> omssa;
    private final ArrayList<PeptideHit> mascot;
    private final ArrayList<PeptideHit> xtandem;
    private final ArrayList<PeptideHit> sequest;
    private final ArrayList<PeptideHit> pepxml;
    private final HashSet<String> proteinSet;
    private final HashSet<String> experimentSet;
    private String experimentList;
    private final HashSet<String> fileSet;
    private String fileList;
    private ArrayList<Protein> proteinList;
    private int cluster;
    private ParsimonyType pType;
    private PeptideIndeterminacyType indeterminateType;
    private double theoreticalMass;

    /**
     * Creates a new instance of Peptide from a peptide hit.
     *
     * @param p The initial peptide hit that forms the basis for this peptide.
     */
    public Peptide(final PeptideHit p) {
        sequence = p.getSequence();
        peptideHits = new ArrayList<PeptideHit>();
        uniqueScanNumbers = new HashSet<String>();
        omssa = new ArrayList<PeptideHit>();
        mascot = new ArrayList<PeptideHit>();
        xtandem = new ArrayList<PeptideHit>();
        sequest = new ArrayList<PeptideHit>();
        pepxml = new ArrayList<PeptideHit>();
        proteinSet = new HashSet<String>();
        experimentSet = new HashSet<String>();
        fileSet = new HashSet<String>();
        pType = ParsimonyType.DISTINCT;
        theoreticalMass = -1;
        indeterminateType = null; // PeptideIndeterminacyType.NONE;
        this.addPeptideHit(p);
    }

    /**
     * Adds a new PeptideHit to the peptide object. Must have the same peptide
     * sequence.
     *
     * @param p The peptide hit to be added.
     */
    public void addPeptideHit(final PeptideHit p) {
        if (!p.getSequence().contentEquals(sequence)) {
            System.err.println(sequence);
            System.err.println(p.getSequence());
            System.err.println("Something is wrong, tried to add a peptide to the wrong group!");
            throw new InvalidPeptideGroupException(sequence, p.getSequence());
//            System.exit(1); // TODO: Throw exception instead
        }
        peptideHits.add(p);
        uniqueScanNumbers.add(p.getScanTuple());
        getAppropriateCollForType(p.getSourceType()).add(p);
        for (final ProteinHit pro : p.getProteinHits()) {
            proteinSet.add(pro.getName());
        }
        // proteinSet.add(p.getProteinName());
        if (proteinSet.size() > 1) {
            pType = ParsimonyType.SHARED;
        }
        if (theoreticalMass < 0) {
            if (p.getTheoreticalMass() > 0) {
                theoreticalMass = p.getTheoreticalMass();
            }
        }
        if (indeterminateType == null) {
            if (p.isIndeterminate()) {
                indeterminateType = PeptideIndeterminacyType.ALL;
            } else {
                indeterminateType = PeptideIndeterminacyType.NONE;
            }
        } else {
            if (p.isIndeterminate() && indeterminateType == PeptideIndeterminacyType.NONE) {
                indeterminateType = PeptideIndeterminacyType.SOME;
            }
            if (!p.isIndeterminate() && indeterminateType == PeptideIndeterminacyType.ALL) {
                indeterminateType = PeptideIndeterminacyType.SOME;
            }
        }
        experimentSet.add(p.getExperiment());
        // fileSet.add(p.getSourceFile());
        fileSet.add(p.getRawFile());
    }

    /**
     * Identify appropriate collection to add a protein hit with given source type
     * @param sourceType
     * @return
     */
    private List<PeptideHit> getAppropriateCollForType(AnalysisProgramType sourceType) {
        switch (sourceType) {
            case MASCOT:
                return mascot;
            case OMSSA:
                return omssa;
            case XTANDEM:
                return xtandem;
            case SEQUEST:
                return sequest;
            case PEPXML:
                return pepxml;
            case UNKNOWN:
            default:
                System.err.println("Unable to determine source of peptide");
                throw new InvalidSourceTypeException(sourceType); // TODO Handle it somewhere?
        }
    }

    /**
     * Returns the amino acid sequence for this peptide
     *
     * @return The string of amino acids.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Retrieves the list of PeptideHits
     *
     * @return list of PeptideHits
     */
    public ArrayList<PeptideHit> getPeptideHits() {
        return peptideHits;
    }

    /**
     * Retrieves the number of the cluster that this peptide belongs to.
     *
     * @return Cluster number
     */
    public int getCluster() {
        return cluster;
    }

    /**
     * Sets the number of the cluster that the peptide belongs to.
     *
     * @param c Cluster number
     */
    public void setCluster(final Integer c) {
        cluster = c;
        // for (PeptideHit p:peptideHits) {
        // p.setCluster(c);
        // }
    }

    /**
     * Returns the list of PeptideHits found by OMSSA.
     *
     * @return The list of PeptideHits found by OMSSA
     */
    public ArrayList<PeptideHit> getOmssa() {
        return omssa;
    }

    /**
     * Returns the list of PeptideHits found by X!Tandem
     *
     * @return The list of PeptideHits found by X!Tandem
     */
    public ArrayList<PeptideHit> getXTandem() {
        return xtandem;
    }

    /**
     * Returns the list of PeptideHits found by Mascot
     *
     * @return The list of PeptideHits found by Mascot
     */
    public ArrayList<PeptideHit> getMascot() {
        return mascot;
    }

    /**
     * Returns a list of the names of the proteins to which this peptide belongs
     *
     * @return List of protein names
     */
    public HashSet<String> getProteins() {
        return proteinSet;
    }

    /**
     * Since the proteins are constructed after the peptides, this method allows
     * the protein objects to be added to the peptide.
     *
     * @param mProteins The set of proteins to be updated
     */
    public void updateProteins(final HashMap<String, Protein> mProteins) {
        proteinList = new ArrayList<Protein>();
        final Iterator<String> i = proteinSet.iterator();
        while (i.hasNext()) {
            final Protein p = mProteins.get(i.next());
            proteinList.add(p);
        }
    }

    /**
     *
     * @return
     */
    // public DefaultMutableTreeNode getTree() {
    // DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
    // for (Protein pro:proteinList) {
    // node.add(new DefaultMutableTreeNode(pro));
    // }
    // return node;
    // }

    /**
     * Retreives a string listing the search engines which found this Peptide
     *
     * @return An alphabetical list of search engines.
     */
    public String getSourceTypes(final boolean useQuotes) {
        final StringBuilder sb = new StringBuilder();
        if (containsMascot()) {
            sb.append("MASCOT");
        }
        if (containsOmssa()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append("OMMSA");
        }
        if (containsSequest()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append("SEQUEST");
        }
        if (containsXTandem()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append("XTANDEM");
        }
        if (containsPepXML()) {
            if (sb.length() > 0)
                sb.append(",");
            sb.append("PEPXML");
        }
        if (useQuotes) {
            sb.insert(0, '"');
            sb.append('"');
        }
        return sb.toString();
    }

    /**
     * Retrns True if any of the PeptideHits contained by this Peptide were
     * found by OMSSA, false otherwise.
     *
     * @return True if found by the OMSSA algorithm, false otherwise.
     */
    public boolean containsOmssa() {
        if (omssa.size() > 0)
            return true;
        return false;
    }

    /**
     * Retrns True if any of the PeptideHits contained by this Peptide were
     * found by Mascot, false otherwise.
     *
     * @return True if found by the Mascot algorithm, false otherwise.
     */
    public boolean containsMascot() {
        if (mascot.size() > 0)
            return true;
        return false;
    }

    /**
     * Retrns True if any of the PeptideHits contained by this Peptide were
     * found by X!Tandem, false otherwise.
     *
     * @return True if found by the X!Tandem algorithm, false otherwise.
     */
    public boolean containsXTandem() {
        if (xtandem.size() > 0)
            return true;
        return false;
    }

    /**
     * Retrns True if any of the PeptideHits contained by this Peptide were
     * found by Sequest, false otherwise.
     *
     * @return True if found by the Sequest algorithm, false otherwise.
     */
    public boolean containsSequest() {
        if (sequest.size() > 0)
            return true;
        return false;
    }

    /**
     * Returns True if any of the PeptideHits contained by this Peptide were
     * found in generic PepXML, false otherwise.
     *
     * @return True if found by a generic algorithm, false otherwise.
     */
    public boolean containsPepXML() {
        if (pepxml.size() > 0)
            return true;
        return false;
    }

    /**
     * A string-ified version of this peptide for display purposes.
     *
     * @return The string version of this peptide
     */
    @Override
    public String toString() {
        // return sequence + " (" + proteinSet.size() + ")";
        return sequence;
    }

    /**
     * A string-ified version of this peptide for exporting purposes.
     *
     * @return A CSV string for this peptide
     */
    public String toCSVString(final HashSet<String> exp) {
        final StringBuilder pepHitsStr = new StringBuilder();

        if (exp.size() > 1) {
            for (final String e : exp) {
                pepHitsStr.append(getNumPeptideHits(e).toString());
                pepHitsStr.append(",");
            }
        } else {
            pepHitsStr.append(getNumPeptideHits().toString());
            pepHitsStr.append(",");
        }
        return sequence + "," + pepHitsStr
                // + getNumPeptideHits() + ","
                + getLength() + "," + getNumProteins() + "," + getTheoreticalMass() + "," + getPeptideType() + "," + getSourceTypes(true) + "," + getScanList(true);
    }

    public static String toTabStringHeader() {
        return "Sequence\t" + "Length\t" + "Mass\t" + "Indeterminate\t" + "Type\t" + "Cluster\n";
    }

    public String toTabString() {
        return sequence + "\t" + getLength() + "\t" + getTheoreticalMass() + "\t" + getIndeterminateType() + "\t" + getPeptideType() + "\t" + getCluster() + "\n";
    }

    /**
     * Return the number of peptide hits in this peptide
     *
     * @return The number of peptide hits.
     */
    public Integer getNumPeptideHits() {
        return uniqueScanNumbers.size();
    }

    public Integer getNumPeptideHits(final String exp) {
        final HashSet<String> scanNumbers = new HashSet<String>();
        for (final PeptideHit p : peptideHits) {
            if (exp.equals(p.getExperiment())) {
                final String combName = p.getScanTuple();
                scanNumbers.add(combName);
            }
        }
        return scanNumbers.size();
    }

    /**
     * Returns the number of proteins that contain this peptide
     *
     * @return The number of proteins that contain this peptide.
     */
    public Integer getNumProteins() {
        return proteinSet.size();
    }

    /**
     * Returns the theoretical mass of this peptide.
     *
     * @return The theoretical mass.
     */
    public Double getTheoreticalMass() {
        return theoreticalMass;
    }

    /**
     * Returns the number of amino acids in this peptide.
     *
     * @return The number of amino acids in this peptide.
     */
    public Integer getLength() {
        return sequence.length();
    }

    /**
     * Returns a enum denoting the parsimony type of this peptide (shared or
     * distinct).
     *
     * @return The parsimony type of the peptide
     */
    public ParsimonyType getPeptideType() {
        return pType;
    }

    /**
     * Returns the set of experiments that any of the PeptideHits are from.
     *
     * @return Set of experiment names.
     */
    public HashSet<String> getExperimentSet() {
        return experimentSet;
    }

    /**
     * Returns the set of file names that any of the PeptideHits are from.
     *
     * @return The set of filenames.
     */
    public HashSet<String> getFileSet() {
        return fileSet;
    }

    /**
     * Returns the set of experiments in a single string suitable for display.
     *
     * @return A list of experiments in a single string.
     */
    public String getExperimentList() {
        if (experimentList == null) {
            String buf = null;
            final ArrayList<String> expList = new ArrayList<String>(experimentSet);
            Collections.sort(expList);
            for (final String p : expList) {
                if (buf == null) {
                    buf = p;
                } else {
                    buf += ", " + p;
                }
            }
            experimentList = buf;
        }
        return experimentList;
    }

    // TODO: fix comment

    /**
     * Returns the set of file names in a single string suitable for display.
     *
     * @return A list of file names in a single string.
     */
    public String getFileList() {
        if (fileList == null) {
            String buf = null;
            final ArrayList<String> fList = new ArrayList<String>(fileSet);
            Collections.sort(fList);
            for (final String p : fList) {
                if (buf == null) {
                    buf = p;
                } else {
                    buf += ", " + p;
                }
            }
            fileList = buf;
        }
        return fileList;
    }

    /**
     * Returns the set of file:scan tuples in a single string suitable for
     * display.
     *
     * @return A list of file:scan tuples in a single string.
     */
    public String getScanList(final boolean useQuotes) {
        StringBuilder buf = null;
        final ArrayList<String> fList = new ArrayList<String>(uniqueScanNumbers);
        Collections.sort(fList);
        for (final String p : fList) {
            if (buf == null) {
                buf = new StringBuilder(p);
            } else {
                buf.append(", ");
                buf.append(p);
            }
        }
        if (useQuotes) {
            buf.insert(0, '"');
            buf.append('"');
        }
        return buf.toString();
    }

    /**
     * The comparator for sorting purposes. Sorts in alphabetical order by
     * peptide sequence.
     *
     * @param p a peptide to compare to.
     * @return Same as string comparison for the peptide sequence.
     */
	@Override
	public int compareTo(final Peptide p) {
		return sequence.compareTo(p.getSequence());
	}

	/**
     * Report if the peptide contains any peptide hits that are inderminate.
     *
     * @return None, Some, or All based upon the number of PeptideHits that are
     *         indeterminate.
     */
	public PeptideIndeterminacyType getIndeterminateType() {
		return indeterminateType;
	}

    public List<PeptideHit> getSequest() {
        return sequest;
    }


    public List<PeptideHit> getPepxml() {
        return pepxml;
    }
}
