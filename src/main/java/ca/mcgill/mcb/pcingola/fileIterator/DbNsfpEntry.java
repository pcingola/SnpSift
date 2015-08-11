package ca.mcgill.mcb.pcingola.fileIterator;

import java.util.HashMap;
import java.util.HashSet;
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

	public DbNsfpEntry() {
		super();
	}

	public DbNsfpEntry(Marker parent, int start) {
		super(parent, start, start, false, "");
	}

	/**
	 * Add a value
	 */
	public void add(String alt, String columnName, String valuesToAdd) {
		// Get map by alt
		HashMap<String, String> altVals = values.getOrCreate(alt);

		// Represent empty values as '.'
		if (valuesToAdd.isEmpty()) valuesToAdd = ".";
		else valuesToAdd = valuesToAdd.replace(';', '\t'); // Split values

		if (!altVals.containsKey(columnName)) {
			// No previous values => Simply insert new value
			altVals.put(columnName, valuesToAdd);
			return;
		}

		// Remove repeated values?
		if (collapseRepeatedValues) {
			// Create a set of current values
			HashSet<String> currVals = new HashSet<String>();
			for (String cv : altVals.get(columnName).split("\t"))
				currVals.add(cv);

			// Add unique values
			for (String nv : valuesToAdd.split("\t"))
				if (!currVals.contains(nv)) {
					altVals.put(columnName, altVals.get(columnName) + "\t" + nv);
					currVals.add(nv);
				}

			return;
		}

		// Just add new value
		String newValue = altVals.get(columnName) + "\t" + valuesToAdd;
		altVals.put(columnName, newValue);

	}

	@Override
	public DbNsfpEntry cloneShallow() {
		DbNsfpEntry clone = (DbNsfpEntry) super.cloneShallow();
		clone.collapseRepeatedValues = collapseRepeatedValues;
		return clone;
	}

	/**
	 * Get tab-separated list of values (null if not found)
	 */
	public String get(String alt, String key) {
		HashMap<String, String> altVals = values.get(alt);
		if (altVals == null) return null;
		return altVals.get(key);
	}

	/**
	 * Get comma separated list of values (null if not found)
	 */
	public String getCsv(String alt, String key) {
		String val = get(alt, key);
		if (val == null) return null;
		return val.replace('\t', ','); // Make this comma separated (VCF compatible)
	}

	/**
	 * Do we have values for this allele
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
