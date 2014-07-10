package gov.nih.nimh.mass_sieve.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Utility class
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public final class SystemInfo {

    private SystemInfo() {
        // do nothing.
    }

    public static String getSystemInfo() {
        Properties p = System.getProperties();
        return "VM: " + p.getProperty("java.vendor") + " Java " + p.getProperty("java.version")
                + "\nOS: " + p.getProperty("os.name") + " " + p.getProperty("os.version")
                + " running on " + p.getProperty("os.arch");
    }

    public static String checkAllocatedMem() {
        long val = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
        val /= 1024;
        String res = "Memory used: " + val + "MB";
        return res;
    }

    public static String checkAvailMem() {
        long val = (Runtime.getRuntime().totalMemory()) / 1024;
        val /= 1024;
        String res = "Current memory available: " + val + "MB";
        return res;
    }

    public static String checkMaxMem() {
        long val = (Runtime.getRuntime().maxMemory()) / 1024;
        val /= 1024;
        String res = "Max memory Availiable: " + val + "MB";
        return res;
    }

    public static long getMemoryUsed(StorageUnit desiredUnit) {
        long result = getMemoryUsed();
        return scaleTo(result, desiredUnit);
    }

    public static long getMemoryMax(StorageUnit desiredUnit) {
        long result = getMemoryMax();
        return scaleTo(result, desiredUnit);
    }

    public static long getMemoryTotal(StorageUnit desiredUnit) {
        long result = getMemoryTotal();
        return scaleTo(result, desiredUnit);
    }

    public static long getMemoryUsed() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public static long getMemoryTotal() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getMemoryMax() {
        return Runtime.getRuntime().maxMemory();
    }

    private static long scaleTo(long value, StorageUnit desiredUnit) {
        long result = value;
        for (StorageUnit su : StorageUnit.reversValues()) {
            if (su == desiredUnit) {
                break;
            }
            result /= 1024;
        }
        return result;
    }

    public enum StorageUnit {

        TeraBytes, MegaBytes, KiloBytes, Bytes;
        private static final List reversValues;

        static {
            reversValues = Arrays.asList(values());
            Collections.reverse(reversValues);
        }

        public static Iterable<StorageUnit> reversValues() {
            return reversValues;
        }
    }
}
