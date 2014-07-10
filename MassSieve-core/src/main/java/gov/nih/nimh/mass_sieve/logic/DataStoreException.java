package gov.nih.nimh.mass_sieve.logic;

/**
 * This exception is thrown if an error occures while working with underlaying
 * data storage.
 * Cause exception is available.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class DataStoreException extends Exception {

    DataStoreException(String msg, Exception cause) {
        super(msg, cause);
    }
}
