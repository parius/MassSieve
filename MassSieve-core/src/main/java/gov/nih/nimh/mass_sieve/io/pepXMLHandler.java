/*
 * pepXMLHandler.java
 *
 * Created on August 21, 2007, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import org.xml.sax.*;

/**
 *
 * @author slotta
 */
public class pepXMLHandler extends AnalysisHandler {
    String mzFileName;
    boolean inSpectrumQuery;
    int curScan;
    int curQuery;
    int curCharge;
    double curExpMass;
    double curMZ;
    
    /** Creates a new instance of pepXMLHandler */
    public pepXMLHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.PEPXML;
        inSpectrumQuery = false;
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        // TODO Estfdr
        if ("search_summary".equals(sName)) {
            mzFileName = stripPathAndExtension(attrs.getValue("base_name"));
            String aProg = attrs.getValue("search_engine");
            if ("SEQUEST".equalsIgnoreCase(aProg)) {
                analysisProgram = AnalysisProgramType.SEQUEST;
                return;
            }
            if ("MASCOT".equalsIgnoreCase(aProg)) {
                analysisProgram = AnalysisProgramType.MASCOT;
                return;
            }
            if ("X! Tandem".equalsIgnoreCase(aProg)) {
                analysisProgram = AnalysisProgramType.XTANDEM;
                return;
            }
            if ("OMSSA".equalsIgnoreCase(aProg)) {
                analysisProgram = AnalysisProgramType.OMSSA;
                return;
            }
            //if (aProg.compareToIgnoreCase("PepArML") == 0) {
            //    analysisProgram = AnalysisProgramType.PEPARML;
            //    return;
            //}
            analysisProgram = AnalysisProgramType.PEPXML;
        }
        
        if ("search_database".equals(sName)) {
            searchDB = stripPathAndExtension(attrs.getValue("local_path"));
        }
        
        if ("spectrum_query".equals(sName)) {
            inSpectrumQuery = true;
            curScan = Integer.parseInt(attrs.getValue("start_scan"));
            curQuery = Integer.parseInt(attrs.getValue("index"));
            curCharge = Integer.parseInt(attrs.getValue("assumed_charge"));
            curExpMass = Double.parseDouble(attrs.getValue("precursor_neutral_mass"));
            curMZ = (curExpMass + (curCharge * MASS_HYDROGEN)) / curCharge;
        }
        
        if (inSpectrumQuery) {
            if ("search_hit".equals(sName)) {
                curPep = new PeptideHit();
                curPep.setPepXML(true);
                curPep.setSequence(attrs.getValue("peptide"));
                curPep.setCharge(curCharge);
                curPep.setExpNeutralMass(curExpMass);
                curPep.setExpMass(curMZ);
                curPep.setScanNum(curScan);
                curPep.setQueryNum(curQuery);
                curPep.setSourceType(analysisProgram);
                curPep.setSourceFile(sourceFile);
                curPep.setRawFile(mzFileName);
                curPep.setDiffMass(attrs.getValue("massdiff"));
                curPep.setTheoreticalMass(attrs.getValue("calc_neutral_pep_mass"));
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("protein"));
                curPro.setDescription(attrs.getValue("protein_descr"));
                addProtein(curPro);
                curProHit = new ProteinHit();
                curProHit.setName(curPro.getName());
                curPep.addProteinHit(curProHit);
                curPro = null;
                curProHit = null;
            }
            if ("alternative_protein".equals(sName)) {
                curPro = new ProteinInfo();
                curPro.setName(attrs.getValue("protein"));
                curPro.setDescription(attrs.getValue("protein_descr"));
                addProtein(curPro);
                curProHit = new ProteinHit();
                curProHit.setName(curPro.getName());
                curPep.addProteinHit(curProHit);
                curPro = null;
                curProHit = null;
            }
            if ("search_score".equals(sName)) {
                String name = attrs.getValue("name");
                String value = attrs.getValue("value");
                if ("expect".equalsIgnoreCase(name)) curPep.setExpect(value);
                if ("estfdr".equalsIgnoreCase(name)) {
                    curPep.setEstimatedFDR(value); 
                }
                if ("xcorr".equalsIgnoreCase(name)) curPep.setXcorr(value);
                if ("ionscore".equalsIgnoreCase(name)) curPep.setIonScore(value);
                if ("identityscore".equalsIgnoreCase(name)) curPep.setIdent(value);
            }
            if ("peptideprophet_result".equals(sName)) {
                curPep.setPepProphet(attrs.getValue("probability"));
            }
        }
        
    }

    public void endElement(String namespaceURI, String sName, String qName) {
        if ("spectrum_query".equals(sName)) {
            inSpectrumQuery = false;
        }
        if (inSpectrumQuery && "search_hit".equals(sName)) {
            addPeptideHit(curPep);
            curPep = null;
        }
    }
    
}
