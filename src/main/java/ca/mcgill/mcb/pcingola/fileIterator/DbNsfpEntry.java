package ca.mcgill.mcb.pcingola.fileIterator;

import java.util.HashMap;
import java.util.Map;

import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * DbNSFP database entry:
 * Reference	https://sites.google.com/site/jpopgen/dbNSFP
 *
 * @author lletourn
 */
public class DbNsfpEntry extends Variant {
	private static final long serialVersionUID = -3275792763917755927L;

	DbNsfp dbNsfp;
	Map<String, String> values;

	public static String[] splitValuesField(String value) {
		if (value.indexOf(DbNsfp.SUBFIELD_SEPARATOR_2) >= 0) return value.split(DbNsfp.SUBFIELD_SEPARATOR_2);
		return value.split(DbNsfp.SUBFIELD_SEPARATOR);
	}

	public DbNsfpEntry(DbNsfp dbNsfp, String line) {
		super();
		this.dbNsfp = dbNsfp;
		parse(line);
	}

	/**
	 * Add a value
	 */
	public void add(String columnName, String valuesToAdd) {
		// Represent empty values as '.'
		if (valuesToAdd.isEmpty()) valuesToAdd = ".";
		else {
			// Use '\t' as separator
			if (valuesToAdd.indexOf(DbNsfp.SUBFIELD_SEPARATOR_CHAR) >= 0) valuesToAdd = valuesToAdd.replace(DbNsfp.SUBFIELD_SEPARATOR_CHAR, '\t'); // Split values
			else if (valuesToAdd.indexOf(DbNsfp.SUBFIELD_SEPARATOR_CHAR_2) >= 0) valuesToAdd = valuesToAdd.replace(DbNsfp.SUBFIELD_SEPARATOR_CHAR_2, '\t'); // Split values
		}

		// Add value
		values.put(columnName, valuesToAdd);
	}

	@Override
	public DbNsfpEntry cloneShallow() {
		DbNsfpEntry clone = (DbNsfpEntry) super.cloneShallow();
		return clone;
	}

	/**
	 * Get data in a VCF INFO field compatible format
	 */
	public String getVcfInfo(String key) {
		String val = values.get(key);
		if (val == null) return null;

		if (val.indexOf('\t') < 0) return VcfEntry.vcfInfoSafe(val);

		// Split and re-join using commas
		String vals[] = val.split("\t");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vals.length; i++) {
			if (i > 0) sb.append(",");
			sb.append(VcfEntry.vcfInfoSafe(vals[i]));
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
	 * Parse dbSNFP values (could be from multiple lines)
	 */
	protected void parse(String line) {
		// Initialize DbNsfpEntry
		String[] vals = line.split(DbNsfp.COLUMN_SEPARATOR, -1);

		String chromosome = vals[dbNsfp.getChromosomeIdx()];
		parent = dbNsfp.getChromosome(chromosome);

		start = parsePosition(vals[dbNsfp.getStartIdx()]);
		end = start;

		// Add all entries
		variantType = VariantType.SNP;
		ref = vals[dbNsfp.getRefIdx()];
		alt = vals[dbNsfp.getAltIdx()];
		genotype = alt;

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

		sb.append(toStr() + "\t");
		sb.append('\n');

		for (String key : values.keySet())
			sb.append("\t" + key + ": '" + values.get(key) + "'\n");

		return sb.toString();
	}
}
