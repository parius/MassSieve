/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nih.nimh.mass_sieve.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Alex Turbin (alex.academATgmail.com)
 */
public class CSVWriter implements java.io.Closeable{
    private Writer fw;

    public CSVWriter(File outFile) throws IOException {
         fw = new BufferedWriter(new FileWriter(outFile));
    }

    public void write(Object[] rowData) throws IOException {
        for (int col = 0; col < rowData.length; col++) {
            Object elem = rowData[col];

            if (col > 0) {
                fw.write(",");
            }
            if (elem != null) {
                String str = elem.toString();
                if (str.contains(",")) {
                    fw.write("\"" + str + "\"");
                } else {
                    fw.write(str);
                }
            }
        }
        fw.write("\n");
    }

    public void close() throws IOException
    {
        fw.close();
    }
}
