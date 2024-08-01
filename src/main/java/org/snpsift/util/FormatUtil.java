package org.snpsift.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.snpeff.fileIterator.VcfFileIterator;

public class FormatUtil {

    /**
     * Format bytes as a human readable string
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Create a VcfFileIterator from a string containig VCF lines
     */
    public static VcfFileIterator lines2VcfFileIterator(String vcfLines) {
        try (var bais = new ByteArrayInputStream(vcfLines.getBytes("UTF-8"))) {
            InputStreamReader isr = new InputStreamReader(bais);
            BufferedReader br = new BufferedReader(isr);
            return new VcfFileIterator(br);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
