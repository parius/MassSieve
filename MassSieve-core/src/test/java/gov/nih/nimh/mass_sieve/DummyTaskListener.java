package gov.nih.nimh.mass_sieve;

import gov.nih.nimh.mass_sieve.tasks.MultiTaskListener;
import gov.nih.nimh.mass_sieve.util.LogStub;
import java.io.InterruptedIOException;

/**
 * MultiTaskListener with no-nothing callback methods.
 *
 * Log progress on every 10 percent.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class DummyTaskListener implements MultiTaskListener {
    private final int LOG_STEP = 10;
    private int taskSize;
    private String taskName;
    private int lastLoggedProgress = -1;

    public void onMultiTaskFinished() {
    }

    public void onTaskStarted(String taskName, int taskSize) {
        this.taskName = taskName;
        this.taskSize = taskSize;
    }

    public void onProgress(int taskStep) throws InterruptedIOException {
        int curPercent = (int)(taskStep / (double)taskSize * 100);
        if (-1 == lastLoggedProgress || (curPercent - lastLoggedProgress) >= LOG_STEP) {
            lastLoggedProgress = curPercent;
            LogStub.trace(taskName+": "+curPercent+ "% completed.");
        }
    }

    public void onTaskFinished() {
    }

    public void onTaskCancelled() {
    }

    public void onTaskFailed() {
    }
}
