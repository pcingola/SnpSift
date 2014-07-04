package ca.mcgill.mcb.pcingola.fileIterator;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * DbNSFP database:
 * Reference	https://sites.google.com/site/jpopgen/dbNSFP
 *
 * @author lletourn
 */
public class DbNsfpFileIterator extends MarkerFileIterator<DbNsfpEntry> {

	@SuppressWarnings("unchecked")
	static private class StringTokenizer {

		String string = null;
		int tokens = 0;
		int[] separatorPosition = new int[1000];

		StringTokenizer(String value, char delim) {
			string = value;
			// Loop on the characters counting the separators and remembering
			// their positions
			StringCharacterIterator sci = new StringCharacterIterator(string);
			char c = sci.first();
			while (c != CharacterIterator.DONE) {
				if (c == delim) {
					// Remember its position
					separatorPosition[tokens] = sci.getIndex();
					tokens++;

					// Resize the position array if needed
					if (tokens >= separatorPosition.length) {
						int[] copy = new int[separatorPosition.length * 10];
						System.arraycopy(separatorPosition, 0, copy, 0, separatorPosition.length);
						separatorPosition = copy;
					}
				}
				c = sci.next();
			}
			// Add one token: tokens = separatorCount + 1
			tokens++;
		}

		<T> T[] tokens(Class<T> componentType) {
			T[] r = (T[]) Array.newInstance(componentType, tokens);
			Constructor<T> ctor;
			try {
				ctor = componentType.getConstructor(String.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Cannot create an array of type [" + componentType + "] from an array of String. The type [" + componentType.getSimpleName() + "] must define a single arg constructor that takes a String.class instance.");
			}

			String currentValue = null;
			int i = 0;
			try {
				int start = 0;
				for (i = 0; i < tokens; i++) {
					// Index of the token's last character (exclusive)
					int nextStart = separatorPosition[i];
					// Special case for the last token
					if (i == tokens - 1) nextStart = string.length();

					// Calculate the size of the token
					int length = nextStart - start;
					if (length > 0) {
						currentValue = string.substring(start, nextStart);
						r[i] = ctor.newInstance(currentValue);
					}
					start = nextStart + 1;
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot create an instance of type [" + componentType + "] from the " + i + "th string value [" + currentValue + "].", e);
			}
			return r;
		}
	}

	/**
	 * Splits a separated string into an array of <code>String</code> tokens. If
	 * the input string is null, this method returns null.
	 *
	 * <p/>
	 * Implementation note: for performance reasons, this implementation uses
	 * neither StringTokenizer nor String.split(). StringTokenizer does not
	 * return all tokens for strings of the form "1,2,,3," unless you use an
	 * instance that returns the separator. By doing so, our code would need to
	 * modify the token string which would create another temporary object and
	 * would make this method very slow. <br/>
	 * String.split does not return all tokens for strings of the form
	 * "1,2,3,,,". We simply cannot use this method.
	 * <p/>
	 * The result is a custom String splitter algorithm which performs well for
	 * large Strings.
	 *
	 * @param value
	 *            the string value to split into tokens
	 * @return an array of String Objects or null if the string value is null
	 */
	static public String[] split(final String value, char delim) {
		if (value == null) return null;
		StringTokenizer st = new StringTokenizer(value, delim);
		return st.tokens(String.class);
	}

	public static final int MIN_LINES = 10 * 1000; // Analyze at least this many lines (because some types might change)
	public static final String HEADER_PREFIX = "#";
	public static final char COLUMN_SEPARATOR = '\t';
	public static final String COLUMN_SEPARATOR_STR = "" + COLUMN_SEPARATOR;
	public static final String SUBFIELD_SEPARATOR = ";";
	public static final String SUBFIELD_SEPARATOR_ALT = ",";
	public static final String COLUMN_CHR_NAME = "chr";
	public static final String COLUMN_POS_NAME = "pos(1-coor)";

	public static final String ALT_NAME = "alt";
	private final TObjectIntHashMap<String> columnNames2Idx = new TObjectIntHashMap<String>();
	boolean collapseRepeatedValues = true;
	int chromosomeIdx;
	int startIdx;
	int altIdx;
	String fieldNames[] = null; // Field names in same order as file columns
	VcfInfoType types[] = null; // VCF data types
	boolean multipleValues[] = null; // Does this column have multiple columns?

	HashMap<String, Integer> names2index; // Map column name to index

	public DbNsfpFileIterator(String fileName) {
		super(fileName, 1);
	}

	public Set<String> getFieldNames() {
		if (columnNames2Idx.size() == 0) parseHeader(null);
		return columnNames2Idx.keySet();
	}

	public String[] getFieldNamesSorted() {
		return fieldNames;
	}

	public boolean[] getMultipleValues() {
		return multipleValues;
	}

	public HashMap<String, Integer> getNames2index() {
		return names2index;
	}

	public VcfInfoType[] getTypes() {
		return types;
	}

	/**
	 * Guess value type
	 * @param value
	 * @return
	 */
	public VcfInfoType guessDataType(String value) {
		// Empty? Nothing to do
		if (value == null || value.isEmpty() || value.equals(".")) return null;

		//---
		// Do we have multiple valued field? Split it
		//---
		if (isMultipleValues(value)) {
			String values[] = null;
			if (value.indexOf(SUBFIELD_SEPARATOR_ALT) >= 0) values = value.split(SUBFIELD_SEPARATOR_ALT);
			else values = value.split(SUBFIELD_SEPARATOR);

			VcfInfoType type = null;
			for (String val : values) {
				VcfInfoType valType = guessDataType(val);
				if (type == null) type = valType;
				else if (valType == null) continue; // We cannot infer this sub-field's data type. No problem
				else if (type != valType) return null; // There is no consensus on the data type of each sub-field => null
			}

			return type;
		}

		//---
		// There is only one value. Let's try to guess what it is
		//---
		try {
			Long.parseLong(value);
			return VcfInfoType.Integer;
		} catch (Exception e) {
			// OK, it was not an integer
		}

		try {
			Double.parseDouble(value);
			return VcfInfoType.Float;
		} catch (Exception e) {
			// OK, it was not a float
		}

		// Is it a character?
		if (value.length() == 1) return VcfInfoType.Character;

		// Is it a flag?
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("no")) return VcfInfoType.Flag;

		// OK, it's a string then

		return VcfInfoType.String;
	}

	/**
	 * Guess data types from file
	 * @return
	 */
	protected boolean guessDataTypes() {
		boolean header = true;
		fieldNames = null;
		types = null;
		multipleValues = null;
		names2index = null;

		// Iterate parsing lines until we guessed all data types (lines can have empty values, so we may not be able to guess them in the first line).
		try {
			for (int entryNum = 0; ready(); entryNum++) {
				line = readLine();
				if (line == null) return false; // End of file?

				if (header) {
					//---
					// Parse header
					//---
					header = false;

					// Make sure this is the header
					if (HEADER_PREFIX.isEmpty() || line.startsWith(HEADER_PREFIX)) {
						line = line.substring(HEADER_PREFIX.length());
						fieldNames = line.split(COLUMN_SEPARATOR_STR);
						types = new VcfInfoType[fieldNames.length];
						multipleValues = new boolean[fieldNames.length];

						names2index = new HashMap<String, Integer>();
						for (int i = 0; i < fieldNames.length; i++) {
							fieldNames[i] = fieldNames[i].trim();
							names2index.put(fieldNames[i], i);
						}
					} else {
						// Cannot parse header!
						return false;
					}
				} else {
					//---
					// Parse values
					//---
					if (multipleValues == null) throw new RuntimeException("Cannot parse file '" + fileName + "'. Missing header?");

					boolean done = true;
					String values[] = line.split(COLUMN_SEPARATOR_STR);

					// Process each field
					for (int i = 0; i < fieldNames.length; i++) {
						// We don't know the type yet? Try to guess it
						VcfInfoType type = guessDataType(values[i]);

						// New data type? Set it
						if (types[i] == null) {
							types[i] = type;
						} else {
							// Some types can 'change'
							if (types[i] == VcfInfoType.Integer && type == VcfInfoType.Float) types[i] = type;
							else if (type == VcfInfoType.String) types[i] = type;
						}

						// Do we have multiple values per field?
						multipleValues[i] |= isMultipleValues(values[i]);
						done &= (types[i] != null);
					}

					// Have we guessed all types? => We are done
					if (done && entryNum > MIN_LINES) return true;
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Error reading file '" + fileName + "'. Line ignored:\n\tLine (" + lineNum + "):\t'" + line + "'", e);
		}

		return true;
	}

	/**
	 * Guess field types: Read many lines and guess the data type for each column
	 */
	public boolean guessVcfTypes() {
		boolean ok = guessDataTypes();
		init(fileName, inOffset); // We need to re-open the file after guessing data types
		return ok;
	}

	/**
	 * Do we have a column 'colName'?
	 */
	public boolean hasField(String filedName) {
		if (columnNames2Idx.size() == 0) parseHeader(null);
		return columnNames2Idx.containsKey(filedName);
	}

	/**
	 * Do we have multiple values separated by 'subfieldSeparator'?
	 */
	boolean isMultipleValues(String value) {
		return value.indexOf(SUBFIELD_SEPARATOR) >= 0 || value.indexOf(SUBFIELD_SEPARATOR_ALT) >= 0;
	}

	/**
	 * Parse dbSNFP values (could be from multiple lines)
	 *
	 * @param valuesForEntry
	 * @return
	 */
	private DbNsfpEntry parseEntry(List<String[]> valuesForEntry) {
		// Initialize DbNsfpEntry
		String[] vals = valuesForEntry.get(0);
		String chromosome = vals[chromosomeIdx];
		int start = parsePosition(vals[startIdx]);
		DbNsfpEntry dbNsfp = new DbNsfpEntry(getChromosome(chromosome), start);
		dbNsfp.setCollapseRepeatedValues(collapseRepeatedValues);

		// Add all entries
		for (String[] altAlleleValues : valuesForEntry) {
			String alt = altAlleleValues[altIdx];

			for (String columnName : columnNames2Idx.keySet()) {
				// Add all "columnName = value" pairs
				int colIndex = columnNames2Idx.get(columnName);
				String value = altAlleleValues[colIndex];
				dbNsfp.add(alt, columnName, value);
			}
		}
		return dbNsfp;
	}

	/**
	 * Parse header line
	 * @param line
	 */
	void parseHeader(String line) {
		// No line provided? Read one
		if (line == null) {
			try {
				if (ready()) line = readLine();
				else throw new RuntimeException("Error reading (parsing header) from file '" + fileName + "'.");
			} catch (IOException e) {
				throw new RuntimeException("Error reading file '" + fileName + "'. Line ignored:\n\tLine (" + lineNum + "):\t'" + line + "'");
			}
		}

		// Parse column names
		if (!line.startsWith(HEADER_PREFIX)) throw new RuntimeException("First line is not a valid header!\n\tFirst line: " + line);
		line = line.substring(1); // Remove first '#' character
		String values[] = split(line, COLUMN_SEPARATOR);

		// Add all column names to hash
		chromosomeIdx = startIdx = altIdx = -1;
		for (int idx = 0; idx < values.length; idx++) {
			columnNames2Idx.put(values[idx].trim(), idx);

			// Chromosome and position column numbers
			if (values[idx].equals(COLUMN_CHR_NAME)) chromosomeIdx = idx;
			else if (values[idx].equals(COLUMN_POS_NAME)) startIdx = idx;
			else if (values[idx].equals(ALT_NAME)) altIdx = idx;
		}

		// Errors?
		if (chromosomeIdx == -1) throw new RuntimeException("Missing '" + COLUMN_CHR_NAME + "' columns in dbNSFP file");
		if (startIdx == -1) throw new RuntimeException("Missing '" + COLUMN_POS_NAME + "' columns in dbNSFP file");
		if (altIdx == -1) throw new RuntimeException("Missing '" + ALT_NAME + "' columns in dbNSFP file");
	}

	@Override
	protected DbNsfpEntry readNext() {
		// Do we need to guess data types?
		if (types == null) guessVcfTypes();

		// Read another entry from the file
		try {
			List<String[]> valuesForEntry = new ArrayList<String[]>();

			while (ready()) {
				line = readLine();
				if (line == null) return null; // End of file?

				// Do we need to parse header?
				if (columnNames2Idx.size() == 0) {
					parseHeader(line);
					continue;
				}

				// Parse data
				String values[] = split(line, '\t');
				if (valuesForEntry.size() > 0) {
					String currentValues[] = valuesForEntry.get(0);
					if (!currentValues[chromosomeIdx].equals(values[chromosomeIdx]) || !currentValues[startIdx].equals(values[startIdx])) {
						nextLine = line;
						break;
					}
				}

				valuesForEntry.add(values);
			}

			if (valuesForEntry.size() == 0) return null;

			DbNsfpEntry entry = parseEntry(valuesForEntry);
			return entry;

		} catch (IOException e) {
			throw new RuntimeException("Error reading file '" + fileName + "'. Line ignored:\n\tLine (" + lineNum + "):\t'" + line + "'");
		}
	}

	//	@Override
	//	public void seek(long pos) throws IOException {
	//		super.seek(pos);
	//		nextLine = null;
	//	}

	public void setCollapseRepeatedValues(boolean collapseRepeatedValues) {
		this.collapseRepeatedValues = collapseRepeatedValues;
	}

}
