/*
 * omssaHandler.java
 *
 * Created on February 24, 2006, 2:02 PM
 *
 * @author Douglas Slotta
 */

package gov.nih.nimh.mass_sieve.io;

import gov.nih.nimh.mass_sieve.*;
import java.util.ArrayList;
import org.xml.sax.*;
import java.util.HashMap;

public class omssaHandler extends AnalysisHandler {
    
    private boolean inMSHitSet;
    private boolean inMSSpectrum;
    private boolean inBioseqs;
    private ArrayList<PeptideHit> peptideHits;
    private HashMap<Integer, Integer> QueryToScan;
    private HashMap<Integer, Double> QueryToMass;
    private HashMap<Integer, String> QueryToRawFile;
    private int currentQuery;
    private double scaleFactor;
    private int pepQueryNum;
    private HashMap<String, String> decode;
    private HashMap<String, String> OIDtoName;
    private String curProAcc;
    private String curProID;
    private String curProOID;
    
    
    /** Creates a new instance of omssaHandler */
    public omssaHandler(String fn) {
        super(fn);
        analysisProgram = AnalysisProgramType.OMSSA;
        inMSHitSet = false;
        inMSSpectrum = false;
        inBioseqs = false;
        QueryToScan = new HashMap<Integer, Integer>();
        QueryToMass = new HashMap<Integer, Double>();
        QueryToRawFile = new HashMap<Integer, String>();
        peptideHits = new ArrayList<PeptideHit>();
        scaleFactor = 100.0;
        OIDtoName = new HashMap<String, String>();
        initDecode();
    }
    
    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        
        if (sName.equals("MSHitSet")) {
            collectData = true;
            inMSHitSet = true;
        }
        if (sName.equals("MSSpectrum")) {
            collectData = true;
            inMSSpectrum = true;
        }
        
        if (sName.equals("MSSearchSettings_db")) collectData = true;
        if (sName.equals("MSSearchSettings_scale")) collectData = true;
        if (sName.equals("MSResponse_scale")) collectData = true;
        
        if (sName.equals("MSHits")) {
            curPep = new PeptideHit();
            curPep.setSourceType(analysisProgram);
        }
        
        if (inMSHitSet) {
            if (sName.equals("MSHits_mzhits")) collectData = false;
            if (sName.equals("MSPepHit")) {
                curPro = new ProteinInfo();
                curProHit = new ProteinHit();
            }
        }
        
        if (sName.equals("MSResponse_bioseqs")) {
            collectData = true;
            inBioseqs = true;
        }
    }
    
    
    
    public void endElement(String namespaceURI, String sName, String qName) {
        
        if (sName.equals("MSSearchSettings_db")) {
            searchDB = data;
            collectData = false;
            data = "";
        }
        
        if (sName.equals("MSResponse_scale")) {
            scaleFactor = Double.parseDouble(data);
            collectData = false;
            data = "";
        }
        if (sName.equals("MSSearchSettings_scale")) {
            scaleFactor = Double.parseDouble(data);
            collectData = false;
            data = "";
        }
        
        if (sName.equals("MSHitSet")) {
            collectData = false;
            inMSHitSet = false;
        }
        if (sName.equals("MSSpectrum")) {
            collectData = false;
            inMSSpectrum = false;
        }
        
        if (sName.equals("MSResponse_bioseqs")) {
            collectData = false;
            inBioseqs = false;
        }
        
        if (sName.equals("MSHits")) {
            curPep.setQueryNum(pepQueryNum);
            curPep.setScanNum(QueryToScan.get(pepQueryNum));
            curPep.setRawFile(QueryToRawFile.get(pepQueryNum));
            curPep.setExpMass(QueryToMass.get(pepQueryNum));
            curPep.setDiffMass();
            addPeptideHit(curPep);
            pepHitCount += 1;
            curPep = null;
        }
        if (sName.equals("MSPepHit")) {
            //curPro.fixIDandName();
            curPro.setName(getAcc_Or_ID());
            curProAcc = null;
            curProID = null;
            curProHit.setName(curPro.getName());
            OIDtoName.put(curProOID, curPro.getName());
            addProtein(curPro);
            curPep.addProteinHit(curProHit);
            
            curPro = null;
            curProHit = null;
        }
        if (inMSHitSet) {
            if (sName.equals("MSHits_mzhits")) collectData = true;
            if (sName.equals("MSHitSet_number")) pepQueryNum = Integer.parseInt(data);
            if (sName.equals("MSHits_charge")) curPep.setCharge(data);
            if (sName.equals("MSHits_mass")) curPep.setExpNeutralMass(Integer.parseInt(data));
            if (sName.equals("MSHits_theomass")) curPep.setTheoreticalMass(Integer.parseInt(data));
            if (sName.equals("MSHits_evalue")) curPep.setExpect(data);
            if (sName.equals("MSHits_pepstring")) curPep.setSequence(data);
            if (sName.equals("MSPepHit_start")) curProHit.setStart(Integer.parseInt(data)+1);
            if (sName.equals("MSPepHit_stop")) curProHit.setEnd(Integer.parseInt(data)+1);
            if (sName.equals("MSPepHit_gi")) curProID = "gi|" + data;
            if (sName.equals("MSPepHit_accession")) curProAcc = data;
            if (sName.equals("MSPepHit_oid")) curProOID = data;
            if (sName.equals("MSPepHit_defline")) curPro.setDescription(data);
            if (sName.equals("MSPepHit_protlength")) curPro.setLength(Integer.parseInt(data));
            data = "";
        }
        if (inMSSpectrum) {
            if (sName.equals("MSSpectrum_number")) currentQuery = Integer.parseInt(data);
            if (sName.equals("MSSpectrum_precursormz")) {
                QueryToMass.put(currentQuery,Double.parseDouble(data));
            }
            if (sName.equals("MSSpectrum_ids_E")) {
                QueryToScan.put(currentQuery,scanFilenameToScanNumber(data, currentQuery));
                String rawFile = scanFilenameToRawFile(data);
                QueryToRawFile.put(currentQuery,rawFile);
                rawFiles.add(rawFile);
            }
            data = "";
        }
        
        if (inBioseqs) {
            if (sName.equals("MSBioseq_oid")) curProOID = data;
            if (sName.equals("NCBIstdaa")) {
                String proName = OIDtoName.get(curProOID);
                curPro = proteinDB.get(proName);
                curPro.setSequence(NCBIstdaaDecode(data));
            }
            data = "";
        }
    }
    
    public String getAcc_Or_ID() {
        if (curProAcc == null) return curProID;
        return curProAcc;
    }
    
    public void scaleMasses() {
        for (PeptideHit ph:peptide_hits) {
            ph.setExpMass(ph.getExpMass() / scaleFactor);
            ph.setExpNeutralMass(ph.getExpNeutralMass() / scaleFactor);
            ph.setTheoreticalMass(ph.getTheoreticalMass() / scaleFactor);
        }
    }
    
    private void initDecode() {
        decode = new HashMap<String, String>();
        //NCBIstdaa              Value	Symbol	Name
        decode.put("00", "-"); //  0	-	Gap
        decode.put("01", "A"); //  1	A	Alanine
        decode.put("02", "B"); //  2	B	Asp or Asn
        decode.put("03", "C"); //  3	C	Cysteine
        decode.put("04", "D"); //  4	D	Aspartic Acid
        decode.put("05", "E"); //  5	E	Glutamic Acid
        decode.put("06", "F"); //  6	F	Phenylalanine
        decode.put("07", "G"); //  7	G	Glycine
        decode.put("08", "H"); //  8	H	Histidine
        decode.put("09", "I"); //  9	I	Isoleucine
        decode.put("0A", "K"); // 10	K	Lysine
        decode.put("0B", "L"); // 11	L	Leucine
        decode.put("0C", "M"); // 12	M	Methionine
        decode.put("0D", "N"); // 13	N	Asparagine
        decode.put("0E", "P"); // 14	P	Proline
        decode.put("0F", "Q"); // 15	Q	Glutamine
        decode.put("10", "R"); // 16	R	Arginine
        decode.put("11", "S"); // 17	S	Serine
        decode.put("12", "T"); // 18	T	Threoine
        decode.put("13", "V"); // 19	V	Valine
        decode.put("14", "W"); // 20	W	Tryptophan
        decode.put("15", "X"); // 21	X	Undetermined or atypical
        decode.put("16", "Y"); // 22	Y	Tyrosine
        decode.put("17", "Z"); // 23	Z	Glu or Gln
        decode.put("18", "U"); // 24	U	Selenocysteine
        decode.put("19", "*"); // 25	*	Termination
        decode.put("1A", "O"); // 26	O	Pyrrolysine
        decode.put("1B", "J"); // 27	J	Leu or Ile
    }
    
    private String NCBIstdaaDecode(String input) {
        // Yes, the encoded version is twice the size of the decoded version
        StringBuffer output = new StringBuffer(input.length() / 2);
        
        for (int i=0; i<input.length(); i+=2) {
            output.append(decode.get(input.substring(i,i+2)));
        }
        return output.toString();
    }
    
}