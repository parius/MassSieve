package gov.nih.nimh.mass_sieve.tasks;

import java.io.InterruptedIOException;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public interface TaskListener {

    public void onTaskStarted(String taskName, int taskSize);

    public void onProgress(int taskStep) throws InterruptedIOException;

    public void onTaskFinished();

    public void onTaskCancelled();

    public void onTaskFailed();
}
