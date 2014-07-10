package gov.nih.nimh.mass_sieve.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public final class IOUtils {

    private IOUtils() {
        // do nothing.
    }

    public static void closeSafe(Closeable obj) {
        if (null != obj) {
            try {
                obj.close();
            } catch (IOException ex) {
                LogStub.error(ex);
            }
        }
    }

    /**
     * Ensures that given file has specified extension.
     * If no, creates a new instance of file with extension catenated to the end
     * of its filename.
     * @param file
     * @param extension Expected, that contains leading point.
     * @return Given file object if extension is null or file already has given extension and new file object otherwise.
     * @throws IOException
     */
    public static File ensureHasExtension(File file, String extension) throws IOException {
        if (null == extension) {
            return file;
        }
        if (!file.getCanonicalPath().endsWith(extension)) {
            return new File(file.getCanonicalPath() + extension);
        }
        return file;
    }
}
