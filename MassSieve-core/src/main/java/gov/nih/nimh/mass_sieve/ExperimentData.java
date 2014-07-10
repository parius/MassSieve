package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.FileInformation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds data about single experiment.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ExperimentData {

    private String name;
    private List<File> allFiles = new ArrayList<File>();
    private List<FileInformation> fileInfos = new ArrayList<FileInformation>();
    private PeptideCollection pepCollection;
    private PeptideCollection pepCollectionOriginal;
    private FilterSettings filterSettings;

    public ExperimentData(String name) {
        this.name = name;
        pepCollectionOriginal = new PeptideCollection();
    }

    public void addFile(File f) {
        allFiles.add(f);
    }

    public void addFileInfo(FileInformation fInfo) {
        fileInfos.add(fInfo);
    }

    public PeptideCollection getPepCollectionOriginal() {
        return pepCollectionOriginal;
    }

    public int getFilesNumber() {
        return allFiles.size();
    }

    public List<File> getFiles() {
        return allFiles;
    }

    public void clearFiles() {
        allFiles.clear();
        fileInfos.clear();
    }

    public List<FileInformation> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInformation> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public void setPepCollectionOriginal(PeptideCollection aPepCollectionOriginal) {
        this.pepCollectionOriginal = aPepCollectionOriginal;
    }

    public PeptideCollection getPepCollection() {
        return pepCollection;
    }

    public void setPepCollection(PeptideCollection aPepCollection) {
        this.pepCollection = aPepCollection;
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }

    public void setFilterSettings(FilterSettings aFilterSettings) {
        this.filterSettings = aFilterSettings;
    }

    public String getName() {
        return name;
    }
}
