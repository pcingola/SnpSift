package org.snpsift.fileIterator;

import htsjdk.tribble.readers.TabixReader;
import org.snpeff.fileIterator.LineFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.interval.Genome;
import org.snpeff.interval.Marker;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.DbMarker;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DbNSFP database:
 * Reference	https://sites.google.com/site/jpopgen/dbNSFP
 *
 * @author pcingola
 * @author lletourn (original author)
 */
public class DbNsfp implements DbMarker<Variant, DbNsfpEntry> {

    public static final String DATA_TYPES_CACHE_EXT = ".data_types";

    public static final int DATA_TYPES_MIN_LINES = 10 * 1000; // Analyze at least this many lines (because some types might change)
    public static final int DATA_TYPES_MAX_LINES = 100 * 1000; // Analyze at most this many lines

    public static final String HEADER_PREFIX = "#";
    public static final String COLUMN_SEPARATOR = "\t";

    public static final char SUBFIELD_SEPARATOR_CHAR = ';'; // Sub-field separator
    public static final String SUBFIELD_SEPARATOR = "" + SUBFIELD_SEPARATOR_CHAR;

    public static final char SUBFIELD_SEPARATOR_CHAR_2 = ','; // This is also use as sub-field separator
    public static final String SUBFIELD_SEPARATOR_2 = "" + SUBFIELD_SEPARATOR_CHAR_2;

    public static final String COLUMN_CHR_NAME = "chr";
    public static final String COLUMN_POS_NAME_v2 = "pos(1-coor)";
    public static final String COLUMN_POS_NAME_v3 = "pos(1-based)";
    public static final String ALT_NAME = "alt";
    public static final String REF_NAME = "ref";

    protected String fileName;
    protected boolean debug;
    protected boolean[] multipleValues = null; // Does this column have multiple columns?
    protected boolean verbose;
    protected int chromosomeIdx; // Column number for chrsomosome informaiton
    protected int startIdx; // Column number for 'start position' informaiton
    protected int altIdx; // Column number of 'ALT' information
    protected int refIdx; // Column number of 'REF' information
    protected int maxChrPosRefAltIdx; // Max of the column indexes to parse chr, pos, ref, alt
    protected String[] fieldNames = null; // Field names in same order as file columns
    protected VcfInfoType[] types = null; // VCF data types
    protected Genome genome;
    protected HashMap<String, Integer> names2index; // Map column name to index
    protected TabixReader tabixReader;
    protected List<DbNsfpEntry> latestResults; // Latest results when searching in the tabix file
    protected Marker latestResultsInterval; // Latest interval queried in tabix file

    public DbNsfp(String fileName) {
        this.fileName = fileName;
        genome = new Genome();
        latestResults = new LinkedList<>();

        // Initialize with an invalid marker
        Genome genome = new Genome("NULL_GENOME");
        Chromosome chr = new Chromosome(genome, -1, -1, "NULL_CHROMOSOME");
        latestResultsInterval = new Marker(chr, -1, -1);
    }

    @Override
    public void close() {
        if (tabixReader != null) tabixReader.close();
        tabixReader = null;
    }

    /**
     * Guess field types: Read many lines and guess the data type for each column
     */
    protected boolean dataTypes() {
        // Data types have been cached before?
        boolean ok = false;

        String cacheFileName = fileName + DATA_TYPES_CACHE_EXT;

        // Load data types from file
        if (!loadCachedDataTypes(cacheFileName)) {
            // Not cached? Calculate data types
            ok = guessDataTypes();
            saveDataTypesCache(cacheFileName);
        }

        forceMissingTypesAsString();

        return ok;
    }

    /**
     * Force missing types as string
     */
    public void forceMissingTypesAsString() {
        for (int i = 0; i < types.length; i++)
            if (types[i] == null) types[i] = VcfInfoType.String;
    }

    public int getAltIdx() {
        return altIdx;
    }

    public int getChrPosRefAltIdx() {
        return maxChrPosRefAltIdx;
    }

    /**
     * Find chromosome 'chromoName'. If it does not exists and 'createChromos' is true, the chromosome is created
     */
    public Chromosome getChromosome(String chromoName) {
        return genome.getOrCreateChromosome(chromoName);
    }

    public int getChromosomeIdx() {
        return chromosomeIdx;
    }

    public int getFieldCount() {
        return fieldNames.length;
    }

    public String getFieldName(int idx) {
        return fieldNames[idx];
    }

    public Set<String> getFieldNames() {
        return names2index.keySet();
    }

    public String[] getFieldNamesSorted() {
        return fieldNames;
    }

    public int getIndex(String name) {
        return names2index.get(name);
    }

    public HashMap<String, Integer> getNames2index() {
        return names2index;
    }

    public int getRefIdx() {
        return refIdx;
    }

    public int getStartIdx() {
        return startIdx;
    }

    public VcfInfoType[] getTypes() {
        return types;
    }

    /**
     * Guess value type
     */
    protected VcfInfoType guessDataType(String value) {
        // Empty? Nothing to do
        if (value == null || value.isEmpty() || value.equals(".")) return null;

        //---
        // Do we have multiple valued field? Split it
        //---
        if (isMultipleValues(value)) {
            String[] values = DbNsfpEntry.splitValuesField(value);

            VcfInfoType type = null;
            for (String val : values) {
                VcfInfoType valType = guessDataType(val);
                if (type == null) type = valType;
                else if (valType == null) continue; // We cannot infer this sub-field's data type. No problem
                else if (type != valType)
                    return null; // There is no consensus on the data type of each sub-field => null
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
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("no"))
            return VcfInfoType.Flag;

        // OK, it's a string then

        return VcfInfoType.String;
    }

    /**
     * Guess data types from file
     */
    protected boolean guessDataTypes() {
        if (verbose) Log.info("Guessing data types for file '" + fileName + "'");

        boolean header = true;
        fieldNames = null;
        types = null;
        multipleValues = null;
        names2index = null;

        // Iterate parsing lines until we guessed all data types (lines
        // can have empty values, so we may not be able to guess them in
        // the first line).
        LineFileIterator lfi = new LineFileIterator(fileName);

        int entryNum = 0;
        for (String line : lfi) {
            if (line == null) {
                lfi.close();
                return false; // End of file?
            }

            if (header) {
                // Parse header
                header = false;
                parseHeader(line);
            } else {
                //---
                // Parse values
                //---
                if (multipleValues == null)
                    throw new RuntimeException("Cannot parse file '" + fileName + "'. Missing header?");

                boolean done = true;
                String[] values = line.split(COLUMN_SEPARATOR, -1);

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

                if (verbose) Gpr.showMark(entryNum, 1000);

                // Have we guessed all types? => We are done
                if (done && entryNum > DATA_TYPES_MIN_LINES) {
                    lfi.close();
                    return true;
                }

                // Too many lines analyzed? We should be done...
                if (entryNum > DATA_TYPES_MAX_LINES) {
                    lfi.close();
                    return false;
                }

                entryNum++;
            }
        }

        return true;
    }

    /**
     * Do we have a column 'colName'?
     */
    public boolean hasField(String filedName) {
        return names2index.containsKey(filedName);
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
            if (verbose) Log.info("Opening database file and loading index");
            tabixReader = new TabixReader(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Error opening tabix file '" + fileName + "'", e);
        }

        return true;
    }

    /**
     * Do we have multiple values separated by 'subfieldSeparator'?
     */
    boolean isMultipleValues(String value) {
        return value.contains(SUBFIELD_SEPARATOR) //
                || value.contains(SUBFIELD_SEPARATOR_2);
    }

    /**
     * Read data types form cache
     *
     * @return true on success
     */
    boolean loadCachedDataTypes(String cacheFileName) {
        if (verbose) Log.info("Loading data types from file '" + cacheFileName + "'");

        // File doesn't exist, cannot load
        if (!Gpr.canRead(cacheFileName)) {
            if (verbose) Log.info("Data types cache file '" + cacheFileName + "' not found");
            return false;
        }

        // Is cache older than database file? Then we need to update the cache
        File db = new File(fileName);
        File cache = new File(cacheFileName);
        if (db.lastModified() > cache.lastModified()) {
            if (verbose) Log.info("Data types cache file '" + cacheFileName + "' needs to be updated");
            return false;
        }

        // Read file
        String[] lines = Gpr.readFile(cacheFileName).split("\n");

        // Initialize
        fieldNames = new String[lines.length];
        types = new VcfInfoType[lines.length];
        multipleValues = new boolean[lines.length];

        // Parse lines
        for (int i = 0; i < lines.length; i++) {
            String[] fields = lines[i].split("\t");

            if (fields.length != 3)
                throw new RuntimeException("Error parsing line " + (i + 1) + " from file '" + fileName + "':\n" + lines[i]);

            fieldNames[i] = fields[0];
            types[i] = (fields[1].equals("null") ? null : VcfInfoType.valueOf(fields[1]));
            multipleValues[i] = Gpr.parseBoolSafe(fields[2]);
        }

        // Names to index mapping
        names2index = new HashMap<>();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i].trim();
            fieldNames[i] = fieldName;
            names2index.put(fieldName, i);
            updateIndexes(fieldName, i);
        }

        updateChrPosRefAltIndex();
        return true;
    }

    /**
     * Does database entry 'DbNsfpEntry' match 'variant'?
     */
    protected boolean match(Variant var, DbNsfpEntry dbEntry) {
        return var.getChromosomeName().equals(dbEntry.getChromosomeName()) //
                && var.getStart() == dbEntry.getStart() //
                && var.getEnd() == dbEntry.getEnd() //
                && var.getReference().equalsIgnoreCase(dbEntry.getReference()) //
                && var.getAlt().equalsIgnoreCase(dbEntry.getAlt()) //
                ;
    }

    @Override
    public void open() {
        dataTypes();

        initTabix(fileName);
    }

    /**
     * Parse header line
     */
    void parseHeader(String line) {
        // Parse column names
        if (!line.startsWith(HEADER_PREFIX))
            throw new RuntimeException("Error: Invalid header line!\n\tLine:\t" + line);

        line = line.substring(HEADER_PREFIX.length()); // Remove header prefix

        // Add all column names to hash
        chromosomeIdx = startIdx = altIdx = -1;
        fieldNames = line.split(COLUMN_SEPARATOR, -1);
        types = new VcfInfoType[fieldNames.length];
        multipleValues = new boolean[fieldNames.length];
        names2index = new HashMap<>();
        for (int idx = 0; idx < fieldNames.length; idx++) {
            String fieldName = fieldNames[idx].trim();
            fieldNames[idx] = fieldName;
            names2index.put(fieldName, idx);
            updateIndexes(fieldName, idx);
        }

        // Errors?
        if (chromosomeIdx == -1) throw new RuntimeException("Missing '" + COLUMN_CHR_NAME + "' columns in dbNSFP file");
        if (startIdx == -1) throw new RuntimeException("Missing '" + COLUMN_POS_NAME_v2 + "' columns in dbNSFP file");
        if (altIdx == -1) throw new RuntimeException("Missing '" + ALT_NAME + "' columns in dbNSFP file");
        updateChrPosRefAltIndex();
    }

    void updateChrPosRefAltIndex() {
        maxChrPosRefAltIdx = -1;
        maxChrPosRefAltIdx = Math.max(maxChrPosRefAltIdx, chromosomeIdx);
        maxChrPosRefAltIdx = Math.max(maxChrPosRefAltIdx, startIdx);
        maxChrPosRefAltIdx = Math.max(maxChrPosRefAltIdx, refIdx);
        maxChrPosRefAltIdx = Math.max(maxChrPosRefAltIdx, altIdx);
    }

    /**
     * Query tabix file to get dbNsfp entries (or cached entries from latest results)
     *
     * @param variant: Variant to query in DnNSFP
     * @return A list of result
     */
    @Override
    public Collection<DbNsfpEntry> query(Variant variant) {
        LinkedList<DbNsfpEntry> results = new LinkedList<>();

        // Check if we've already retrieved these results last time
        if (latestResultsInterval.includes(variant)) {
            // Match: We can find the results in `latestResults`
            for (DbNsfpEntry de : latestResults)
                if (match(variant, de)) results.add(de);
            return results;
        }

        // Query tabix file
        TabixReader.Iterator tabixIterator = tabixReader.query(variant.getChromosomeName(), variant.getStart(), variant.getEnd() + 1);
        latestResults = new LinkedList<>(); // Clear latest results
        latestResultsInterval = variant; // Update latest results interval
        if (tabixIterator == null) return null; // No results?

        //---
        // Read and parse all entries, select the ones the match query
        //---
        int numLines = 0;
        String line;
        try {
            while ((line = tabixIterator.next()) != null) {
                // Parse
                line = Gpr.removeBackslashR(line);
                if (debug) Log.debug("Query: Parse line " + line);
                DbNsfpEntry de = new DbNsfpEntry(this, line);

                // Add to results & latestResults
                if (match(variant, de)) results.add(de);
                latestResults.add(de);
                numLines++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading tabix file '" + fileName + "'", e);
        }

        if (debug) Log.debug("Query: " + variant.toStr() + "\tParsed lines: " + numLines);

        return results;
    }

    /**
     * Save data types to cache file
     */
    protected void saveDataTypesCache(String cacheFileName) {
        if (verbose) Log.info("Saving data types to file '" + cacheFileName + "'");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fieldNames.length; i++)
            sb.append(fieldNames[i]).append("\t").append(types[i]).append("\t").append(multipleValues[i]).append("\n");

        Gpr.toFile(cacheFileName, sb);
    }

    @Override
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Update indexes
     */
    void updateIndexes(String fieldName, int idx) {
        switch (fieldName.toLowerCase()) {
            case COLUMN_CHR_NAME:
                chromosomeIdx = idx;
                break;

            case COLUMN_POS_NAME_v2:
            case COLUMN_POS_NAME_v3:
                startIdx = idx;
                break;

            case ALT_NAME:
                altIdx = idx;
                break;

            case REF_NAME:
                refIdx = idx;
                break;

            default:
                break;
        }
    }

}
