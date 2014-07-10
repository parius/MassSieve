package gov.nih.nimh.mass_sieve.actions;

import gov.nih.nimh.mass_sieve.AppConfig;
import gov.nih.nimh.mass_sieve.logic.ActionResult;
import gov.nih.nimh.mass_sieve.util.LogStub;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Action to execute an operation requested from command line
 * @author Alex Turbin (alex.academATgmail.com)
 * @author Alexey Tigarev tigra@nlp.od.ua
 */
public abstract class ExternalAction {

    /**
     * Performs the action. Actual behavior is defined in descendants.
     */
    abstract public ActionResult perform();

    public List<File> searchForFiles(List<String> fileNames) {
        List<File> result = new ArrayList<File>();
        if (null == fileNames) {
            return result;
        }

        for (String fileName : fileNames) {
            // try by absolute path
            File file = new File(fileName);
            if (!file.exists()) {
                // try current directory
                file = new File(".", fileName);
                if (!file.exists()) {
                    // try default directory
                    String root = AppConfig.getDefaultSearchFilesDirectory();
                    file = new File(root, fileName);
                }
            }

            if (file.exists()) {
                result.add(file);
            } else {
                LogStub.warn("Couldn't find file:" + fileName);
            }
        }

        return result;
    }

}
