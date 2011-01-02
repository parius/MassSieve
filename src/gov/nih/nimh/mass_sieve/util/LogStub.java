package gov.nih.nimh.mass_sieve.util;

/**
 * Simple logging facility.
 * In future should be replaced by logging framework like log4j.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class LogStub {

    private static final String DEBUG_MARKER = "[DEBUG] ";
    private static final String TRACE_MARKER = "[TRACE] ";

    public static void trace(Object param) {
        System.out.printf("%s%s\n", TRACE_MARKER, param);
    }

    private LogStub() {
    }

    public static void debug(String msg) {
        System.out.printf("%s%s\n", DEBUG_MARKER, msg);
    }

    public static void error(Throwable t) {
        t.printStackTrace();
    }
}
