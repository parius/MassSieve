package gov.nih.nimh.mass_sieve.tasks;

import java.io.InterruptedIOException;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public interface InputStreamObserver {

    public void onRead(long totalBytesRead) throws InterruptedIOException;
}
