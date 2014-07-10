package gov.nih.nimh.mass_sieve.logic;

import gov.nih.nimh.mass_sieve.Experiment;
import gov.nih.nimh.mass_sieve.Protein;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CompareExperimentDiffData {

    public ArrayList<String> expNames;
    public Map<Integer, String> proteinIndex;
    public Map<String, Experiment> exps;
    private final List<String> colNames;

    public CompareExperimentDiffData(List<String> colNames, List<String> proteins, List<Experiment> expList) {
        proteinIndex = new HashMap<Integer, String>(proteins.size());
        for (int i = 0; i < proteins.size(); i++) {
            proteinIndex.put(i, proteins.get(i));
        }

        exps = new LinkedHashMap<String, Experiment>();
        for (Experiment e : expList) {
            exps.put(e.getName(), e);
        }
        this.colNames = colNames;
    }

    public String[] getColumnNames() {
        return colNames.toArray(new String[colNames.size()]);
    }

    public int getRowCount() {
        return proteinIndex.size();
    }

    public Object[] getRow(int rowIndex) {
        String proteinName = proteinIndex.get(rowIndex);

        List<Object> result = new ArrayList<Object>();
        result.add(proteinName);

        for (String expName : exps.keySet()) {
            Experiment e = exps.get(expName);
            Protein p = e.getPepCollection().getMinProteins().get(proteinName);
            if (p == null) {
                result.addAll(Collections.nCopies(5, ""));
            } else {
                result.add(p.getParsimonyType());
                result.add(p.getCoverageNum());
                result.add(p.getCoveragePercent());
                result.add(p.getNumUniquePeptides());
                result.add(p.getNumPeptideHits());
            }
        }
        return result.toArray();
    }
}
