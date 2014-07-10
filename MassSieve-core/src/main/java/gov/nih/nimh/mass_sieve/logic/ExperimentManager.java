package gov.nih.nimh.mass_sieve.logic;

import gov.nih.nimh.mass_sieve.Experiment;
import gov.nih.nimh.mass_sieve.ExperimentData;
import gov.nih.nimh.mass_sieve.ExportProteinType;
import gov.nih.nimh.mass_sieve.FilterSettings;
import gov.nih.nimh.mass_sieve.Peptide;
import gov.nih.nimh.mass_sieve.PeptideCollection;
import gov.nih.nimh.mass_sieve.PeptideHit;
import gov.nih.nimh.mass_sieve.Protein;
import gov.nih.nimh.mass_sieve.ProteinDB;
import gov.nih.nimh.mass_sieve.ProteinInfo;
import gov.nih.nimh.mass_sieve.io.CSVWriter;
import gov.nih.nimh.mass_sieve.io.FileInformation;
import gov.nih.nimh.mass_sieve.io.ParseFile;
import gov.nih.nimh.mass_sieve.io.SetLexer;
import gov.nih.nimh.mass_sieve.io.SetParser;
import gov.nih.nimh.mass_sieve.tasks.DeterminedTaskListener;
import gov.nih.nimh.mass_sieve.tasks.InputStreamObserver;
import gov.nih.nimh.mass_sieve.tasks.InputStreamProgressObserver;
import gov.nih.nimh.mass_sieve.tasks.MultiTaskListener;
import gov.nih.nimh.mass_sieve.tasks.ObserverableInputStream;
import gov.nih.nimh.mass_sieve.util.IOUtils;
import gov.nih.nimh.mass_sieve.util.LogStub;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.biojava.bio.BioException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * Web controller in future.
 * 
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ExperimentManager {

	private final ProteinDB proteinDB = new ProteinDB();

	/**
	 * Saves a set of experiments to a given file.
	 * 
	 * @throws DataStoreException
	 *             if error occurs while saving bundle to storage.
	 */
	public void saveExperimentsBundle(final ExperimentsBundle eb, final File f) throws DataStoreException {
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));

			final List<Experiment> experiments = eb.getExperiments();
			os.writeInt(experiments.size());

			for (final Experiment exp : experiments) {
				os.writeObject(exp);
			}

			os.writeObject(eb.getProteinDB().getMap());

		} catch (final FileNotFoundException ex) {
			LogStub.error(ex);
			final String msg = "Unable to save file";
			throw new DataStoreException(msg, ex);
		} catch (final IOException ex) {
			LogStub.error(ex);
			final String msg = "Unable to save file";
			throw new DataStoreException(msg, ex);
		} finally {
			IOUtils.closeSafe(os);
		}
	}

	/**
	 * Loads set of experiments from a given file.
	 * 
	 * @throws DataStoreException
	 */
	public ExperimentsBundle loadExperimentsBundle(final File f) throws DataStoreException {
		final List<Experiment> exps = new ArrayList<Experiment>();
		Map<String, ProteinInfo> proteinDBMap;

		ObjectInputStream ois = null;
		try {
			Object obj;
			InputStream in;
			if (f.getPath().endsWith(".gz")) {
				in = new GZIPInputStream(new FileInputStream(f));
			} else {
				in = new FileInputStream(f);
			}

			ois = new ObjectInputStream(new BufferedInputStream(in));

			final int expCount = ois.readInt();
			if (expCount == 1) {
				System.out.println("File contains " + expCount + " experiment");
			} else {
				System.out.println("File contains " + expCount + " experiments");
			}

			for (int i = 0; i < expCount; i++) {
				obj = ois.readObject();
				final Experiment exp = (Experiment) obj;
				exps.add(exp);
			}

			obj = ois.readObject();
			proteinDBMap = (Map<String, ProteinInfo>) obj;
		} catch (final FileNotFoundException ex) {
			LogStub.error(ex);
			final String msg = "Unable to open file";
			throw new DataStoreException(msg, ex);
		} catch (final ClassNotFoundException ex) {
			LogStub.error(ex);
			final String msg = "File format does not match current MassSieve version";
			throw new DataStoreException(msg, ex);
		} catch (final IOException ex) {
			LogStub.error(ex);
			final String msg = "Unable to open file";
			throw new DataStoreException(msg, ex);
		} finally {
			IOUtils.closeSafe(ois);
		}

		addToProteinDatabase(proteinDBMap.values());
		final ExperimentsBundle eb = new ExperimentsBundle(exps, proteinDB);
		// assign proteinDB to every experiment
		for (final Experiment e : exps) {
			final PeptideCollection pepCol = e.getPepCollection();
			final HashMap<String, Protein> minProteins = pepCol.getMinProteins();
			for (final Protein pro : minProteins.values()) {
				pro.setProteinDB(proteinDB);
			}
		}
		return eb;
	}

	/**
	 * Exports proteins, peptides, peptide hits, protein-peptide relationships
	 * and equivalent proteins.
	 * 
	 * @param file
	 *            File to export data.
	 * @param pepCollection
	 *            Source for exported data.
	 * @return result of the operation.
	 */
	public ActionResponse exportDatabase(final File file, final PeptideCollection pepCollection) {
		Writer fw = null;
		try {
			fw = new BufferedWriter(new FileWriter(file));
			// export proteins
			fw.write("# Proteins\n");
			fw.write(Protein.toTabStringHeader());
			for (final Protein pro : pepCollection.getMinProteins().values()) {
				fw.write(pro.toTabString());
			}
			// export peptides
			fw.write("\n\n# Peptides\n");
			fw.write(Peptide.toTabStringHeader());
			for (final Peptide pep : pepCollection.getMinPeptides().values()) {
				fw.write(pep.toTabString());
			}
			// export peptide hits
			fw.write("\n\n# Peptide Hits\n");
			fw.write(PeptideHit.toTabStringHeader());
			for (final PeptideHit pepHit : pepCollection.getPeptideHits()) {
				fw.write(pepHit.toTabString());
			}
			// export protein-peptide relationships
			fw.write("\n\n# ProteinToPeptides\n");
			fw.write("Protein\tPeptide\n");
			for (final Protein pro : pepCollection.getMinProteins().values()) {
				for (final Peptide pep : pro.getAllPeptides()) {
					fw.write(pro.getName() + "\t" + pep.getSequence() + "\n");
				}
			}
			// export equivalent proteins
			fw.write("\n\n# EquivalentProteins\n");
			fw.write("Protein\tProtein\n");
			for (final Protein pro1 : pepCollection.getMinProteins().values()) {
				for (final Protein pro2 : pro1.getEquivalent()) {
					fw.write(pro1.getName() + "\t" + pro2.getName() + "\n");
				}
			}
		} catch (final IOException ex) {
			LogStub.error(ex);
			return new ActionResponse(ActionResult.FAILED, "Failed to export database.");
		} finally {
			IOUtils.closeSafe(fw);
		}
		return ActionResponse.SUCCESS;
	}

	public ActionResponse exportResults(final File file, final PeptideCollection pepCollection, final ExportProteinType type) {
		Writer fw = null;
		try {
			fw = new BufferedWriter(new FileWriter(file));
			// Output column headers if any.
			if (ExportProteinType.PREFERRED == type) {
				fw.write("Proteins\tPeptides\tScans\n");
			} else {
				fw.write("Proteins\tParsimony\tPeptides\tScans\n");
			}
			for (final Protein pro : pepCollection.getMinProteins().values()) {
				if ((ExportProteinType.PREFERRED == type) && !pro.isMostEquivalent()) {
					continue; // preferred proteins only
				}
				if (ExportProteinType.PREFERRED == type) {
					fw.write(pro.getName());
				}
				for (final Peptide pep : pro.getAllPeptides()) {
					if (ExportProteinType.PREFERRED == type) {
						fw.write("\t" + pep.getSequence() + "\t" + pep.getScanList(false) + "\n");
					} else {
						fw.write(pro.getName() + "\t" + pro.getParsimonyType() + "\t" + pep.getSequence() + "\t" + pep.getScanList(false) + "\n");
					}
				}
			}
		} catch (final IOException ex) {
			LogStub.error(ex);
			return new ActionResponse(ActionResult.FAILED, "Failed to export results.");
		} finally {
			IOUtils.closeSafe(fw);
		}
		return ActionResponse.SUCCESS;
	}

	public ActionResponse exportSeqDB(final File file, final Set<String> minProteins) {
		int seqCount = 0;
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));

			for (final String prot : minProteins) {
				final ProteinInfo info = proteinDB.get(prot);
				final RichSequence rs = info.getRichSequence();
				if (rs != null && (rs.length() > 0)) {
					RichSequence.IOTools.writeFasta(os, rs, null);
					seqCount++;
				}
			}
		} catch (final IOException ex) {
			LogStub.error(ex);
			return new ActionResponse(ActionResult.FAILED, "Unable to export sequences");
		} finally {
			IOUtils.closeSafe(os);
		}

		return new ActionResponse(ActionResult.SUCCESS, "Exported " + seqCount + " sequences.");
	}

	/**
	 * Asynchroniously adds sequence db files to current experiment. Notifies
	 * about current progress via listener.
	 */
	public void addSeqDBfiles(final File[] files, final MultiTaskListener listener) {
		// TODO: use thread pool
		new Thread(new Runnable() {
			/*
			 * TODO: use RichSequence.IOTools.readFile to guess format, support
			 * more formats, support genbank directly
			 */

			@Override
			public void run() {
				int seqCount = 0;
				// int dupCount = 0;
				for (final File f : files) {
					System.err.println("Parsing " + f.getName() + " as the sequence database");
					// XXX: >2gb files?
					listener.onTaskStarted(f.getName(), (int) f.length());
					try {
						// Not sure if your input is EMBL or Genbank? Load them
						// both here.^M
						// Class.forName("org.biojavax.bio.seq.io.EMBLFormat");
						// Class.forName("org.biojavax.bio.seq.io.UniProtFormat");
						// Class.forName("org.biojavax.bio.seq.io.UniProtXMLFormat");
						// Class.forName("org.biojavax.bio.seq.io.GenbankFormat");
						Class.forName("org.biojavax.bio.seq.io.FastaFormat");

						// Now let BioJavaX guess which format you actually
						// should use (using the default namespace)
						final Namespace ns = RichObjectFactory.getDefaultNamespace();

						final InputStreamObserver inputObserver = new InputStreamProgressObserver(listener);
						final ObserverableInputStream pin = new ObserverableInputStream(new FileInputStream(f), inputObserver);
						final BufferedInputStream bin = new BufferedInputStream(pin);
						final RichSequenceIterator seqItr = RichSequence.IOTools.readStream(bin, ns);
						while (seqItr.hasNext()) {
							try {
								final RichSequence seq = seqItr.nextRichSequence();
								final String seqName = seq.getName();
								// System.out.println(seqName);
								if (proteinDB.contains(seqName)) {
									final ProteinInfo pInfo = proteinDB.get(seqName);
									pInfo.updateFromRichSequence(seq);
									// RichSequence daSeq =
									// proteinDB.get(seqName);
									// if (daSeq != null) {
									// if (daSeq.getInternalSymbolList() !=
									// SymbolList.EMPTY_LIST ) dupCount++;
									// }
									// proteinDB.put(seqName, seq);
									System.err.println(seqName);
									seqCount++;
								}
							} catch (final BioException ex) {
								if (ex.getCause() != null) {
									throw ex.getCause();
								}
								LogStub.error(ex);
							}
						}
						System.err.println(f.getName() + " sequence database read complete!");
					} catch (final InterruptedIOException ex) {
						listener.onTaskCancelled();
						System.err.println("Fasta import canceled by user");
						break;
					} catch (final IOException ex) {
						listener.onTaskFailed();
						LogStub.error(ex);
					} catch (final ClassNotFoundException ex) {
						LogStub.error(ex);
					} catch (final Throwable ex) {
						LogStub.error(ex);
					}
					listener.onTaskFinished();
				}
				listener.onMultiTaskFinished();
			}
		}).start();
	}

	public ExperimentData createNewExperiment(final String expName) {
		return new ExperimentData(expName);
	}

	public List<ProteinInfo> addFileToExperiment(final ExperimentData expData, final File f, final InputStreamObserver inputObserver, final DeterminedTaskListener parseListener) {
		final List<ProteinInfo> result = new ArrayList<ProteinInfo>();

		final String expName = expData.getName();
		expData.addFile(f);
		final String filename = f.getName();

		final ParseFile pf = new ParseFile(f, inputObserver, parseListener);
		final Set<String> acceptedProteins = processPeptideCollection(expName, filename, pf.getPeptideHits(), expData);
		final Map<String, ProteinInfo> pDB = pf.getProteinDB();
		for (final String pName : pDB.keySet()) {
			if (acceptedProteins.contains(pName)) {
				result.add(pDB.get(pName));
			}
		}
		final FileInformation fInfo = pf.getFileInformation();
		fInfo.setExperiment(expName);
		expData.addFileInfo(fInfo);

		addToProteinDatabase(result);
		return result;
	}

	private Set<String> processPeptideCollection(final String expName, final String filename, final List<PeptideHit> peptideHits, final ExperimentData expData) {
		final Set<String> acceptedProteins = new HashSet<String>();
		for (final PeptideHit p : peptideHits) {
			p.setExperiment(expName);
			p.setSourceFile(filename);
			if (expData.getFilterSettings().peptideHitConformsToFilter(p)) {
				expData.getPepCollectionOriginal().addPeptideHit(p);
				acceptedProteins.addAll(p.getProteinNames());
			}
		}
		return acceptedProteins;
	}

	public PeptideCollection filterBySearchProgram(final PeptideCollection pc, final String filterText) {
		PeptideCollection result;
		final StringReader setDescription = new StringReader(filterText);
		final SetLexer lexer = new SetLexer(setDescription);
		final SetParser parser = new SetParser(lexer);
		parser.setPeptideCollection(pc);
		try {
			result = parser.expr();
			result.updatePeptideHits();
			return result;
		} catch (final TokenStreamException ex) {
			LogStub.error(ex);
		} catch (final RecognitionException ex) {
			LogStub.error(ex);
		}
		return new PeptideCollection();
	}

	public void recomputeCutoff(final ExperimentData expData) {
		final FilterSettings filterSettings = expData.getFilterSettings();
		PeptideCollection pepFiltered;
		pepFiltered = expData.getPepCollectionOriginal().getCutoffCollection(filterSettings);
		pepFiltered.computeIndeterminates();
		if (!filterSettings.getUseIndeterminates()) {
			pepFiltered = pepFiltered.getNonIndeterminents();
		}
		pepFiltered = filterBySearchProgram(pepFiltered, expData.getFilterSettings().getFilterText());
		if (filterSettings.getFilterPeptides()) {
			pepFiltered = pepFiltered.getPeptidesByHits(filterSettings.getPepHitCutoffCount());
		}
		// TODO handle additional cutoff filter
		pepFiltered.createProteinList(proteinDB);
		if (filterSettings.getFilterProteins()) {
			final int numPeps = filterSettings.getPeptideCutoffCount();
			pepFiltered = pepFiltered.filterByPeptidePerProtein(proteinDB, numPeps);
		}
		if (filterSettings.getFilterCoverage()) {
			pepFiltered.updateClusters();
			final int minCoverage = filterSettings.getCoverageCutoffAmount();
			pepFiltered = pepFiltered.filterByProteinCoverage(proteinDB, minCoverage);
		}
		pepFiltered.updateClusters();
		expData.setPepCollection(pepFiltered);
	}

	/**
	 * Creates new experiment data object that could be saved as serialized
	 * object.
	 */
	public Experiment getPersistentExperiment(final ExperimentData expData) {
		final Experiment exp = new Experiment();
		exp.setName(expData.getName());
		exp.setFileInfos(expData.getFileInfos());
		exp.setFilterSettings(expData.getFilterSettings());
		exp.setPepCollection(expData.getPepCollection());
		exp.setPepCollectionOriginal(expData.getPepCollectionOriginal());
		return exp;
	}

	public void addToProteinDatabase(final ProteinInfo pInfo) {
		final String pName = pInfo.getName();
		if (!proteinDB.contains(pName)) {
			proteinDB.add(pInfo);
		} else {
			final ProteinInfo pInfoOld = proteinDB.get(pName);
			pInfoOld.update(pInfo);
		}
	}

	public void addToProteinDatabase(final Collection<ProteinInfo> proteins) {
		for (final ProteinInfo info : proteins) {
			addToProteinDatabase(info);
		}
	}

	public ProteinDB getProteinDatabase() {
		return proteinDB;
	}

	public void saveExperiment(final ExperimentData exp, final File file) throws DataStoreException {
		final Experiment persist = getPersistentExperiment(exp);
		saveExperiment(persist, file);
	}

	public void saveExperiment(final Experiment exp, final File file) throws DataStoreException {
		final List<Experiment> persistList = new ArrayList<Experiment>();
		persistList.add(exp);

		final ExperimentsBundle bundle = new ExperimentsBundle(persistList, proteinDB);
		saveExperimentsBundle(bundle, file);
	}

	public CompareExperimentDiffData compareExperimentDiff(final List<ExperimentsBundle> bundles) {
		final List<String> experimentsNames = new ArrayList<String>();
		final HashSet<String> uniqueProteins = new HashSet<String>();
		final List<Experiment> experiments = new ArrayList<Experiment>();
		for (final ExperimentsBundle eb : bundles) {
			final List<Experiment> exps = eb.getExperiments();
			for (final Experiment e : exps) {
				final String expName = e.getName();
				experimentsNames.add(expName);
				uniqueProteins.addAll(e.getPepCollection().getMinProteins().keySet());
			}
			experiments.addAll(exps);
		}
		final ArrayList<String> proteins = new ArrayList<String>(uniqueProteins);
		Collections.sort(proteins);

		// prepare header data
		final List<String> columnNames = new ArrayList<String>();
		columnNames.add("Protein Name");
		for (final String eName : experimentsNames) {
			columnNames.add(eName + " Parsimony");
			columnNames.add(eName + " Cover");
			columnNames.add(eName + " %Cover");
			columnNames.add(eName + " Unique Peps");
			columnNames.add(eName + " PepHits");
		}
		// end prepare header data

		return new CompareExperimentDiffData(columnNames, proteins, experiments);
	}

	public void exportCompareExperimentDiffData(final String filename, final CompareExperimentDiffData cedd) throws DataStoreException {
		final File cvsFile = new File(filename);
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(cvsFile);

			writer.write(cedd.getColumnNames());

			final int totalRows = cedd.getRowCount();
			for (int i = 0; i < totalRows; i++) {
				final Object[] row = cedd.getRow(i);
				writer.write(row);
			}
		} catch (final IOException e) {
			throw new DataStoreException("Cannot write csv file:" + filename, e);
		} finally {
			IOUtils.closeSafe(writer);
		}
	}

	public Experiment compareExperimentsParsimonies(final String name, final List<Experiment> expsToCompare) {
		final ArrayList<PeptideHit> allHits = new ArrayList<PeptideHit>();
		final ArrayList<FileInformation> fInfos = new ArrayList<FileInformation>();

		double maxMascot = Double.MIN_VALUE;
		double maxOmssa = Double.MIN_VALUE;
		double maxXtandem = Double.MIN_VALUE;
		for (final Experiment exp : expsToCompare) {
			if (exp.getFilterSettings().getMascotCutoff() > maxMascot) {
				maxMascot = exp.getFilterSettings().getMascotCutoff();
			}
			if (exp.getFilterSettings().getOmssaCutoff() > maxOmssa) {
				maxOmssa = exp.getFilterSettings().getOmssaCutoff();
			}
			if (exp.getFilterSettings().getXtandemCutoff() > maxXtandem) {
				maxXtandem = exp.getFilterSettings().getXtandemCutoff();
			}
			allHits.addAll(exp.getPepCollection().getPeptideHits());
			fInfos.addAll(exp.getFileInfos());
		}

		final ExperimentData comparisonData = new ExperimentData(name);
		final FilterSettings fs = new FilterSettings();
		fs.setUseIonIdent(false);
		fs.setMascotCutoff(maxMascot);
		fs.setOmssaCutoff(maxOmssa);
		fs.setXtandemCutoff(maxXtandem);

		comparisonData.setFilterSettings(fs);
		comparisonData.setFileInfos(fInfos);

		for (final PeptideHit p : allHits) {
			comparisonData.getPepCollectionOriginal().addPeptideHit(p);
		}
		recomputeCutoff(comparisonData);

		return getPersistentExperiment(comparisonData);
	}
}
