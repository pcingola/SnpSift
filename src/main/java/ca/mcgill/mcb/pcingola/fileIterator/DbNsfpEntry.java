package ca.mcgill.mcb.pcingola.fileIterator;

import java.util.HashMap;
import java.util.Map;

import ca.mcgill.mcb.pcingola.collections.AutoHashMap;
import ca.mcgill.mcb.pcingola.interval.Marker;

/**
 * DbNSFP database entry: 
 * Reference	https://sites.google.com/site/jpopgen/dbNSFP
 * 
 * @author lletourn
 */
public class DbNsfpEntry extends Marker {
	private static final long serialVersionUID = -3275792763917755927L;

	AutoHashMap<String, HashMap<String, String>> values = new AutoHashMap<String, HashMap<String, String>>(new HashMap<String, String>());
	boolean collapseRepeatedValues = true;

	public DbNsfpEntry(Marker parent, int start) {
		super(parent, start, start, 1, "");
	}

	/**
	 * Add a value
	 * @param alt
	 * @param columnName
	 * @param value
	 */
	public void add(String alt, String columnName, String value) {
		// Get map by alt
		HashMap<String, String> altVals = values.getOrCreate(alt);

		// We cannot use comma or semicolon, so we replace them by ','
		value = value.replace(';', ',');
		// value = value.replace(';', '|');
		// value = value.replace(',', '|');

		// Represent empty values as '.'
		if (value.isEmpty()) value = ".";

		if (!altVals.containsKey(columnName)) {
			// No previous values => Simply insert new value
			altVals.put(columnName, value);
			return;
		}

		// Append new value, but first make sure we are not repeating values
		boolean shouldAdd = true;
		if (collapseRepeatedValues) {
			String currvals[] = altVals.get(columnName).split("\t");
			for (String v : currvals)
				if (v.equalsIgnoreCase(value)) {
					shouldAdd = false;
					break;
				}
		}

		// Value already in the map? don't add twice
		if (shouldAdd) {
			String newValue = altVals.get(columnName) + "\t" + value;
			altVals.put(columnName, newValue);
		}
	}

	/**
	 * Get tab-separated list of values (null if not found)
	 * @param alt
	 * @param key
	 * @return
	 */
	public String get(String alt, String key) {
		HashMap<String, String> altVals = values.get(alt);
		if (alt == null) return null;
		return altVals.get(key);
	}

	/**
	 * Get comma separated list of values (null if not found)
	 * @param alt
	 * @param key
	 * @return
	 */
	public String getCsv(String alt, String key) {
		String val = get(alt, key);
		if (val == null) return null;
		return val.replace('\t', ','); // Make this comma separated (VCF compatible)
	}

	/**
	 * Do we have values for this allele
	 * @param allele
	 * @return
	 */
	public boolean hasValues(String allele) {
		return values.containsKey(allele);
	}

	public void setCollapseRepeatedValues(boolean collapseRepeatedValues) {
		this.collapseRepeatedValues = collapseRepeatedValues;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(getChromosomeName() + "\t");
		sb.append(getStart() + "\t");
		for (String alt : values.keySet())
			sb.append(alt + ",");
		sb.deleteCharAt(sb.length() - 1);
		sb.append('\n');

		for (String key : values.keySet()) {
			Map<String, String> map = values.get(key);
			sb.append("\tALT '" + key + "':\n");
			for (String mk : map.keySet()) {
				sb.append("\t\t" + mk + ": '" + map.get(mk) + "'\n");
			}
		}
		return sb.toString();
	}
}
