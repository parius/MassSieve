package gov.nih.nimh.mass_sieve.cli;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CLIParseException extends Exception{

    public CLIParseException(String message) {
        super(message);
    }

    public CLIParseException(Exception ex) {
        super(ex);
    }

}
