package gov.nih.nimh.mass_sieve;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Protein Database maintained for each user.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class ProteinDB {

    private Map<String, ProteinInfo> map = new HashMap<String, ProteinInfo>();

    public ProteinDB() {
    }

    public ProteinInfo get(String name) {
        return map.get(name);
    }

    public void add(ProteinInfo info) {
        map.put(info.getName(), info);
    }

    public void addAll(Collection<ProteinInfo> infos) {
        for (ProteinInfo info : infos) {
            add(info);
        }
    }

    public Iterable<String> proteinNames() {
        return map.keySet();
    }

    /**
     *
     * @return deep copy protein database
     */
    public Map<String, ProteinInfo> getMap() {
        return new HashMap<String, ProteinInfo>(map);
    }

    public boolean contains(String name) {
        return map.containsKey(name);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    void clear() {
        map.clear();
    }
}
