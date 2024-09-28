package org.snpsift.annotate.mem.database;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Marker;
import org.snpeff.util.Log;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.SortedVariantsVcfIterator;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounters;
import org.snpsift.util.ShowProgress;

/**
 * A database of variant's data used to annotate a VCF file (i.e. VCF entries).
 * The database only loads one chromosome at a time, to speed up the process while fitting in memory.
 * 
 * The database is a collection of VariantDatabaseChr objects, one per chromosome. Each 'VariantDatabaseChr' is
 * stored in one file per chromosome.
 * 
 * 'VariantDatabase' manages the 'VariantDatabaseChr' files (loading, saving, etc).
 */
public class VariantDatabase {
	public static final String VARIANT_DATABASE_EXT = "snpsift.vardb";	// Database file extension
	public static final String VARIANT_DATAFRAME_EXT = "snpsift.df";	// Database file extension
	public static final String FIELDS_EXT = "snpsift.db_fields";	// Fields file

	String chr; // Current chromosome
	Marker currentInterval; // Current interval
	String databaseVcfFileName; // Original VCF file from which the db is reated
	String dbDir; // Directory where databases are stored
	boolean emptyIfNotFound; // If a database file is not found, create an empty one
	Fields fields; // Fields to create or annotate
	String fieldNamesAnnotate[]; // Fields to annotate entries (may be a subset of 'fieldNamesCreate')
	String fieldNamesCreate[]; // Fields to create the database
	String prefix; // Prefix for inf field names added
	VariantDataFrame variantDataFrame; // Database for current chromosome
	VariantTypeCounters variantTypeCounters; // Counters per chromosome
	boolean verbose = false; // Be verbose

	/**
	 * Get the database directory name from a "VCF database" file name buy just appending ".snpsift.vardb"
	 */
	public static String dbDirFromVcfFileName(String dbFileName) {
		return dbFileName + '.' + VariantDatabase.VARIANT_DATABASE_EXT;
	}

	public VariantDatabase() {
		this.chr = null;
		this.dbDir = null;
		this.variantDataFrame = null;
		this.currentInterval = null;
		this.fieldNamesCreate = null;
		this.fieldNamesAnnotate = null;
		this.fields = null;
		this.variantTypeCounters = null;
	}

	/**
	 * Constructor used to create a database
	 */
	public VariantDatabase(String databaseVcfFileName, String dbDir, String[] fieldNamesCreate) {
		this();
		this.databaseVcfFileName = databaseVcfFileName;
		this.dbDir = dbDir;
		this.fieldNamesCreate = fieldNamesCreate;
	}

	/**
	 * Constructor used to annotate a database
	 */
	public VariantDatabase(String databaseVcfFileName, String dbDir, String[] fieldNamesAnnotate, String prefix, boolean emptyIfNotFound) {
		this();
		this.databaseVcfFileName = databaseVcfFileName;
		this.dbDir = dbDir;
		this.fieldNamesAnnotate = fieldNamesAnnotate;
		this.prefix = prefix;
		this.emptyIfNotFound = emptyIfNotFound;
	}

	/**
	 * Add a VCF entry to the database
	 */
	protected void add(VariantVcfEntry variantVcfEntry) {
		// Same chromosome? => Add to current database
		var chr = variantVcfEntry.getChromosomeName();
		if(!chr.equals(this.chr)) {
			// Different chromosome? => Save current database and create a new one
			if(variantDataFrame != null) variantDataFrame.save(dbDir + "/" + this.chr + '.' + VARIANT_DATAFRAME_EXT);
			this.chr = chr;
			var vcounter = variantTypeCounters.get(chr);
			if(vcounter == null) throw new RuntimeException("Cannot find variant type counters for chromosome: '" + chr + "'");
			variantDataFrame = new VariantDataFrame(vcounter);
		}
		variantDataFrame.add(variantVcfEntry);
	}

	/**
	 * This method is used to annotate a VCF entry
	 * The annotations are added to the INFO field of the VCF entry
	 */
	public int annotate(VcfEntry vcfEntry) {
		var chr = vcfEntry.getChromosomeName();
		VariantDataFrame df = get(chr);
		// return db.annotate(vcfEntry);
		return df.annotate(vcfEntry, fieldNamesAnnotate);
	}

	/**
	 * Check that all `fieldNames` are available in `fields`
	 * @return true is all fieldsNames are present, false otherwise
	 */
	public boolean checkFields(String[] fieldNames, boolean throwExceptionOnError) {
		for(String fieldName: fieldNames) {
			if( fields.get(fieldName) == null) {
				if( throwExceptionOnError ) throw new RuntimeException("Field '" + fieldName + "' not found in database.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Create a database from a VCF file content (as a string)
	 * This is used for testing
	 */
	public void create(String vcfContents) {
		// Get column types
		fields = parseVcfHeaderFields(VcfFileIterator.fromString(vcfContents));
		checkFields(fieldNamesCreate, true);
		// Count variants
		variantTypeCounters = new VariantTypeCounters(fields, verbose);
		variantTypeCounters.count(VcfFileIterator.fromString(vcfContents));
		// Load data
		var sortedVariants = SortedVariantsVcfIterator.lines2SortedVariantsVcfIterator(vcfContents);
		createFromVcf(sortedVariants);
		sortedVariants.close();
	}

	/**
	 * Create a database from a VCF file
	 */
	public void create() {
		// Create directory
		var dir = new File(dbDir);
		if(!dir.exists()) dir.mkdirs();
		// Parse headers and get column types
		fields = parseVcfHeaderFields(databaseVcfFileName);
		checkFields(fieldNamesCreate, true);
		// Count the number of entries in the VCF file		
		variantTypeCounters = new VariantTypeCounters(fields, verbose);
		variantTypeCounters.count(databaseVcfFileName);
		// Load data from VCF file
		if( verbose ) Log.info("Creating variant database from VCF file '" + databaseVcfFileName + "'");
		var sortedVariants = new SortedVariantsVcfIterator(databaseVcfFileName);
		createFromVcf(sortedVariants);
		sortedVariants.close();
		// Make sure we save the last dataFrame
		if(variantDataFrame != null) variantDataFrame.save(dbDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT);
		// Save some database parameters
		save();
	}

	/**
	 * Creat database from a SortedVariantsVcfIterator
	 */
	void createFromVcf(SortedVariantsVcfIterator sortedVariants) {
		// Iterate over all VCF entries
		var i = 0; // Current entry number
		var progress = new ShowProgress();
		for (var variantVcf : sortedVariants) {
			add(variantVcf);
			i++;
			progress.tick(i, variantVcf); // Show progress
		}
		if( verbose ) Log.info("\nDone: " + i + " variants in " + progress.elapsedSec() + " seconds.");
	}

	/**
	 * Get the database for a chromosome
	 */
	public VariantDataFrame get(String chr) {
		if(chr.equals(this.chr)) return variantDataFrame;
		// Load from database file
		this.chr = chr;
		var variantDataFrameFile = dbDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT;
		if( verbose ) Log.info("Loading data frame from file: " + variantDataFrameFile);
		variantDataFrame = VariantDataFrame.load(variantDataFrameFile, emptyIfNotFound);
		variantDataFrame.setPrefix(prefix); // Propagate prefix to the df
		return variantDataFrame;
	}

	public String getDatabaseVcfFileName() {
		return databaseVcfFileName;
	}
	
	public String getDbDir() { 
		return dbDir;
	}

	public Fields getFields() {
		return fields;
	}

	public VariantTypeCounters getVariantTypeCounters() {
		return variantTypeCounters;
	}

	public void load() {
		this.fields = Fields.load(dbDir + "/fields." + FIELDS_EXT);
	}

	/**
	 * Read VCF header and get columns data types
	 * @param databaseVcfFileName
	 */
	protected Fields parseVcfHeaderFields(String databaseVcfFileName) {
		var vcfFile = new VcfFileIterator(databaseVcfFileName);
		return parseVcfHeaderFields(vcfFile);
	}

	/**
	 * Read VCF header and get columns data types
	 * @param databaseVcfFileName
	 */
	protected Fields parseVcfHeaderFields(VcfFileIterator vcfFileIterator) {
		var fields = new Fields();
		var fnames = Arrays.asList(fieldNamesCreate);
		Set<String> fieldNamesSet = new HashSet<String>(fnames);
		// Read VCF header
		var vcfHeader = vcfFileIterator.readHeader();
		// For each field in the VCF header, decide which column type we'll use
		for(var vcfInfo : vcfHeader.getVcfHeaderInfo()) {
			// Skip implicit fields
			if(vcfInfo.isImplicit()) continue;
			// Check if field name is in the list of fields to extract. if not found, skip this field
			if(! fieldNamesSet.contains(vcfInfo.getId())) continue;
			// Add field
			fields.add(vcfInfo);
		}
		vcfFileIterator.close();
		return fields;
	}

	public void save() {
		fields.save(dbDir + "/fields." + FIELDS_EXT);
	}

	public void setDbDir(String dbDir) {
		this.dbDir = dbDir;
	}

	public void setFieldNamesAnnotate(String[] fieldNamesAnnotate) {
		this.fieldNamesAnnotate = fieldNamesAnnotate;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		if( variantDataFrame != null ) variantDataFrame.setPrefix(prefix);
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VariantDatabase[chr: " + chr + "]\n");
		sb.append(variantDataFrame);
		 return sb.toString();
	}

	public Collection<VcfHeaderEntry> vcfHeaders() {
		return fields.vcfHeaders(prefix);
	}

}

