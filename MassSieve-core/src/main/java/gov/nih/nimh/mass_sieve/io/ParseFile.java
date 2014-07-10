/*
 * ParseFile.java
 *
 * Created on February 24, 2006, 9:11 AM
 *
 * @author Douglas Slotta
 */
package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.PeptideHit;
import gov.nih.nimh.mass_sieve.ProteinInfo;
import gov.nih.nimh.mass_sieve.tasks.DeterminedTaskListener;
import gov.nih.nimh.mass_sieve.tasks.InputStreamObserver;
import gov.nih.nimh.mass_sieve.tasks.ObserverableInputStream;
import gov.nih.nimh.mass_sieve.tasks.Task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;

public class ParseFile {

	private XMLReader xmlReader;
	private AnalysisHandler handler;

	// public ParseFile(String f, Component p) {
	public ParseFile(final File f, final InputStreamObserver taskObserver, final DeterminedTaskListener parseListener) {
		String filename = f.getPath();

		InputStream in = null;

		if (filename.endsWith(".gz")) {
			filename = filename.substring(0, filename.length() - 3);

			try {
				in = new GZIPInputStream(new FileInputStream(f));
			} catch (final FileNotFoundException e) {
				// TODO process
			} catch (final IOException e) {
				// TODO process
			}
		}

		try {
			if (null == in) {
				in = new FileInputStream(filename);
			}

			if (filename.endsWith(".dat")) {
				System.err.println("Parsing " + filename + " as a Mascot dat file");
				final mascotDatHandler mdh = new mascotDatHandler(filename);
				final MascotDatfile mdf = mdh.mascotDatRead(taskObserver);

				final Task task = new Task(3, parseListener);
				mdh.mascotDatParse(mdf, task);
				handler = mdh;
			} else if (filename.endsWith(".sqt")) {
				System.err.println("Parsing " + filename + " as a Sequest sqt file");
				final sequestSqtHandler sh = new sequestSqtHandler(filename);
				sh.sequestSqtParse();
				handler = sh;
			} else { // Maybe it is an XML file?

				final AnalysisProgramType filetype = checkXMLFiletype(f);

				switch (filetype) {
				case MASCOT:
					System.err.println(filename + " is a Mascot XML file, this is not supported");
					// handler = new mascotXMLHandler(filename);
					// XMLParse();
					break;
				case OMSSA:
					System.err.println("Parsing " + filename + " as an OMSSA file");
					handler = new omssaHandler(filename);
					XMLParse(taskObserver, in);
					((omssaHandler) handler).scaleMasses();
					break;
				case XTANDEM:
					System.err.println("Parsing " + filename + " as a X!Tandem file");
					handler = new xtandemHandler(filename);
					XMLParse(taskObserver, in);
					break;
				case PEPXML:
					System.err.println("Parsing " + filename + " as a PepXML file");
					handler = new pepXMLHandler(filename);
					XMLParse(taskObserver, in);
					break;
				case UNKNOWN:
					System.err.println("Unable to determine filetype for: " + filename);
					break;
				}
			}
		} catch (final FileNotFoundException e) {
			// TODO handle
		} finally {
			IOUtils.closeQuietly(in);

		}

	}

	public ArrayList<PeptideHit> getPeptideHits() {
		return handler.getPeptideHits();
	}

	public HashMap<String, ProteinInfo> getProteinDB() {
		return handler.getProteinDB();
	}

	public FileInformation getFileInformation() {
		return handler.getFileInformation();
	}

	protected int scanFilenameToScanNumber(final String fn) {
		final StringBuilder sb = new StringBuilder(fn);
		int val, start, stop;
		start = sb.indexOf(".") + 1;
		stop = sb.substring(start).indexOf(".") + start;
		val = Integer.parseInt(sb.substring(start, stop));
		return val;
	}

	private void XMLParse(final InputStreamObserver taskObserver, final InputStream in) {
		try {
			// Parse the input
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(handler);

			final ObserverableInputStream ois = new ObserverableInputStream(in, taskObserver);
			final BufferedInputStream bin = new BufferedInputStream(ois);
			xmlReader.parse(new InputSource(bin));
		} catch (final SAXException t) {
			// filetype = AnalysisProgramType.UNKNOWN;
		} catch (final Throwable t) {
			t.printStackTrace();
		}
	}

	AnalysisProgramType checkXMLFiletype(final File f) {
		final CheckTypeHandler check_handler = new CheckTypeHandler();

		// Use the default (non-validating) parser
		try {

			InputStream in = null;
			if (f.getPath().endsWith(".gz")) {
				in = new GZIPInputStream(new FileInputStream(f));
			} else {
				in = new FileInputStream(f);
			}

			// Parse the input
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(check_handler);
			xmlReader.parse(new InputSource(in));
		} catch (final TypeFoundException t) {
			return t.getFileType();
		} catch (final SAXException t) {
			return AnalysisProgramType.UNKNOWN;
		} catch (final Throwable t) {
			t.printStackTrace();
		}
		return AnalysisProgramType.UNKNOWN;
	}

}

class CheckTypeHandler extends DefaultHandler {

	@Override
	public void startElement(final String namespaceURI, final String sName, final String qName, final Attributes attrs) throws TypeFoundException {
		if (sName.equals("MSSearch") || sName.equals("MSResponse")) {
			throw new TypeFoundException(AnalysisProgramType.OMSSA);
		}
		if (sName.equals("bioml")) {
			throw new TypeFoundException(AnalysisProgramType.XTANDEM);
		}
		if (sName.equals("msms_pipeline_analysis")) {
			throw new TypeFoundException(AnalysisProgramType.PEPXML);
		}
		if (sName.equals("mascot_search_results")) {
			throw new TypeFoundException(AnalysisProgramType.MASCOT);
		}
	}

	@Override
	public void endDocument() throws TypeFoundException {
		throw new TypeFoundException(AnalysisProgramType.UNKNOWN);
	}
}

class TypeFoundException extends SAXException {

	public AnalysisProgramType type;

	TypeFoundException(final AnalysisProgramType ftype) {
		type = ftype;
	}

	public AnalysisProgramType getFileType() {
		return type;
	}
}
