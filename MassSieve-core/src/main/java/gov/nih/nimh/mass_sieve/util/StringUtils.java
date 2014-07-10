/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nih.nimh.mass_sieve.util;

/**
 * Utility methods to manipulate with strings.
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class StringUtils {

    public static String toString(String delimeter, Object... params) {
        StringBuilder sb = new StringBuilder();

        int len = params.length;
        for (int i = 0; i < len; i++) {
            sb.append(params[i]);
            if (i + 1 < len) {
                sb.append(delimeter);
            }
        }
        return sb.toString();
    }
}
