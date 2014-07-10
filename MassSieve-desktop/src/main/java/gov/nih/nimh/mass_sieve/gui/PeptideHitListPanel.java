/*
 * PeptideHitListPanel.java
 *
 * Created on May 13, 2007, 3:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.gui;

import gov.nih.nimh.mass_sieve.PeptideHit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 
 * @author slotta
 */
public class PeptideHitListPanel extends ListPanel {
	private static final FieldColumn modSequence = new FieldColumn("modSequence", "Modified Sequence");
	private static final FieldColumn analysis = new FieldColumn("sourceType", "Analysis");
	private static final FieldColumn scan = new FieldColumn("scanNum", "Scan");
	private static final FieldColumn query = new FieldColumn("queryNum", "Query");
	private static final FieldColumn indeterminate = new FieldColumn("indeterminate", "Indet");
	private static final FieldColumn expect = new FieldColumn("expect", "Expect");
	private static final FieldColumn estFDR = new FieldColumn("estimatedFDR", "Est.FDR");
	private static final FieldColumn pepProphet = new FieldColumn("pepProphet", "Pep Proph");
	private static final FieldColumn ionScore = new FieldColumn("ionScore", "ION");
	private static final FieldColumn xcorr = new FieldColumn("xcorr", "Xcorr");
	private static final FieldColumn expMass = new FieldColumn("expMass", "m/z");
	private static final FieldColumn expNeutralMass = new FieldColumn("expNeutralMass", "Exp. Mass");
	private static final FieldColumn diffMass = new FieldColumn("diffMass", '\u0394' + "Mass");
	private static final FieldColumn charge = new FieldColumn("Z", "Charge");
	private static final FieldColumn indet = new FieldColumn("ident", "Ident");
	private static final FieldColumn experiment = new FieldColumn("experiment", "Experiment");
	private static final FieldColumn sourceFile = new FieldColumn("sourceFile", "Source File");
	private static final FieldColumn rawFile = new FieldColumn("rawFile", "Raw File");
	private static final FieldColumn proteinName = new FieldColumn("proteinName", "Protein");
	private static final FieldColumn start = new FieldColumn("start", "Start");
	private static final FieldColumn end = new FieldColumn("end", "End");
	private static final FieldColumn peptide = new FieldColumn("sequence", "Peptide");

	/** Creates a new instance of PeptideHitListPanel */
	public PeptideHitListPanel(final ExperimentPanel ePanel) {
		super(ePanel);
	}

	public void addPeptideHitList(final HashSet<PeptideHit> list) {
		final TableFormatBuilder builder = new TableFormatBuilder();

		builder.addColumns(modSequence, analysis, scan, query, indeterminate, expect, estFDR, pepProphet, ionScore, indet, xcorr, expMass, expNeutralMass, diffMass, charge);

		builder.addColumns(proteinName, start, end);

		builder.addColumns(experiment, sourceFile, rawFile);

		pTableFormat = builder.createTableFormat();
		this.addList(list);
	}

	public void addPeptideHitList(final List<PeptideHit> list) {
		final TableFormatBuilder builder = new TableFormatBuilder();

		builder.addColumns(modSequence, analysis, scan, query, indeterminate, expect, estFDR, pepProphet, ionScore, indet, xcorr, expMass, expNeutralMass, diffMass, charge);

		builder.addColumns(experiment, sourceFile, rawFile);

		pTableFormat = builder.createTableFormat();
		this.addList(list);
	}

	public void addProteinPeptideHitList(final ArrayList<PeptideHit> list) {
		final TableFormatBuilder builder = new TableFormatBuilder();

		builder.addColumns(peptide);

		builder.addColumns(modSequence, analysis, scan, query, indeterminate, expect, estFDR, pepProphet, ionScore, indet, xcorr, expMass, expNeutralMass, diffMass, charge);

		builder.addColumns(experiment, sourceFile, rawFile);

		pTableFormat = builder.createTableFormat();
		this.addList(list);
	}

}
