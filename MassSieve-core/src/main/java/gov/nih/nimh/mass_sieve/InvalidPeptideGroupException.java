package gov.nih.nimh.mass_sieve;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Jul 22, 2011 3:22:16 PM
 *
 * @author Alexey Tigarev
 */
public class InvalidPeptideGroupException extends RuntimeException {
    public InvalidPeptideGroupException(String hitSequence, String groupSequence) {
        super("Tried to add peptide hit with sequence " + hitSequence + " to group with sequence " + groupSequence);
    }
}
