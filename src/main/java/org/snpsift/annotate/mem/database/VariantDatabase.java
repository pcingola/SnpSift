package org.snpsift.annotate.mem.database;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Marker;
import org.snpeff.util.Log;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.Field;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.annotate.mem.SortedVariantsVcfIterator;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounters;
import org.snpsift.util.FormatUtil;
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
	public static final String VARIANT_DATAFRAME_EXT = "snpsift_db";	// Database file extension

	String chr; // Current chromosome
	String dfDir; // Directory where databases are stored
	boolean emptyIfNotFound; // If a database file is not found, create an empty one
	Fields fields; // Fields to create or annotate
	String fieldNames[]; // Fields to create or annotate
	Marker currentInterval; // Current interval
	VariantDataFrame variantDataFrame; // Database for current chromosome
	VariantTypeCounters variantTypeCounters; // Counters per chromosome

	public VariantDatabase() {
		this.chr = null;
		this.dfDir = null;
		this.variantDataFrame = null;
		this.currentInterval = null;
		this.fieldNames = null;
		this.fields = null;
		this.variantTypeCounters = null;
	}

	/**
	 * Constructor used to create a database
	 */
	public VariantDatabase(String[] fieldNames) {
		this();
		this.fieldNames = fieldNames;
	}

	public VariantDatabase(String dfDir, boolean emptyIfNotFound) {
		this();
		this.dfDir = dfDir;
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
			if(variantDataFrame != null) variantDataFrame.save(dfDir + "/" + this.chr + '.' + VARIANT_DATAFRAME_EXT);
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
		var db = get(chr);
		return db.annotate(vcfEntry);
	}

	/**
	 * Read VCF header and get columns data types
	 * @param databaseFileName
	 */
	protected Fields parseFields(String databaseFileName) {
		var vcfFile = new VcfFileIterator(databaseFileName);
		return parseFields(vcfFile);
	}

	/**
	 * Read VCF header and get columns data types
	 * @param databaseFileName
	 */
	protected Fields parseFields(VcfFileIterator vcfFileIterator) {
		var fields = new Fields();
		var fnames = Arrays.asList(fieldNames);
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
			fields.add(new Field(vcfInfo));
		}
		vcfFileIterator.close();
		return fields;
	}

	/**
	 * Create a database from a VCF file content (as a string)
	 * This is used for testing
	 */
	public void create(String vcfContents) {
		// Get column types
		fields = parseFields(FormatUtil.lines2VcfFileIterator(vcfContents));
		// Count variants
		variantTypeCounters = new VariantTypeCounters(fields);
		variantTypeCounters.count(FormatUtil.lines2VcfFileIterator(vcfContents));
		// Load data
		var sortedVariants = SortedVariantsVcfIterator.lines2SortedVariantsVcfIterator(vcfContents);
		createFromVcf(sortedVariants);
		sortedVariants.close();
	}

	/**
	 * Create a database from a VCF file
	 */
	public void create(String databaseFileName, String dfDir) {
		this.dfDir = dfDir;
		// Create directory
		var dir = new File(dfDir);
		if(!dir.exists()) dir.mkdirs();
		// Get column types
		fields = parseFields(databaseFileName); 
		// Count the number of entries in the VCF file		
		variantTypeCounters = new VariantTypeCounters(fields);
		variantTypeCounters.count(databaseFileName);
		// Load data
		Log.info("Creating variant database from file '" + databaseFileName + "'");
		var sortedVariants = new SortedVariantsVcfIterator(databaseFileName);
		createFromVcf(sortedVariants);
		sortedVariants.close();
		// Make sure we save the last database
		if(variantDataFrame != null) variantDataFrame.save(dfDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT);
	}

	/**
	 * Creat database from a SortedVariantsVcfIterator
	 */
	public void createFromVcf(SortedVariantsVcfIterator sortedVariants) {
		// Iterate over all VCF entries
		var i = 0; // Current entry number
		var progress = new ShowProgress();
		for (var variantVcf : sortedVariants) {
			add(variantVcf);
			i++;
			progress.tick(i, variantVcf); // Show progress
		}
		Log.info("\nDone: " + i + " variants in " + progress.elapsedSec() + " seconds.");
	}

	/**
	 * Get the database for a chromosome
	 */
	public VariantDataFrame get(String chr) {
		if(chr.equals(this.chr)) return variantDataFrame;
		// Load from database file
		this.chr = chr;
		var variantDataFrameFile = dfDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT;
		Log.info("Loading data frame from file: " + variantDataFrameFile);
		variantDataFrame = VariantDataFrame.load(variantDataFrameFile, emptyIfNotFound);
		return variantDataFrame;
	}

	public Fields getFields() {
		return fields;
	}

	public VariantTypeCounters getVariantTypeCounters() {
		return variantTypeCounters;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VariantDatabase[chr: " + chr + "]\n");
		sb.append(variantDataFrame);
		 return sb.toString();
	}
}

