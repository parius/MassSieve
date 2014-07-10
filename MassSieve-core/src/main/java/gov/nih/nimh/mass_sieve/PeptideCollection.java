/*
 * PeptideCollection.java
 *
 * Created on March 31, 2006, 2:02 PM
 *
 * @author Douglas Slotta
 */
package gov.nih.nimh.mass_sieve;

//import gov.nih.nimh.mass_sieve.gui.PeptideCollectionView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PeptideCollection implements Serializable, Comparable<PeptideCollection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5544591299675162147L;

	private HashMap<String, Peptide> minPeptides;
	private HashMap<String, Protein> minProteins;
	private HashMap<Integer, PeptideCollection> clusters;
	private final HashSet<String> experimentSet;
	private ArrayList<PeptideHit> peptideHits;
	private ArrayList<Protein> equivalents, subsets, supersets, subsumables, differentiables, discretes, countables;
	private int cluster_num;
	Integer countablesCount;

	/** Creates a new instance of PeptideCollection */
	public PeptideCollection() {
		peptideHits = new ArrayList<PeptideHit>();
		minPeptides = new HashMap<String, Peptide>();
		experimentSet = new HashSet<String>();
		cluster_num = -1;
		countablesCount = null;
	}

	public void addPeptideHit(final PeptideHit p) {
		if (p == null) {
			return;
		}
		peptideHits.add(p);
		experimentSet.add(p.getExperiment());
		final String key = p.getSequence();
		if (minPeptides.containsKey(key)) {
			final Peptide pg = minPeptides.get(key);
			pg.addPeptideHit(p);
		} else {
			final Peptide pg = new Peptide(p);
			minPeptides.put(key, pg);
		}
	}

	public void updatePeptideHits() {
		peptideHits = new ArrayList<PeptideHit>();
		for (final Peptide p : minPeptides.values()) {
			peptideHits.addAll(p.getPeptideHits());
			experimentSet.addAll(p.getExperimentSet());
		}
	}

	public void addPeptideGroup(final Peptide pg) {
		final String key = pg.getSequence();
		if (minPeptides.containsKey(key)) {
			System.out.print("This PeptideCollection already contains " + key);
			System.out.println(" are you sure this is what you want?");
		} else {
			minPeptides.put(key, pg);
		}
	}

	// public PeptideCollection getCutoffCollection(double omssa, double mascot,
	// double xtandem) {
	// return getCutoffCollection(omssa, mascot, xtandem, false);
	// }
	public PeptideCollection getCutoffCollection(final FilterSettings fs) { // TODO
																			// filter
																			// by
																			// expect
																			// cutoff
		final PeptideCollection pc = new PeptideCollection();

		for (final PeptideHit p : peptideHits) {
			if (fs.conformsToFilter(p)) {
				pc.addPeptideHit(p);
			}
		}

		return pc;
	}

	public void computeIndeterminates() {
		final HashMap<String, HashSet<PeptideHit>> scan2pep = new HashMap<String, HashSet<PeptideHit>>();
		HashSet<PeptideHit> pepList;

		for (final PeptideHit p : peptideHits) {
			final String scan = p.getScanTuple();
			if (scan2pep.containsKey(scan)) {
				pepList = scan2pep.get(scan);
			} else {
				pepList = new HashSet<PeptideHit>();
				scan2pep.put(scan, pepList);
			}
			pepList.add(p);
		}
		for (final HashSet<PeptideHit> pepSet : scan2pep.values()) {
			boolean indeterm = false;
			if (pepSet.size() > 1) {
				final HashSet<String> pepSeqs = new HashSet<String>();
				for (final PeptideHit p : pepSet) {
					pepSeqs.add(p.getSequence());
				}
				if (pepSeqs.size() > 1)
					indeterm = true;
			}
			for (final PeptideHit p : pepSet) {
				p.setIndeterminate(indeterm);
			}
		}
	}

	public PeptideCollection getNonIndeterminents() {
		final PeptideCollection pc = new PeptideCollection();

		for (final PeptideHit p : peptideHits) {
			if (!p.isIndeterminate()) {
				pc.addPeptideHit(p);
			}
		}
		return pc;
	}

	public void createProteinList(final ProteinDB proteinDB) {
		minProteins = new HashMap<String, Protein>();
		for (final Peptide pg : minPeptides.values()) {
			final String pepName = pg.getSequence();
			for (final String protName : pg.getProteins()) {
				if (minProteins.containsKey(protName)) {
					final Protein prot = minProteins.get(protName);
					prot.addPeptide(pepName);
				} else {
					final Protein prot = new Protein();
					prot.setProteinDB(proteinDB);
					prot.setName(protName);
					prot.addPeptide(pepName);
					prot.setCluster(cluster_num);
					minProteins.put(protName, prot);
				}
			}
		}
		for (final Protein p : minProteins.values()) {
			final ProteinInfo pInfo = new ProteinInfo();
			pInfo.setName(p.getName());
			pInfo.setDescription(p.getDescription());
			pInfo.setLength(p.getLength());
			proteinDB.add(pInfo);
		}
	}

	public void updateProteins(final HashMap<String, Protein> mainProteins) {
		minProteins = new HashMap<String, Protein>();
		for (final Peptide pg : minPeptides.values()) {
			final String pepName = pg.getSequence();
			for (final String protName : pg.getProteins()) {
				if (minProteins.containsKey(protName)) {
					final Protein prot = minProteins.get(protName);
					prot.addPeptide(pepName);
				} else {
					final Protein prot = mainProteins.get(protName);
					minProteins.put(protName, prot);
				}
			}
		}
		for (final Peptide pg : minPeptides.values()) {
			pg.updateProteins(minProteins);
		}
		for (final Protein p : minProteins.values()) {
			p.updatePeptides(minPeptides);
		}
	}

	public void updateClusters() {
		clusters = new HashMap<Integer, PeptideCollection>();
		// relate all peptides to -1 cluster
		for (final Peptide pg : minPeptides.values()) {
			pg.setCluster(-1);
		}
		// assign each peptide to new cluster
		int clusterIndex = 1;
		for (final Peptide pg : minPeptides.values()) {
			if (pg.getCluster() == -1) {
				final PeptideCollection newCluster = new PeptideCollection();
				newCluster.setClusterNum(clusterIndex);
				pg.setCluster(clusterIndex);
				newCluster.addPeptideGroup(pg);
				addLinkedMembers(newCluster, clusterIndex, pg);
				newCluster.updateProteins(minProteins);
				newCluster.updateParsimony();
				clusters.put(clusterIndex, newCluster);
				clusterIndex++;
			}
		}

		// update groups of equivalence
		int equivGroup = 0;
		final HashSet<String> usedProteins = new HashSet<String>();
		final ArrayList<Protein> equivOrder = new ArrayList<Protein>();
		equivOrder.addAll(getCountables());
		equivOrder.addAll(getSubsets());
		equivOrder.addAll(getSubsumables());
		for (final Protein p : equivOrder) {
			if (!usedProteins.contains(p.getName())) {
				p.setEquivalentGroup(equivGroup);
				usedProteins.add(p.getName());
				for (final Protein ps : p.getEquivalent()) {
					ps.setEquivalentGroup(equivGroup);
					usedProteins.add(ps.getName());
				}
				equivGroup++;
			}
		}
	}

	private void addLinkedMembers(final PeptideCollection cluster, final Integer clustNum, final Peptide pg) {
		for (final String proName : pg.getProteins()) {
			final Protein pro = minProteins.get(proName);
			pro.setCluster(clustNum);
			for (final String pepName : pro.getPeptides()) {
				final Peptide subPg = minPeptides.get(pepName);
				if (subPg.getCluster() == -1) {
					subPg.setCluster(clustNum);
					cluster.addPeptideGroup(subPg);
					addLinkedMembers(cluster, clustNum, subPg);
				}
			}
		}
	}

	public void updateParsimony() {
		for (final Protein prot : minProteins.values()) {
			for (final String pepName : prot.getPeptides()) {
				final Peptide pep = minPeptides.get(pepName);
				prot.addAssociatedProteins(pep.getProteins());
			}
		}
		for (final Protein prot : minProteins.values()) {
			prot.updateParsimony(minProteins);
		}
		for (final Protein prot : minProteins.values()) {
			prot.computeParsimonyType();
		}
	}

	public PeptideProteinNameSet getPeptideProteinNameSet() {
		final PeptideProteinNameSet pps = new PeptideProteinNameSet();
		pps.setPeptides(minPeptides.keySet());
		pps.setProteins(minProteins.keySet());
		return pps;
	}

	public PeptideCollection filterByPeptidePerProtein(final ProteinDB proteinDB, final int numPeps) {
		final HashSet<String> proDiscard = new HashSet<String>();
		for (final String pName : minProteins.keySet()) {
			final Protein p = minProteins.get(pName);
			if (p.getNumUniquePeptides() < numPeps) {
				proDiscard.add(pName);
			}
		}
		final PeptideCollection new_pc = new PeptideCollection();
		for (final PeptideHit ph : peptideHits) {
			new_pc.addPeptideHit(ph.maskProtein(proDiscard));
		}
		new_pc.createProteinList(proteinDB);
		return new_pc;
	}

	public PeptideCollection filterByProteinCoverage(final ProteinDB proteinDB, final int minCoverage) {
		final HashSet<String> proDiscard = new HashSet<String>();
		for (final String pName : minProteins.keySet()) {
			final Protein p = minProteins.get(pName);
			if (p.getCoveragePercent() < minCoverage) {
				proDiscard.add(pName);
			}
		}
		final PeptideCollection new_pc = new PeptideCollection();
		for (final PeptideHit ph : peptideHits) {
			new_pc.addPeptideHit(ph.maskProtein(proDiscard));
		}
		new_pc.createProteinList(proteinDB);
		return new_pc;
	}

	public PeptideCollection getPeptidesByHits(final int numHits) {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.getNumPeptideHits() >= numHits) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		new_pc.updatePeptideHits();
		return new_pc;
	}

	public PeptideCollection getOmssa() {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.containsOmssa()) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		return new_pc;
	}

	public PeptideCollection getMascot() {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.containsMascot()) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		return new_pc;
	}

	public PeptideCollection getXTandem() {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.containsXTandem()) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		return new_pc;
	}

	public PeptideCollection getSequest() {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.containsSequest()) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		return new_pc;
	}

	public PeptideCollection getPepXML() {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final Peptide pg : minPeptides.values()) {
			if (pg.containsPepXML()) {
				new_pc.minPeptides.put(pg.getSequence(), pg);
			}
		}
		return new_pc;
	}

	public PeptideCollection union(final PeptideCollection pc) {
		final PeptideCollection new_pc = new PeptideCollection();
		new_pc.minPeptides.putAll(pc.minPeptides);
		for (final String key : minPeptides.keySet()) {
			if (!new_pc.minPeptides.containsKey(key)) {
				new_pc.minPeptides.put(key, minPeptides.get(key));
			}
		}
		return new_pc;
	}

	public PeptideCollection intersection(final PeptideCollection pc) {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final String key : minPeptides.keySet()) {
			if (pc.minPeptides.containsKey(key)) {
				new_pc.minPeptides.put(key, minPeptides.get(key));
			}
		}
		return new_pc;
	}

	public PeptideCollection difference(final PeptideCollection pc) {
		final PeptideCollection new_pc = new PeptideCollection();
		for (final String key : minPeptides.keySet()) {
			if (!pc.minPeptides.containsKey(key)) {
				new_pc.minPeptides.put(key, minPeptides.get(key));
			}
		}
		return new_pc;
	}

	public void setClusterNum(final int i) {
		cluster_num = i;
	}

	public int getClusterNum() {
		return cluster_num;
	}

	public PeptideCollection getCluster(final int i) {
		return clusters.get(i);
	}

	/**
	 * @return unmodifiable view of clusters.
	 */
	public Map<Integer, PeptideCollection> getClusters() {
		return Collections.unmodifiableMap(clusters);
	}

	public int getNumElements() {
		return minPeptides.size() + minProteins.size();
	}

	@Override
	public int compareTo(final PeptideCollection pc) {
		final int x = getNumElements();
		final int y = pc.getNumElements();
		if (x > y) {
			return -1;
		}
		if (x < y) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Cluster " + cluster_num + " (" + getNumElements() + ')';
		// return "Cluster " + cluster_num;
	}

	public HashMap<String, Peptide> getMinPeptides() {
		return minPeptides;
	}

	public void setMinPeptides(final HashMap<String, Peptide> minPeptides) {
		this.minPeptides = minPeptides;
	}

	public HashMap<String, Protein> getMinProteins() {
		return minProteins;
	}

	public void setMinProteins(final HashMap<String, Protein> minProteins) {
		this.minProteins = minProteins;
	}

	public ArrayList<PeptideHit> getPeptideHits() {
		return peptideHits;
	}

	public HashSet<String> getExperimentSet() {
		return experimentSet;
	}

	private void categorizeProteins() {
		equivalents = new ArrayList<Protein>();
		subsets = new ArrayList<Protein>();
		supersets = new ArrayList<Protein>();
		subsumables = new ArrayList<Protein>();
		differentiables = new ArrayList<Protein>();
		discretes = new ArrayList<Protein>();
		countables = new ArrayList<Protein>();

		final ArrayList<Protein> sortProteins = new ArrayList<Protein>();
		sortProteins.addAll(minProteins.values());
		Collections.sort(sortProteins);
		for (final Protein p : sortProteins) {
			switch (p.getParsimonyType()) {
			case EQUIVALENT:
				equivalents.add(p);
				break;
			case SUBSET:
				subsets.add(p);
				break;
			case SUPERSET:
				supersets.add(p);
				break;
			case SUBSUMABLE:
				subsumables.add(p);
				break;
			case DIFFERENTIABLE:
				differentiables.add(p);
				break;
			case DISCRETE:
				discretes.add(p);
				break;
			}
		}

		countables.addAll(discretes);
		countables.addAll(differentiables);
		countables.addAll(supersets);
		countables.addAll(equivalents);
	}

	public ArrayList<Protein> getEquivalents() {
		if (equivalents == null) {
			categorizeProteins();
		}
		return equivalents;
	}

	public ArrayList<Protein> getSubsets() {
		if (subsets == null) {
			categorizeProteins();
		}
		return subsets;
	}

	public ArrayList<Protein> getSupersets() {
		if (supersets == null) {
			categorizeProteins();
		}
		return supersets;
	}

	public ArrayList<Protein> getSubsumables() {
		if (subsumables == null) {
			categorizeProteins();
		}
		return subsumables;
	}

	public ArrayList<Protein> getDifferentiables() {
		if (differentiables == null) {
			categorizeProteins();
		}
		return differentiables;
	}

	public ArrayList<Protein> getDiscretes() {
		if (discretes == null) {
			categorizeProteins();
		}
		return discretes;
	}

	public ArrayList<Protein> getCountables() {
		if (countables == null) {
			categorizeProteins();
		}
		return countables;
	}

	public Integer getCountablesCount() {
		if (countablesCount == null) {
			final HashSet<Integer> uniqueEquivs = new HashSet<Integer>();
			for (final Protein p : getCountables()) {
				uniqueEquivs.add(p.getEquivalentGroup());
			}
			countablesCount = uniqueEquivs.size();
		}
		return countablesCount;
	}

	public Set<String> getPeptideNames() {
		return minPeptides.keySet();
	}

	public Set<String> getProteinNames() {
		return minProteins.keySet();
	}
}
