package org.snpsift.fileIterator;

import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * DbNSFP database entry:
 * Reference	https://sites.google.com/site/jpopgen/dbNSFP
 *
 * @author lletourn
 */
public class DbNsfpEntry extends Variant {
    private static final long serialVersionUID = -3275792763917755927L;

    DbNsfp dbNsfp;
    String line;
    Map<String, String> values;

    public DbNsfpEntry(DbNsfp dbNsfp, String line) {
        super();
        this.dbNsfp = dbNsfp;
        this.line = line;
        parseChrPosRefAlt();
    }

    public static String[] splitValuesField(String value) {
        if (value.contains(DbNsfp.SUBFIELD_SEPARATOR_2)) return value.split(DbNsfp.SUBFIELD_SEPARATOR_2);
        return value.split(DbNsfp.SUBFIELD_SEPARATOR);
    }

    /**
     * Add a value
     */
    public void add(String columnName, String valuesToAdd) {
        // Represent empty values as '.'
        if (valuesToAdd.isEmpty()) valuesToAdd = ".";
        else {
            // Use '\t' as separator
            if (valuesToAdd.indexOf(DbNsfp.SUBFIELD_SEPARATOR_CHAR) >= 0)
                valuesToAdd = valuesToAdd.replace(DbNsfp.SUBFIELD_SEPARATOR_CHAR, '\t'); // Split values
            else if (valuesToAdd.indexOf(DbNsfp.SUBFIELD_SEPARATOR_CHAR_2) >= 0)
                valuesToAdd = valuesToAdd.replace(DbNsfp.SUBFIELD_SEPARATOR_CHAR_2, '\t'); // Split values
        }

        // Add value
        values.put(columnName, valuesToAdd);
    }

    @Override
    public DbNsfpEntry cloneShallow() {
        return (DbNsfpEntry) super.cloneShallow();
    }

    /**
     * Get data in a VCF INFO field compatible format
     */
    public String getVcfInfo(String key) {
        if (values == null) parseKeyValues();
        String val = values.get(key);
        if (val == null) return null;

        if (val.indexOf('\t') < 0) return VcfEntry.vcfInfoValueSafe(val);

        // Split and re-join using commas
        String[] vals = val.split("\t");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(VcfEntry.vcfInfoValueSafe(vals[i]));
        }

        return sb.toString();
    }

    /**
     * Do we have values for this allele
     */
    public boolean hasValues(String allele) {
        return values.containsKey(allele);
    }

    /**
     * Parse dbSNFP 'Chr, pos, ref, alt' values from a single line
     */
    protected void parseChrPosRefAlt() {
        String[] vals = line.split(DbNsfp.COLUMN_SEPARATOR, -1);

        // Parse chromosome
        String chromosome = vals[dbNsfp.getChromosomeIdx()];
        parent = dbNsfp.getChromosome(chromosome);

        // Parse start & end
        start = end = parsePosition(vals[dbNsfp.getStartIdx()]);
        variantType = VariantType.SNP;

        // Ref & Alt entries
        ref = vals[dbNsfp.getRefIdx()];
        alt = genotype = vals[dbNsfp.getAltIdx()];
    }

    /**
     * Parse dbSNFP ALL values from a single line and store them in 'values' hash
     * <p>
     * Note: This method is supposed to be a lazy parsing of the key/value
     * pairs, so it DOES NOT store 'chr, pos, ref, alt'. You can use
     * `parseChrPosRefAlt()` for that.
     */
    protected void parseKeyValues() {
        // Initialize DbNsfpEntry
        String[] vals = line.split(DbNsfp.COLUMN_SEPARATOR, -1);

        // Add 'key=value' pairs
        values = new HashMap<>();
        for (int i = 0; i < dbNsfp.getFieldCount(); i++)
            add(dbNsfp.getFieldName(i), vals[i]);
    }

    /**
     * Parse a string as a 'position'.
     */
    public int parsePosition(String posStr) {
        return Gpr.parseIntSafe(posStr) - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(toStr()).append("\t").append('\n');

        for (String key : values.keySet())
            sb.append("\t").append(key).append(": '").append(values.get(key)).append("'\n");

        return sb.toString();
    }
}
