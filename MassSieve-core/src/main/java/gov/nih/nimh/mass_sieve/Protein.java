/*
 * Protein.java
 *
 * Created on March 6, 2006, 5:36 PM
 *
 * @author Douglas Slotta
 */
package gov.nih.nimh.mass_sieve;

import ca.odell.glazedlists.EventList;
import gov.nih.nimh.mass_sieve.util.StringUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.biojava.bio.BioException;
import org.biojava.bio.proteomics.IsoelectricPointCalc;
import org.biojava.bio.proteomics.MassCalc;
import org.biojava.bio.seq.Feature;
import org.biojava.bio.seq.impl.ViewSequence;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.RangeLocation;
import org.biojava.bio.symbol.SymbolPropertyTable;
import org.biojava.utils.ChangeVetoException;
import org.biojavax.bio.seq.RichSequence;

public class Protein implements Serializable, Comparable<Protein> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7360463538245984462L;
	
	private String name;
    //private String id;
    private String description;
    private double mass;
    private double pI;
    private int length;
    private int numPeptideHits;
    private HashSet<String> peptideSet;
    private int coverageNum;
    private ArrayList<Peptide> distinct;
    private ArrayList<Peptide> shared;
    private ArrayList<Peptide> allPeptides;
    private int cluster;
    transient private ViewSequence seqObj;
    private HashSet<String> associatedProteins;
    private HashSet<String> experimentSet;
    private HashSet<String> fileSet;
    private ArrayList<Protein> equivalent;
    private ArrayList<Protein> subset;
    private ArrayList<Protein> superset;
    private ArrayList<Protein> differentiable;
    private String equivalentList;
    private String experimentList;
    private String fileList;
    private ParsimonyType pType;
    private int equivalentGroup;
    private boolean mostEquivalent;

    transient private ProteinDB proteinDB;

    /** Creates a new instance of Protein */
    public Protein() {
        name = null;
        description = null;
        mass = -1;
        pI = -1;
        length = 0;
        numPeptideHits = 0;
        peptideSet = new HashSet<String>();
        cluster = -1;
        coverageNum = 0;
        associatedProteins = new HashSet<String>();
        experimentSet = new HashSet<String>();
        fileSet = new HashSet<String>();
        equivalent = new ArrayList<Protein>();
        subset = new ArrayList<Protein>();
        superset = new ArrayList<Protein>();
        differentiable = new ArrayList<Protein>();
        distinct = new ArrayList<Peptide>();
        shared = new ArrayList<Peptide>();
        seqObj = null;
        mostEquivalent = false;
    }

    public int compareTo(Protein p) {
        return name.compareToIgnoreCase(p.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Protein) {
            Protein p = (Protein) obj;
            return name.equalsIgnoreCase(p.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public void addPeptideHitFeatures() {
        //HashSet<Integer> coverage = new HashSet<Integer>();
        for (Peptide pep : allPeptides) {
            ArrayList<PeptideHit> pHits = pep.getPeptideHits();
            for (PeptideHit p : pHits) {
                for (ProteinHit pro : p.getProteinHits()) {
                    if (pro.getName().equals(name)) {
                        if (pro.getStart() < 0) {
                            pro.updateLocation(p.getSequence(), seqObj.seqString());
                            //if (p.getProteinName().equals(name)) {
                            //scanNumbers.add(p.getScanNum());
                            // Compute coverage
                            //for (int i=pro.getStart(); i<=pro.getEnd(); i++) {
                            //for (int i=p.getStart(); i<=p.getEnd(); i++) {
                            //    coverage.add(i);
                            //}
                        }
                        Feature.Template templ = new Feature.Template();

                        //fill in the template
                        templ.annotation = org.biojava.bio.Annotation.EMPTY_ANNOTATION;
                        templ.location = new RangeLocation(pro.getStart(), pro.getEnd());
                        //templ.location = new RangeLocation(p.getStart(),p.getEnd());
                        templ.source = p.getSourceFile();
                        templ.type = "peptide hit";
                        try {
                            seqObj.createFeature(templ);
                        } catch (BioException ex) {
                            ex.printStackTrace();
                        } catch (ChangeVetoException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        //coverageNum = coverage.size();
    }

    public double getMass() {
        if (mass < 0) {
            mass = proteinDB.get(this.name).getMass();
            if (mass < 0 && getSeqObj() != null && seqObj.length() > 0) {
                MassCalc mc = new MassCalc(SymbolPropertyTable.AVG_MASS, false);
                try {
                    mass = mc.getMass(seqObj);
                    mass = (new BigDecimal(mass)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
                    proteinDB.get(this.name).setMass(mass);
                } catch (IllegalSymbolException ex) {
                    //ex.printStackTrace();
                } catch (BioException ex) {
                    ex.printStackTrace();
                } catch (NullPointerException ex) {
                    //ex.printStackTrace();
                }
            }
        }
        if (mass < 0) {
            return 0.0;
        }
        return mass;
    }

    public double getIsoelectricPoint() {
        if (pI < 0 && getSeqObj() != null) {
            IsoelectricPointCalc ic = new IsoelectricPointCalc();
            try {
                pI = ic.getPI(seqObj, true, true);
            } catch (IllegalAlphabetException ex) {
                ex.printStackTrace();
            } catch (IllegalSymbolException ex) {
                ex.printStackTrace();
            } catch (BioException ex) {
                ex.printStackTrace();
            }
            pI = (new BigDecimal(pI)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        }
        if (pI < 0) {
            return 0.0;
        }
        return pI;
    }

    public int getLength() {
        //if (getSeqObj() == null) {
        //    return 0;
        //}
        if (length <= 0) {
            length = proteinDB.get(this.name).getLength();
        }
        return length;
    }

    public void setLength(int n) {
        length = n;
        return;
    }

    public void setLength(String s) {
        this.setLength(Integer.parseInt(s));
    }

    public int getCoverageNum() {
        if (coverageNum <= 0) {
            HashSet<Integer> coverage = new HashSet<Integer>();
            for (Peptide pep : allPeptides) {
                ArrayList<PeptideHit> pHits = pep.getPeptideHits();
                for (PeptideHit p : pHits) {
                    for (ProteinHit pro : p.getProteinHits()) {
                        if (pro.getName().equals(name)) {
                            // Compute coverage
                            if (pro.getStart() >= 0) {
                                for (int i = pro.getStart(); i <= pro.getEnd(); i++) {
                                    coverage.add(i);
                                }
                            }
                        }
                    }
                }
            }
            coverageNum = coverage.size();
        }
        return coverageNum;
    }

    public int getCoverageNum(String exp) {
        HashSet<Integer> coverage = new HashSet<Integer>();
        for (Peptide pep : allPeptides) {
            ArrayList<PeptideHit> pHits = pep.getPeptideHits();
            for (PeptideHit p : pHits) {
                if (p.getExperiment().equals(exp)) {
                    for (ProteinHit pro : p.getProteinHits()) {
                        if (pro.getName().equals(name)) {
                            //if (p.getProteinName().equals(name)) {
                            // Compute coverage
                            for (int i = pro.getStart(); i <= pro.getEnd(); i++) {
                                //for (int i=p.getStart(); i<=p.getEnd(); i++) {
                                coverage.add(i);
                            }
                        }
                    }
                }
            }
        }
        return coverage.size();
    }

    public double getCoveragePercent() {
        int len = getLength();
        int cNum = getCoverageNum();
        if (len == 0 || cNum == 0) {
            return 0.0;
        }
        double pcov = new BigDecimal((double) coverageNum / len * 100.0).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return pcov;
    }

    public double getCoveragePercent(String exp) {
        int len = getLength();
        int cNum = getCoverageNum(exp);
        if (len == 0 || cNum == 0) {
            return 0.0;
        }
        double pcov = new BigDecimal((double) cNum / len * 100.0).setScale(1, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return pcov;
    }

    public int getNumPeptideHits() {
        if (numPeptideHits == 0) {
            HashSet<String> scanNumbers = new HashSet<String>();
            for (Peptide pep : allPeptides) {
                ArrayList<PeptideHit> pHits = pep.getPeptideHits();
                for (PeptideHit p : pHits) {
                    if (p.containsProtein(name)) {
                        //if (p.getProteinName().equals(name)) {
                        String combName = p.getScanTuple();
                        scanNumbers.add(combName);
                    }
                }
            }
            numPeptideHits = scanNumbers.size();
        }
        return numPeptideHits;
    }

    public int getNumPeptideHits(String exp) {
        HashSet<String> scanNumbers = new HashSet<String>();
        for (Peptide pep : allPeptides) {
            ArrayList<PeptideHit> pHits = pep.getPeptideHits();
            for (PeptideHit p : pHits) {
                if ((p.containsProtein(name)) && exp.equals(p.getExperiment())) {
                    //if ((p.getProteinName().equals(name)) && exp.equals(p.getExperiment())) {
                    String combName = p.getScanTuple();
                    scanNumbers.add(combName);
                }
            }
        }
        return scanNumbers.size();
    }

    public int getNumUniquePeptides() {
        return peptideSet.size();
    }

    public int getNumUniquePeptides(String exp) {
        //int count = 0;
        HashSet<String> countPeps = new HashSet<String>();
        for (Peptide pep : allPeptides) {
            //if (pep.getExperimentSet().contains(exp)) { count++; }
            ArrayList<PeptideHit> pHits = pep.getPeptideHits();
            for (PeptideHit p : pHits) {
                if ((p.containsProtein(name)) && exp.equals(p.getExperiment())) {
                    //if ((p.getProteinName().equals(name)) && exp.equals(p.getExperiment())) {
                    countPeps.add(pep.getSequence());
                    break;
                }
            }
        }
        //return count;
        return countPeps.size();
    }

    public ArrayList<PeptideHit> getPeptideHitList() {
        ArrayList<PeptideHit> allPepHits = new ArrayList<PeptideHit>();
        for (Peptide pep : allPeptides) {
            ArrayList<PeptideHit> pHits = pep.getPeptideHits();
            for (PeptideHit p : pHits) {
                if (p.containsProtein(name)) {
                    //if (p.getProteinName().equals(name)) {
                    allPepHits.add(p);
                }
            }
        }
        return allPepHits;
    }

    public void print() {
        System.out.print("Name: " + name);
        //System.out.print(" Id: " + id);
        System.out.print(" Mass: " + getMass());
        System.out.println(" Desc: " + description);
        System.out.println("  " + peptideSet.toString());
    }

    public void setName(String a) {
        name = a;
    }
    //public void setID(String a) {
    //    id = a;
    //}

    public void setDescription(String d) {
        description = d;
    }

    public void addPeptide(String p) {
        peptideSet.add(p);
    }

    public String getName() {
        return name;
    }
    //public String getID() {
    //    return id;
    //}

    public void setMass(double m) {
        mass = m;
    }

    public String getDescription() {
        if (description == null) {
            description = proteinDB.get(this.name).getDescription();
            //return "";
        }
        return description;
    }

    public HashSet<String> getPeptides() {
        return peptideSet;
    }

    public int getCluster() {
        return cluster;
    }

    public void setCluster(int c) {
        cluster = c;
    }

    @Override
    public String toString() {
        return name;
    }

    public static String toTabStringHeader() {
        return "Protein Name\t"
                + "Preferred\t"
                + "Parsimony Type\t"
                + "Cluster\t"
                + "Length\t"
                + "% coverage\t"
                + "Mass\t"
                + "pI\t"
                + "Description\n";
    }

    public String toTabString() {
        return name + "\t"
                + isMostEquivalent() + "\t"
                + getParsimonyType() + "\t"
                + getCluster() + "\t"
                + getLength() + "\t"
                + getCoveragePercent() + "\t"
                + getMass() + "\t"
                + getIsoelectricPoint() + "\t"
                + getDescription() + "\n";
    }

    public ViewSequence getSeqObj() {
        //if (seqObj == null) {
        setSeqObj(proteinDB.get(this.name).getRichSequence());
        //}
        return seqObj;
    }

    public void setSeqObj(RichSequence seq) {
        if (seq != null) {
            if (seq.length() > 0) {
                seqObj = new ViewSequence(seq);
                addPeptideHitFeatures();
            }
            //System.out.println(this.getDescription());
            //System.out.println(seq.getDescription());
            if ((seq.getDescription() != null) && 
                    (this.getDescription().length() < seq.getDescription().length())) {
                setDescription(seq.getDescription());
            }
        }
    }

    public HashSet<Protein> getAssociatedProteinSet() {
        HashSet<Protein> proSet = new HashSet<Protein>();
        proSet.addAll(equivalent);
        proSet.addAll(differentiable);
        proSet.addAll(superset);
        proSet.addAll(subset);
        return proSet;
    }

    public HashSet<String> getAssociatedProteins() {
        return associatedProteins;
    }

    public void addAssociatedProteins(String p) {
        if (!p.equals(name)) {
            associatedProteins.add(p);
        }
    }

    public void addAssociatedProteins(HashSet<String> p) {
        associatedProteins.addAll(p);
        if (associatedProteins.contains(name)) {
            associatedProteins.remove(name);
        }
    }

    public void updateParsimony(HashMap<String, Protein> minProteins) {
        Iterator<String> i = associatedProteins.iterator();
        while (i.hasNext()) {
            String pName = i.next();
            Protein p = minProteins.get(pName);
            switch (compareParsimony(p)) {
                case EQUIVALENT: {
                    equivalent.add(p);
                    break;
                }
                case DIFFERENTIABLE: {
                    differentiable.add(p);
                    break;
                }
                case SUBSET: {
                    subset.add(p);
                    break;
                }
                case SUPERSET: {
                    superset.add(p);
                    break;
                }
                case ERROR: {
                    System.err.println(name + " and " + p + " are not parsimonious!?");
                }
            }
        }
    }

    public ParsimonyType compareParsimony(Protein p) {
        HashSet<String> peps = p.getPeptides();
        if (peps.size() == peptideSet.size()) {
            if (peptideSet.containsAll(peps)) {
                return ParsimonyType.EQUIVALENT;
            } else {
                return ParsimonyType.DIFFERENTIABLE;
            }
        } else if (peps.size() < peptideSet.size()) {
            if (peptideSet.containsAll(peps)) {
                return ParsimonyType.SUBSET;
            } else {
                return ParsimonyType.DIFFERENTIABLE;
            }
        } else if (peps.size() > peptideSet.size()) {
            if (peps.containsAll(peptideSet)) {
                return ParsimonyType.SUPERSET;
            } else {
                return ParsimonyType.DIFFERENTIABLE;
            }
        }
        return ParsimonyType.ERROR;
    }

    public void updatePeptides(HashMap<String, Peptide> minPeptides) {
        Iterator<String> i = peptideSet.iterator();
        allPeptides = new ArrayList<Peptide>();
        while (i.hasNext()) {
            Peptide pep = minPeptides.get(i.next());
            experimentSet.addAll(pep.getExperimentSet());
            fileSet.addAll(pep.getFileSet());
            allPeptides.add(pep);
            if (pep.getPeptideType() == ParsimonyType.DISTINCT) {
                distinct.add(pep);
            } else if (pep.getPeptideType() == ParsimonyType.SHARED) {
                shared.add(pep);
            } else {
                System.err.println("Peptide " + pep + " has no parsimony type set!");
            }
        }
        Collections.sort(allPeptides);
        //Collections.sort(distinct);
        //Collections.sort(shared);
    }

    private boolean isSubsumable() {
        if (!distinct.isEmpty()
                || (differentiable.size() <= 1)
                || !superset.isEmpty()) {
            return false;
        }
        for (int i = 0; i < (differentiable.size() - 1); i++) {
            for (int j = i + 1; j < differentiable.size(); j++) {
                Protein p = differentiable.get(i);
                Protein q = differentiable.get(j);
                if (p.compareParsimony(q) == ParsimonyType.DIFFERENTIABLE) {
                    // Might be, check to see if they cover the peptide set
                    HashSet<String> peps = new HashSet<String>();
                    for (Protein pro : differentiable) {
                        peps.addAll(pro.getPeptides());
                    }
                    if (peps.containsAll(peptideSet)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public void computeParsimonyType() {
        if (distinct.size() >= 1) {
            if (shared.isEmpty()) {
                pType = ParsimonyType.DISCRETE;
                mostEquivalent = true;
                return;
            } else {
                pType = ParsimonyType.DIFFERENTIABLE;
                mostEquivalent = true;
                return;
            }
        }
        if (superset.size() >= 1) {
            pType = ParsimonyType.SUBSET;
            mostEquivalent = false;
            return;
        }
        if (isSubsumable()) {
            pType = ParsimonyType.SUBSUMABLE;
            mostEquivalent = false;
            return;
        }
        if (subset.size() >= 1) {
            pType = ParsimonyType.SUPERSET;
            mostEquivalent = this.checkMostEquivalent();
            return;
        }
        pType = ParsimonyType.EQUIVALENT;
        mostEquivalent = this.checkMostEquivalent();
    }

    private boolean checkMostEquivalent() {
        if (equivalent.isEmpty()) {
            return true;
        }
        Collections.sort(equivalent);
        if (this.compareTo(equivalent.get(0)) < 0) {
            return true;
        }
        return false;
    }

    public ParsimonyType getParsimonyType() {
        return pType;
    }

    public String getEquivalentList() {
        if (equivalentList == null) {
            ArrayList<Protein> equivList = new ArrayList<Protein>(equivalent);
            Collections.sort(equivList);
            equivalentList = StringUtils.toString(", ", equivList.toArray());
        }
        return equivalentList;
    }

    public ArrayList<Protein> getEquivalent() {
        return equivalent;
    }

    public ArrayList<Protein> getSubset() {
        return subset;
    }

    public ArrayList<Protein> getSuperset() {
        return superset;
    }

    public ArrayList<Protein> getDifferentiable() {
        return differentiable;
    }

    public int getEquivalentGroup() {
        return equivalentGroup;
    }

    public void setEquivalentGroup(int eg) {
        equivalentGroup = eg;
    }

    public ArrayList<Peptide> getAllPeptides() {
        return allPeptides;
    }

    public String getExperimentList() {
        if (experimentList == null) {
            ArrayList<String> expList = new ArrayList<String>(experimentSet);
            Collections.sort(expList);
            experimentList = StringUtils.toString(", ", expList.toArray());
        }
        return experimentList;
    }

    public String getFileList() {
        if (fileList == null) {
            ArrayList<String> fList = new ArrayList<String>(fileSet);
            Collections.sort(fList);
            fileList = StringUtils.toString(", ", fList.toArray());
        }
        return fileList;
    }

    public boolean isMostEquivalent() {
        return mostEquivalent;
    }

    public void setMostEquivalent(boolean isMost, EventList list) {
        mostEquivalent = isMost;
        int loc = list.indexOf(this);
        list.set(loc, this);
        if (isMost) {
            for (Protein p : equivalent) {
                p.setMostEquivalent(false, list);
            }
        }
    }

    public String getMostEquivalent() {
        if (mostEquivalent) {
            return this.getName();
        }
        for (Protein p : equivalent) {
            if (p.isMostEquivalent()) {
                return p.getName();
            }
        }
        return "No preferred found";
    }
    
    public void setProteinDB(ProteinDB proteinDB) {
        this.proteinDB = proteinDB;
    }

}
