package gov.nih.nimh.mass_sieve.tasks;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public interface DeterminedTaskListener {

    public void onChangeStepName(String stepName);

    public void onProgress(int curValue, int totalValue);

    public void onFinish();
}
