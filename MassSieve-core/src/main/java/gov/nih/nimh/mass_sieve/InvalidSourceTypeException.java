package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.io.AnalysisProgramType;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Jul 22, 2011 2:49:53 PM
 *
 * @author Alexey Tigarev
 */
public class InvalidSourceTypeException extends RuntimeException {
    public InvalidSourceTypeException(AnalysisProgramType sourceType) {
        super(sourceType.toString());
    }
}
