package gov.nih.nimh.mass_sieve.tasks;

/**
 * Represents progress on a multi-step task.
 * Task has integer <code>totalSize</code>, knows its <code>currentStepName</code>
 * and <code>currentProgress</code>.
 * When progress is made (by call to <code>setProgress</code>) or execution of next
 * step is started (by call to <code>setStep</code>) it notifies a listener.
 *
 * @author Alex Turbin alex.academ@gmail.com
 */
public class Task {
    private String currentStepName;
    private int currentProgress;
    private final int totalSize;

    private DeterminedTaskListener listener;

    public Task(int totalSize, DeterminedTaskListener listener) {
        this.totalSize = totalSize;
        this.listener = listener;
    }

    public void setStep(String stepName) {
        currentStepName = stepName;
        listener.onChangeStepName(currentStepName);
    }

    public void setProgress(int value) {
        currentProgress = value;
        listener.onProgress(currentProgress, totalSize);
        checkIfDone();
    }

    private boolean checkIfDone() {
        if (totalSize <= currentProgress) {
            listener.onFinish();
            return true;
        }
        return false;
    }

}
