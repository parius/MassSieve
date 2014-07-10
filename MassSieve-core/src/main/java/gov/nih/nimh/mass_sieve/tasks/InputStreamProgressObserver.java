package gov.nih.nimh.mass_sieve.tasks;

import java.io.InterruptedIOException;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class InputStreamProgressObserver implements InputStreamObserver {
    private final TaskListener listener;

    public InputStreamProgressObserver(TaskListener listener) {
        this.listener = listener;
    }

    public void onRead(long totalBytesRead) throws InterruptedIOException {
        listener.onProgress((int) totalBytesRead);
    }

}
