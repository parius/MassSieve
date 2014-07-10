/*
 * Experiment.java
 *
 * Created on May 9, 2007, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.FileInformation;

import java.io.Serializable;
import java.util.List;

/**
 * This class is used to store experiment data for data serialization purposes,
 * it is not utilized otherwise. Both the filtered and unfiltered peptide
 * collections are stored to save the state (prefered or non-prefered) of the
 * proteins.
 * 
 * @author Douglas J. Slotta
 */
public class Experiment implements Serializable {

	public static final long serialVersionUID = 2L;
	private String name;
	private PeptideCollection pepCollection, pepCollectionOriginal;
	private List<FileInformation> fileInfos;
	private FilterSettings filterSettings;

	/** Creates a new instance of Experiment */
	public Experiment() {
	}

	/**
	 * Returns the filtered peptide collection
	 * 
	 * @return PeptideCollection
	 */
	public PeptideCollection getPepCollection() {
		return pepCollection;
	}

	/**
	 * Used to add a filtered PeptideCollection.
	 * 
	 * @param pepCollection
	 *            PeptideCollection to be added.
	 */
	public void setPepCollection(final PeptideCollection pepCollection) {
		this.pepCollection = pepCollection;
	}

	/**
	 * Stores the experiment metadata.
	 * 
	 * @return A list of file metadata for each file in the experiment.
	 */
	public List<FileInformation> getFileInfos() {
		return fileInfos;
	}

	/**
	 * Sets the file metadata for this experiment.
	 * 
	 * @param fileInfos
	 *            The list of metadata for each file in the experiment.
	 */
	public void setFileInfos(final List<FileInformation> fileInfos) {
		this.fileInfos = fileInfos;
	}

	/**
	 * Retrieves the filter settings for this experiment.
	 * 
	 * @return the filter settings.
	 */
	public FilterSettings getFilterSettings() {
		return filterSettings;
	}

	/**
	 * Stores the filter settings for this experiment.
	 * 
	 * @param filterSettings
	 *            The filter settings to be stored.
	 */
	public void setFilterSettings(final FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}

	/**
	 * Returns the name of the experiment.
	 * 
	 * @return the experiment name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the experiment.
	 * 
	 * @param name
	 *            The experiment name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Retrieves the unfiltered PeptideCollection.
	 * 
	 * @return The unfiltered PeptideCollection.
	 */
	public PeptideCollection getPepCollectionOriginal() {
		return pepCollectionOriginal;
	}

	/**
	 * Stores the unfiltered PeptideCollection
	 * 
	 * @param pepCollectionOriginal
	 *            The unfiltered PeptideCollection
	 */
	public void setPepCollectionOriginal(final PeptideCollection pepCollectionOriginal) {
		this.pepCollectionOriginal = pepCollectionOriginal;
	}
}
