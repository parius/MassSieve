/*
 * FilterSettings.java
 *
 * Created on May 9, 2007, 1:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import static gov.nih.nimh.mass_sieve.io.AnalysisProgramType.PEPXML;

import java.io.Serializable;

/**
 * Stores the filter setting, so they may be passed from dialog to the
 * experiment. Also serialized so they may be saved with the experiments.
 * 
 * @author Douglas J. Slotta
 */
public class FilterSettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7706997181668729191L;

	private double omssaCutoff, mascotCutoff, xtandemCutoff, sequestCutoff, peptideProphetCutoff;
	private String filterText;
	private boolean useIonIdent, useIndeterminates, usePepProphet, filterPeptides, filterProteins, filterCoverage;
	private int pHitCutoffCount, peptideCutoffCount, coverageCutoffAmount;
	private double estimatedFdrCutoff;

	/** Creates a new instance of FilterSettings */
	public FilterSettings() {
		omssaCutoff = 0.05;
		mascotCutoff = 0.05;
		xtandemCutoff = 0.05;
		sequestCutoff = 0.0;
		peptideProphetCutoff = 0.95;
		useIonIdent = true;
		filterText = "o+m+x+s+p";
		useIndeterminates = true;
		usePepProphet = false;
		filterPeptides = false;
		filterProteins = false;
		pHitCutoffCount = 1;
		peptideCutoffCount = 1;
		estimatedFdrCutoff = 0.1; // TODO
	}

	/**
	 * Used to duplicate the settings from another filterSettings object into
	 * the current object.
	 * 
	 * @param fromFilter
	 *            The filterSettings object from which to clone the settings.
	 */
	public void cloneFilterSettings(final FilterSettings fromFilter) {
		this.setOmssaCutoff(fromFilter.getOmssaCutoff());
		this.setMascotCutoff(fromFilter.getMascotCutoff());
		this.setXtandemCutoff(fromFilter.getXtandemCutoff());
		this.setSequestCutoff(fromFilter.getSequestCutoff());
		this.setUseIonIdent(fromFilter.getUseIonIdent());
		this.setFilterText(fromFilter.getFilterText());
		this.setUseIndeterminates(fromFilter.getUseIndeterminates());
		this.setUsePepProphet(fromFilter.getUsePepProphet());
		this.setFilterPeptides(fromFilter.getFilterPeptides());
		this.setFilterProteins(fromFilter.getFilterProteins());
		this.setPepHitCutoffCount(fromFilter.getPepHitCutoffCount());
		this.setPeptideCutoffCount(fromFilter.getPeptideCutoffCount());
		this.setEstimatedFdrCutoff(fromFilter.getEstimatedFdrCutoff());
	}

	/**
	 * Set the OMSSA cutoff expectation score.
	 * 
	 * @param s
	 *            OMSSA cutoff expectation score
	 */
	public void setOmssaCutoff(final String s) {
		omssaCutoff = Double.parseDouble(s);
	}

	/**
	 * Set the Mascot cutoff expectation score.
	 * 
	 * @param s
	 *            Mascot cutoff expectation score
	 */
	public void setMascotCutoff(final String s) {
		mascotCutoff = Double.parseDouble(s);
	}

	/**
	 * Set the X!Tandem cutoff expectation score.
	 * 
	 * @param s
	 *            X!Tandem cutoff expectation score
	 */
	public void setXtandemCutoff(final String s) {
		xtandemCutoff = Double.parseDouble(s);
	}

	/**
	 * Set the Sequest cutoff score.
	 * 
	 * @param s
	 *            Sequest cutoff score
	 */
	public void setSequestCutoff(final String s) {
		sequestCutoff = Double.parseDouble(s);
	}

	/**
	 * Set the Peptide Prophet cutoff score.
	 * 
	 * @param s
	 *            Peptide Prophet cutoff score
	 */
	public void setPeptideProphetCutoff(final String s) {
		peptideProphetCutoff = Double.parseDouble(s);
	}

	/**
	 * Set the OMSSA cutoff expectation score.
	 * 
	 * @param d
	 *            OMSSA cutoff expectation score
	 */
	public void setOmssaCutoff(final double d) {
		omssaCutoff = d;
	}

	/**
	 * Set the Mascot cutoff expectation score.
	 * 
	 * @param d
	 *            Mascot cutoff expectation score
	 */
	public void setMascotCutoff(final double d) {
		mascotCutoff = d;
	}

	/**
	 * Set the X!Tandem cutoff expectation score.
	 * 
	 * @param d
	 *            X!Tandem cutoff expectation score
	 */
	public void setXtandemCutoff(final double d) {
		xtandemCutoff = d;
	}

	/**
	 * Set the Sequest cutoff score.
	 * 
	 * @param d
	 *            Sequest cutoff score
	 */
	public void setSequestCutoff(final double d) {
		sequestCutoff = d;
	}

	/**
	 * For those who are more comfortable using a cutoff criteria of Ion Score
	 * greater than the identity rather than the use the expectation score. This
	 * is equivalent to an expectation cutoff of 0.05
	 * 
	 * @param b
	 *            True if the Ion>Identity criteria should be used for Mascot,
	 *            false otherwise
	 */
	public void setUseIonIdent(final boolean b) {
		useIonIdent = b;
		if (useIonIdent) {
			mascotCutoff = 0.05;
		}
	}

	/**
	 * Sets the string that describes the search engine result set intersection.
	 * 
	 * @param s
	 *            The search engine result set intersection description string.
	 */
	public void setFilterText(final String s) {
		filterText = s;
	}

	/**
	 * Retrieves the OMSSA cutoff expectation score.
	 * 
	 * @return The OMSSA cutoff expectation score.
	 */
	public double getOmssaCutoff() {
		return omssaCutoff;
	}

	/**
	 * Retrieves the Mascot cutoff expectation score.
	 * 
	 * @return The Mascot cutoff expectation score.
	 */
	public double getMascotCutoff() {
		return mascotCutoff;
	}

	/**
	 * Retrieves the X!Tandem cutoff expectation score.
	 * 
	 * @return The X!Tandem cutoff expectation score.
	 */
	public double getXtandemCutoff() {
		return xtandemCutoff;
	}

	/**
	 * Retrieves the Sequest cutoff score.
	 * 
	 * @return The Sequest cutoff score.
	 */
	public double getSequestCutoff() {
		return sequestCutoff;
	}

	/**
	 * Retrieves the string that describes the search engine result set
	 * intersection.
	 * 
	 * @return The string that describes the search engine result set
	 *         intersection.
	 */
	public String getFilterText() {
		return filterText;
	}

	/**
	 * For those who are more comfortable using a cutoff criteria of Ion Score
	 * greater than the identity rather than the use the expectation score. This
	 * is equivalent to an expectation cutoff of 0.05
	 * 
	 * @return True if the Ion>Identity criteria should be used for Mascot,
	 *         false otherwise
	 */
	public boolean getUseIonIdent() {
		return useIonIdent;
	}

	/**
	 * Used to determine if indeterminate peptide hits should be used
	 * 
	 * @param b
	 *            True if indeterminates should be used, false if not.
	 */
	public void setUseIndeterminates(final boolean b) {
		useIndeterminates = b;
	}

	/**
	 * Determines if the peptide hit count should be used as a filter
	 * 
	 * @param b
	 *            True if peptides should be filtered based on number of hits,
	 *            false otherwise
	 */
	public void setFilterPeptides(final boolean b) {
		filterPeptides = b;
	}

	/**
	 * Determines if the number of peptides per protein should be used as a
	 * filter
	 * 
	 * @param b
	 *            True if proteins should be filtered based on number of
	 *            peptides, false otherwise
	 */
	public void setFilterProteins(final boolean b) {
		filterProteins = b;
	}

	/**
	 * Determines if the percent coverage per protein should be used as a filter
	 * 
	 * @param b
	 *            True if proteins should be filtered based on peptide coverage,
	 *            false otherwise
	 */
	public void setFilterCoverage(final boolean b) {
		filterCoverage = b;
	}

	/**
	 * Sets the required number of PeptideHits per peptide.
	 * 
	 * @param i
	 *            the number of PeptideHits.
	 */
	public void setPepHitCutoffCount(final int i) {
		pHitCutoffCount = i;
	}

	/**
	 * Sets the required number of peptides per protein.
	 * 
	 * @param i
	 *            the number of Peptides.
	 */
	public void setPeptideCutoffCount(final int i) {
		peptideCutoffCount = i;
	}

	/**
	 * Sets the required protein coverage.
	 * 
	 * @param i
	 *            Protein coverage, integer between 0 and 100.
	 */
	public void setCoverageCutoffAmount(final int i) {
		coverageCutoffAmount = i;
	}

	/**
	 * Used to determine if indeterminate peptide hits should be used
	 * 
	 * @return True if indeterminates should be used, false if not.
	 */
	public boolean getUseIndeterminates() {
		return useIndeterminates;
	}

	/**
	 * Determines if the peptide hit count should be used as a filter
	 * 
	 * @return True if peptides should be filtered based on number of hits,
	 *         false otherwise
	 */
	public boolean getFilterPeptides() {
		return filterPeptides;
	}

	/**
	 * Determines if the number of peptides per protein should be used as a
	 * filter
	 * 
	 * @return True if proteins should be filtered based on number of peptides,
	 *         false otherwise
	 */
	public boolean getFilterProteins() {
		return filterProteins;
	}

	/**
	 * Determines if the percent coverage per protein should be used as a filter
	 * 
	 * @return True if proteins should be filtered based on peptide coverage,
	 *         false otherwise
	 */
	public boolean getFilterCoverage() {
		return filterCoverage;
	}

	/**
	 * Retrieves the required number of PeptideHits per peptide.
	 * 
	 * @return The number of PeptideHits.
	 */
	public int getPepHitCutoffCount() {
		return pHitCutoffCount;
	}

	/**
	 * Retrieves the required number of peptides per protein.
	 * 
	 * @return The number of Peptides.
	 */
	public int getPeptideCutoffCount() {
		return peptideCutoffCount;
	}

	/**
	 * Retrieves the required protein coverage.
	 * 
	 * @return Protein coverage, integer between 0 and 100.
	 */
	public int getCoverageCutoffAmount() {
		return coverageCutoffAmount;
	}

	public double getPeptideProphetCutoff() {
		return peptideProphetCutoff;
	}

	public void setPeptideProphetCutoff(final double d) {
		peptideProphetCutoff = d;
	}

	public boolean getUsePepProphet() {
		return usePepProphet;
	}

	public void setUsePepProphet(final boolean b) {
		usePepProphet = b;
	}

	/**
	 * Sets Expect cutoff
	 * 
	 * @param aExpectCutoff
	 *            double expect cutoff value to set
	 */
	private void setEstimatedFdrCutoff(final double aExpectCutoff) {
		estimatedFdrCutoff = aExpectCutoff;
	}

	/**
	 * Sets Expect cutoff as <code>String</code>
	 * 
	 * @param text
	 *            String representation of Expect cutoff
	 */
	public void setEstimatedFdrCutoff(final String text) {
		estimatedFdrCutoff = Double.valueOf(text);
	}

	/**
	 * Retrieves Expect cutoff value
	 * 
	 * @return Expect cutoff value (as <code>double</code>)
	 */
	public double getEstimatedFdrCutoff() {
		return estimatedFdrCutoff;
	}

	/**
	 * Test if a specified peptide conforms to a specified filter
	 * 
	 * @param peptideHit
	 *            peptide hit
	 * @return <code>true</code> if peptide hit conforms to filter,
	 *         <code>false</code> otherwise
	 */
	boolean conformsToFilter(final PeptideHit peptideHit) {
		if (peptideHit.getEstimatedFDR() > estimatedFdrCutoff) { // added by
																	// Alexey
																	// Tigarev
			return false;
		}
		if (getUsePepProphet() && peptideHit.isPepXML()) {
			return peptideHit.getPepProphet() >= peptideProphetCutoff;
		}
		switch (peptideHit.getSourceType()) {
		case MASCOT:
			if (getUseIonIdent()) {
				return peptideHit.getIonScore() >= peptideHit.getIdent();
			} else {
				return peptideHit.getExpect() <= mascotCutoff;
			}
		case OMSSA:
			return peptideHit.getExpect() <= omssaCutoff;
		case XTANDEM:
			return peptideHit.getExpect() <= xtandemCutoff;
		case SEQUEST:
			return peptideHit.getExpect() <= sequestCutoff;
		case PEPXML:
			return !peptideHit.CanGetPepProphet() || peptideHit.getPepProphet() >= peptideProphetCutoff;
		case UNKNOWN:
		default:
			return false; // TODO ?
		}
	}

	public boolean peptideHitConformsToFilter(final PeptideHit peptideHit) {
		if (peptideHit.getEstimatedFDR() > estimatedFdrCutoff) { // added by
																	// Alexey
																	// Tigarev
			return false;
		}
		if (peptideHit.isPepXML() && (getUsePepProphet() || peptideHit.getSourceType() == PEPXML)) {
			return !peptideHit.CanGetPepProphet() || peptideHit.getPepProphet() >= getPeptideProphetCutoff();
		}
		switch (peptideHit.getSourceType()) {
		case MASCOT:
			return peptideHit.getExpect() <= mascotCutoff;
		case OMSSA:
			return peptideHit.getExpect() <= omssaCutoff;
		case XTANDEM:
			return peptideHit.getExpect() <= xtandemCutoff;
		case SEQUEST:
			return peptideHit.getExpect() <= sequestCutoff;
		case PEPXML:
			// / ?????
		case UNKNOWN:
		default:
			return false;// TODO ?
		}
	}

}
