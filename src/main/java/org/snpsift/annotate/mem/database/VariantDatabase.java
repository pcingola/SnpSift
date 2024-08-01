package org.snpsift.annotate.mem.database;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Marker;
import org.snpeff.interval.tree.IntervalForest;
import org.snpeff.util.Log;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;
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
	public static final String VARIANT_DATAFRAME_EXT = "snpsift_db";	// Database file extension

	String chr; // Current chromosome
	String dfDir; // Directory where databases are stored
	boolean emptyIfNotFound; // If a database file is not found, create an empty one
	String[] fields; // Fields to create or annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	Marker currentInterval; // Current interval
	VariantDataFrame variantDataFrame; // Database for current chromosome
	VariantTypeCounters variantTypeCounters; // Counters per chromosome
	IntervalForest intervalForest; // Interval forest of the DataFrame's intervals

	/**
	 * Constructor used to create a database
	 */
	public VariantDatabase(String[] fields) {
		this.fields = fields;
		this.chr = null;
		this.dfDir = null;
		this.variantDataFrame = null;
		this.currentInterval = null;
		this.fields2type = null;
		this.variantTypeCounters = null;
	}

	public VariantDatabase(String dfDir, boolean emptyIfNotFound) {
		this.dfDir = dfDir;
		this.emptyIfNotFound = emptyIfNotFound;
		this.fields = null;
		this.chr = null;
		this.variantDataFrame = null;
		this.currentInterval = null;
		this.fields2type = null;
		this.variantTypeCounters = null;
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
	 * Decide which column data type we'll use for a VcfField
	 * If it's a single value we can use a "primitive" type DataColumn, otherwise we'll use a "StrignColumn"
	 */
	VcfInfoType columnType(VcfHeaderInfo vcfInfo) {
		VcfInfoType vcfFieldType = vcfInfo.getVcfInfoType();
		// Multiple values? => Use a "StringColumn"
		if (vcfInfo.getVcfInfoNumber() != VcfInfoNumber.NUMBER ||  vcfInfo.getNumber() > 1) return VcfInfoType.String;
		// Unknown type? => Use a "StringColumn"
		if (vcfInfo.getVcfInfoType() == VcfInfoType.UNKNOWN) return VcfInfoType.String;
		// Single value, we can use a "primitive" type DataColumn
		return vcfFieldType;
	}

	/**
	 * Read VCF header and get columns data types
	 * @param databaseFileName
	 */
	Map<String, VcfInfoType>  columnTypes(String databaseFileName) {
		// Initialize fields, add type 'null'
		Map<String, VcfInfoType> fields2type = new HashMap<>();
		for(String field: fields) 
			fields2type.put(field, null);
		// Read VCF header
		var vcfFile = new VcfFileIterator(databaseFileName);
		var vcfHeader = vcfFile.readHeader();
		// For each field in the VCF header, decide which column type we'll use
		for(var vcfInfo : vcfHeader.getVcfHeaderInfo()) {
			// Skip implicit fields
			if(!vcfInfo.isImplicit()) continue;
			// Check if field name is in the list of fields to extract. if not found, skip this field
			if(! fields2type.containsKey(vcfInfo.getId())) continue;
			// Add field
			fields2type.put(vcfInfo.getId(), columnType(vcfInfo));
		}
		vcfFile.close();
		// If the fields type is still 'null', set it to 'String'
		for(String field: fields) {
			if(fields2type.get(field) == null) fields2type.put(field, VcfInfoType.String);
		}
		return fields2type;
	}

	/**
	 * Create a database from a VCF file
	 */
	public void create(String databaseFileName, String dfDir) {
		this.dfDir = dfDir;
		// Create directory
		var dir = new java.io.File(dfDir);
		if(!dir.exists()) dir.mkdirs();
		// Get column types
		fields2type = columnTypes(databaseFileName); 
		// Count the number of entries in the VCF file		
		variantTypeCounters = new VariantTypeCounters(fields2type);
		variantTypeCounters.count(databaseFileName);
		// Load data
		createFromVcf(databaseFileName);
		// Make sure we save the last database
		if(variantDataFrame != null) variantDataFrame.save(dfDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT);
	}

	/**
	 * Creat database from a VCF file
	 */
	void createFromVcf(String databaseFileName) {
		Log.info("Creating variant database from file '" + databaseFileName + "'");
		// Iterate over all VCF entries
		var sortedVariants = new SortedVariantsVcfIterator(databaseFileName);
		var i = 0; // Current entry number
		var progress = new ShowProgress();
		for (var variantVcf : sortedVariants) {
			add(variantVcf);
			i++;
			progress.tick(i, variantVcf); // Show progress
		}
		sortedVariants.close();
		Log.info("\nDone: " + i + " variants in " + progress.elapsedSec() + " seconds.");
	}

	/**
	 * Get the database for a chromosome
	 */
	VariantDataFrame get(String chr) {
		if(chr.equals(this.chr)) return variantDataFrame;
		// Load from database file
		this.chr = chr;
		var variantDataFrameFile = dfDir + "/" + chr + '.' + VARIANT_DATAFRAME_EXT;
		Log.info("Loading data frame from file: " + variantDataFrameFile);
		variantDataFrame = VariantDataFrame.load(variantDataFrameFile, emptyIfNotFound);
		return variantDataFrame;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VariantDatabase[chr: " + chr + "]\n");
		sb.append(variantDataFrame);
		 return sb.toString();
	}
}

