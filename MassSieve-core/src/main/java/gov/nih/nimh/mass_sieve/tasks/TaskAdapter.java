package gov.nih.nimh.mass_sieve.tasks;

import java.awt.Component;
import java.awt.Cursor;
import java.io.InterruptedIOException;
import javax.swing.ProgressMonitor;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class TaskAdapter implements TaskListener {
    protected ProgressMonitor mon;
    protected final Component parent;

    public TaskAdapter(Component parent) {
        this.parent = parent;
    }

    public void onTaskStarted(String taskName, int taskSize) {
        mon = new ProgressMonitor(parent, "Loading " + taskName, "", 0, taskSize);
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void onProgress(int taskStep) throws InterruptedIOException {
        checkMonitorCancelled();
        mon.setProgress(taskStep);
    }

    public void onTaskFinished() {
        parent.setCursor(null);
    }

    public void onTaskCancelled() {
        parent.setCursor(null);
    }

    public void onTaskFailed() {
        parent.setCursor(null);
    }

    protected void checkMonitorCancelled() throws InterruptedIOException {
        if (mon.isCanceled()) {
            throw new InterruptedIOException("progress");
        }
    }
}
