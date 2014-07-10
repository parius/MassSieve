/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.Peptide;
import gov.nih.nimh.mass_sieve.PeptideCollection;
import gov.nih.nimh.mass_sieve.PeptideProteinNameSet;
import gov.nih.nimh.mass_sieve.Protein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ExperimentPanelTreeView {

	public DefaultTreeModel getTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel) {
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(expPanel.getName() + " Overview");
		root.add(getPeptideHitsTree(pepCollection, expPanel));
		root.add(getPeptideTree(pepCollection, expPanel, true));
		root.add(getProteinTree(pepCollection, expPanel, true));
		root.add(getClusterTree(pepCollection, expPanel));
		root.add(getParsimonyTree(pepCollection, expPanel));
		return new DefaultTreeModel(root);
	}

	private DefaultMutableTreeNode getPeptideHitsTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel) {
		final PeptideHitListPanel panel = PeptideCollectionView.getView(pepCollection).getPeptideHitListPanel(expPanel);
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(panel);
		return root;
	}

	private DefaultMutableTreeNode getPeptideTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel, final boolean useListPanel) {
		DefaultMutableTreeNode root;
		if (useListPanel) {
			root = new DefaultMutableTreeNode(PeptideCollectionView.getView(pepCollection).getPeptideListPanel(expPanel));
		} else {
			final PeptideProteinNameSet pps = pepCollection.getPeptideProteinNameSet();
			pps.setName("Peptides (" + pps.getPeptides().size() + ")");
			root = new DefaultMutableTreeNode(pps);
		}
		DefaultMutableTreeNode child;
		final ArrayList<Peptide> sortPeptides = new ArrayList<Peptide>();
		sortPeptides.addAll(pepCollection.getMinPeptides().values());
		Collections.sort(sortPeptides);
		for (final Peptide pg : sortPeptides) {
			// child = pg.getTree();
			child = new DefaultMutableTreeNode(pg);
			root.add(child);
		}
		return root;
	}

	private DefaultMutableTreeNode getProteinTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel, final boolean useListPanel) {
		DefaultMutableTreeNode root;
		if (useListPanel) {
			final ProteinListPanel plp = PeptideCollectionView.getView(pepCollection).getProteinListPanel(expPanel);
			root = new DefaultMutableTreeNode(plp);
		} else {
			final PeptideProteinNameSet pps = pepCollection.getPeptideProteinNameSet();
			pps.setName("Proteins (" + pps.getProteins().size() + ")");
			root = new DefaultMutableTreeNode(pps);
		}
		DefaultMutableTreeNode child;
		final ArrayList<Protein> sortProteins = new ArrayList<Protein>();
		sortProteins.addAll(pepCollection.getMinProteins().values());
		Collections.sort(sortProteins);
		for (final Protein prot : sortProteins) {
			// child = prot.getTree(expPanel);
			child = new DefaultMutableTreeNode(prot);
			root.add(child);
		}
		return root;
	}

	private DefaultMutableTreeNode getClusterTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel) {
		final PeptideCollectionView view = PeptideCollectionView.getView(pepCollection);
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(view.getClusterListPanel(expPanel));
		DefaultMutableTreeNode child, grandchild;

		// Sort by cluster num
		final ArrayList<Integer> sortClusterNum = new ArrayList<Integer>();
		sortClusterNum.addAll(pepCollection.getClusters().keySet());
		Collections.sort(sortClusterNum);
		for (final Integer i : sortClusterNum) {
			final PeptideCollection pc = pepCollection.getCluster(i);
			child = new DefaultMutableTreeNode(pc);
			root.add(child);
			grandchild = getPeptideTree(pc, expPanel, false);
			child.add(grandchild);
			grandchild = getProteinTree(pc, expPanel, false);
			child.add(grandchild);
		}
		return root;
	}

	private DefaultMutableTreeNode getParsimonyTree(final PeptideCollection pepCollection, final ExperimentPanel expPanel) {
		final PeptideCollectionView view = PeptideCollectionView.getView(pepCollection);
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode(view.getParsimonyListPanel(expPanel));
		DefaultMutableTreeNode child;

		// Discrete
		PeptideProteinNameSet pps = ProteinListToPPNSet(pepCollection.getDiscretes());
		pps.setName("Discrete (" + pepCollection.getDiscretes().size() + ")");
		child = new DefaultMutableTreeNode(pps);
		for (final Protein p : pepCollection.getDiscretes()) {
			child.add(new DefaultMutableTreeNode(p));
		}
		root.add(child);

		// Differentiable
		pps = ProteinListToPPNSet(pepCollection.getDifferentiables());
		pps.setName("Differentiable (" + pepCollection.getDifferentiables().size() + ")");
		child = new DefaultMutableTreeNode(pps);
		for (final Protein p : pepCollection.getDifferentiables()) {
			child.add(new DefaultMutableTreeNode(p));
		}
		root.add(child);

		// Superset
		child = ProListToEquivTree(pepCollection.getSupersets(), "Superset");
		root.add(child);

		// Subsumable
		child = ProListToEquivTree(pepCollection.getSubsumables(), "Subsumable");
		root.add(child);

		// Subset
		child = ProListToEquivTree(pepCollection.getSubsets(), "Subset");
		root.add(child);

		// Equivalent
		child = ProListToEquivTree(pepCollection.getEquivalents(), "Equivalent");
		root.add(child);
		return root;
	}

	private DefaultMutableTreeNode ProListToEquivTree(final ArrayList<Protein> proList, final String name) {
		DefaultMutableTreeNode root, child;
		int size = 0;
		final PeptideProteinNameSet pps = ProteinListToPPNSet(proList);

		root = new DefaultMutableTreeNode(pps);
		final HashSet<String> usedProteins = new HashSet<String>();
		for (final Protein p : proList) {
			if (!usedProteins.contains(p.getName())) {
				if (p.getEquivalent().size() > 0) {
					final ArrayList<Protein> equivList = new ArrayList<Protein>();
					equivList.add(p);
					equivList.addAll(p.getEquivalent());
					final PeptideProteinNameSet ppsEquiv = ProteinListToPPNSet(equivList);
					ppsEquiv.setName(p.getName() + " Group (" + equivList.size() + ")");
					child = new DefaultMutableTreeNode(ppsEquiv);
					size++;
					for (final Protein ps : equivList) {
						child.add(new DefaultMutableTreeNode(ps));
						usedProteins.add(ps.getName());
					}
					root.add(child);
				} else {
					root.add(new DefaultMutableTreeNode(p));
					usedProteins.add(p.getName());
					size++;
				}
			}
		}
		pps.setName(name + " (" + size + ")");
		return root;
	}

	private PeptideProteinNameSet ProteinListToPPNSet(final ArrayList<Protein> proteins) {
		final Set<String> protNames = new HashSet<String>();
		final Set<String> pepNames = new HashSet<String>();
		for (final Protein p : proteins) {
			protNames.add(p.getName());
			pepNames.addAll(p.getPeptides());
		}
		final PeptideProteinNameSet pps = new PeptideProteinNameSet();
		pps.setPeptides(pepNames);
		pps.setProteins(protNames);
		return pps;
	}
}
