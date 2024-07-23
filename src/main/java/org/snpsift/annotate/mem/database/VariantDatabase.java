package org.snpsift.annotate.mem.database;

import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;
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
	public static final String DB_EXT = "snpsift_db";	// Database file extension

	String chr; // Current chromosome
	String dbDir; // Directory where databases are stored
	String[] fields; // Fields to create or annotate
	Map<String, VcfInfoType> fields2type; // Fields to create or annotate
	VariantDataFrame db; // Database for current chromosome
	VariantTypeCounters variantTypeCounters; // Counters per chromosome

	/**
	 * Constructor used to create a database
	 */
	public VariantDatabase(String[] fields) {
		this.fields = fields;
		this.dbDir = null;
		this.db = null;
		this.chr = null;
		this.fields2type = null;
		this.variantTypeCounters = null;
	}

	/**
	 * Add a VCF entry to the database
	 */
	void add(VcfEntry vcfEntry) {
		// Same chromosome? => Add to current database
		var chr = vcfEntry.getChromosomeName();
		if(!chr.equals(this.chr)) {
			// Different chromosome? => Save current database and create a new one
			if(db != null) db.save(dbDir + "/" + this.chr + '.' + DB_EXT);
			this.chr = chr;
			var vcounter = variantTypeCounters.get(chr);
			if(vcounter == null) throw new RuntimeException("Cannot find variant type counters for chromosome: '" + chr + "'");
			db = new VariantDataFrame(vcounter, fields2type);
		}
		db.add(vcfEntry);
	}

	/**
	 * This method is used to annotate a VCF entry
	 * The annotations are added to the INFO field of the VCF entry
	 */
	void annotate(VcfEntry vcfEntry) {
		var chr = vcfEntry.getChromosomeName();
		var db = get(chr);
		db.annotate(vcfEntry, fields);
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
			System.out.println("Added field: " + vcfInfo.getId() + " type: " + fields2type.get(vcfInfo.getId()));
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
	public void createDb(String databaseFileName, String dbDir) {
		this.dbDir = dbDir;
		// Create directory
		var dir = new java.io.File(dbDir);
		if(!dir.exists()) dir.mkdirs();
		// Get column types
		fields2type = columnTypes(databaseFileName); 
		// Count the number of entries in the VCF file		
		variantTypeCounters = new VariantTypeCounters(fields2type);
		variantTypeCounters.count(databaseFileName);
		// Load data
		loadDbData(databaseFileName);
		// Make sure we save the last database
		if(db != null) db.save(dbDir + "/" + chr + '.' + DB_EXT);
	}

	/**
	 * Get the database for a chromosome
	 */
	VariantDataFrame get(String chr) {
		if(chr.equals(this.chr)) return db;
		// Load from database file
		this.chr = chr;
		var dbFile = dbDir + "/" + chr + '.' + DB_EXT;
		db = VariantDataFrame.load(dbFile);
		return db;
	}

	/**
	 * Load database from a VCF file
	 */
	void loadDbData(String databaseFileName) {
		System.out.println("Loading db from file: " + databaseFileName);
		// Iterate over all VCF entries
		var vcfFile = new VcfFileIterator(databaseFileName);
		var i = 0; // Current entry number
		var progress = new ShowProgress();
		for (var vcfEntry : vcfFile) {
			add(vcfEntry);
			progress.tick(i, vcfEntry); // Show progress
			i++;
		}
		vcfFile.close();
		System.out.println("Done: " + i + " entries.");
	}
}

