package org.snpsift.annotate;

import htsjdk.tribble.readers.TabixReader;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.ChromosomeSimpleName;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;

import java.io.IOException;
import java.util.*;


/**
 * Use a bgzip-compressed, tabix indexed VCF file as a database for annotations
 *
 * @author pcingola
 */
public class DbVcfTabix extends DbVcf {

    protected TabixReader tabixReader;
    protected VcfFileIterator vcf;
    protected String chrPrepend;

    public DbVcfTabix(String dbFileName) {
        super(dbFileName);
    }

    /**
     * Do all elements in 'chrNames' statrt with 'pref'?
     */
    boolean allStartWith(String pref, Collection<String> chrNames) {
        return chrNames.stream().allMatch(chr -> chr.startsWith(pref));
    }

    @Override
    public void close() {
        if (tabixReader != null) tabixReader.close();
        tabixReader = null;

        if (vcf != null) vcf.close();
        vcf = null;
        vcfHeader = null;
    }

    /**
     * Get chromosome name with same prefix as the one in Tabix index (i.e. prepend `chrPrepend` if necesary)
     */
    private String chrName(Variant variant) {
        var chr = variant.getChromosomeName();
        if (chrPrepend.isEmpty()) return chr; // No need to prepend prefix
        if (chr.startsWith(chrPrepend)) return chr; // Already starts with prefix
        return chrPrepend + chr; // Add prefix
    }

    /**
     * Find a common prefix in all chromosomes in tabix's index
     * For example, if all chromosome names starts with 'CHR', we might
     * need to prepend that to our query
     */
    private String findChrPrefix(Set<String> chrNames) {
        // Any commonly used prefixes, such as 'chr', 'chromo', etc.?
        String pref = findCommonlyUsedPrefixes(chrNames);
        if (!pref.isEmpty()) return pref;

        return findSharedPrefix(chrNames);
    }

    /**
     * Check isf some "commonly used" prefixes (e.g. 'chr', 'chromo', etc.) are in all chromosome names
     */
    private String findCommonlyUsedPrefixes(Set<String> chrNames) {
        for (String s : ChromosomeSimpleName.CHROMO_PREFIX) {
            if (allStartWith(s, chrNames)) return s;

            // Same but uppercase?
            var sUpper = s.toUpperCase(Locale.ROOT);
            if (allStartWith(sUpper, chrNames)) return sUpper;

            // Same but first letter uppercase
            var sFirstUpper = s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
            if (allStartWith(sFirstUpper, chrNames)) return sFirstUpper;

        }
        return ""; // Nothing found
    }

    /**
     * Find a common pattern in all chromosomes names
     * For example, if all chromosome names starts with 'CHR_'
     */
    private String findSharedPrefix(Set<String> chrNames) {
        if (chrNames.size() < 2) return ""; // Cannot find 'common prefix' with less than 2 names

        String prefix = chrNames.iterator().next(); // Initialize with any chrName
        for (String chr : chrNames) {
            // Find a matching "first part" for ALL chrNames
            if (chr.startsWith(prefix)) continue; // We are ok

            // Find a common start: Shorten the current prefix until we find the same prefix in 'chr'
            // For example, if the `prefix = "CHR_13"`, and `chr = "CHR_9"` then we want to
            // remove characters from the end of `prefix` until we get "CHR_"
            foundNewPrepend:
            {
                for (int i = prefix.length() - 1; i > 0; i--) {
                    if (chr.startsWith(prefix.substring(0, i))) { // Does it start with a substring of the prepend?
                        prefix = prefix.substring(0, i); // Update new prepend
                        break foundNewPrepend;
                    }
                }
                return ""; // No prepend found
            }
        }
        return prefix;
    }

    /**
     * Initialize tabix reader
     */
    protected boolean initTabix(String fileName) {
        try {
            // Do we have a tabix file?
            String indexFile = fileName + ".tbi";
            if (!Gpr.exists(indexFile)) throw new RuntimeException("Cannot find tabix index file '" + indexFile + "'");

            // Open tabix reader
            tabixReader = new TabixReader(fileName);
            chrPrepend = findChrPrefix(tabixReader.getChromosomes());
        } catch (IOException e) {
            throw new RuntimeException("Error opening tabix file '" + fileName + "'", e);
        }

        return true;
    }

    /**
     * Open database annotation file
     */
    @Override
    public void open() {
        // Open VCF file and read header
        vcf = new VcfFileIterator(dbFileName);
        vcfHeader = vcf.readHeader();

        // Open tabix index
        initTabix(dbFileName);
    }

    @Override
    public List<VariantVcfEntry> query(Variant variant) {
        List<VariantVcfEntry> results = new LinkedList<>();

        // Query and parse results
        var chr = chrName(variant);
        var start = variant.getStart() - 1; // Why '-1'? We want to capture deletions that happen right before the start base
        var end = variant.getEnd() + 1; // Why '+1'? Tabix query interval does not include the 'end' base
        TabixReader.Iterator ti = tabixReader.query(chr, start, end);

        // Any results?
        if (ti != null) {
            try {
                String line;
                while ((line = ti.next()) != null) {
                    line = Gpr.removeBackslashR(line);
                    VcfEntry ve = vcf.parseVcfLine(line);
                    results.addAll(VariantVcfEntry.factory(ve));
                }
            } catch (IOException e) {
                throw new RuntimeException("Error reading tabix file '" + dbFileName + "'", e);
            }
        }

        return results;
    }

}
