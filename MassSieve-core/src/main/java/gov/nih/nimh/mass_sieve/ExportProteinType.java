package gov.nih.nimh.mass_sieve;

/**
 * Type of proteins to be exported.
 * There could be exported all proteins or only preferred ones.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public enum ExportProteinType {

    PREFERRED,
    ALL;

    public static ExportProteinType parse(int type) {
        return (0 == type) ? PREFERRED : ALL;
    }
}
