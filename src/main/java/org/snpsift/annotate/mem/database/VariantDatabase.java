package org.snpsift.annotate.mem.database;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounters;
import org.snpsift.util.ShowProgress;

/**
 * A database of variant's data.
 * The database is stored in a file per chromosome, this class manages the database files (loading, saving, etc).
 * The database is a collection of VariantDatabaseChr objects, one per chromosome.
 */
public class VariantDatabase {
	public static final String DB_EXT = ".ssdb";	// Database file extension

	String chr; // Current chromosome
	String dbDir; // Directory where databases are stored
	String[] fields; // Fields to create or annotate
	VariantDatabaseChr db; // Database for current chromosome
	VariantTypeCounters variantTypeCounters; // Counters per chromosome

	public VariantDatabase() {
		db = null;
		variantTypeCounters = null;
	}

	/**
	 * Constructor
	 * @param counter
	 */
	public VariantDatabase(VariantTypeCounters variantTypeCounters, String[] fields) {
		db = null;
		this.variantTypeCounters = variantTypeCounters;
		this.fields = fields;
	}

	void add(VcfEntry vcfEntry) {
		// Same chromosome? => Add to current database
		var chr = vcfEntry.getChromosomeName();
		if(chr.equals(this.chr)) {
			db.add(vcfEntry, fields);
			return;
		}
		// Different chromosome? => Save current database and create a new one
		if(db != null) db.save(dbDir + "/" + this.chr + DB_EXT);
		this.chr = chr;
		db = new VariantDatabaseChr(variantTypeCounters.counters.get(chr), fields);
		db.add(vcfEntry, fields);
	}

	public void createDb(String databaseFileName) {
		// Load data
		System.out.println("Loading db from file: " + databaseFileName);
		var vcfFile = new VcfFileIterator(databaseFileName);
		var i = 0; // Current entry number
		var progress = new ShowProgress();
		for (var vcfEntry : vcfFile) {
			add(vcfEntry);
			progress.tick(i, vcfEntry); // Show progress
			i++;
		}
		System.out.println("Done: " + i + " entries.");
	}

	/**
	 * Get the database for a chromosome
	 */
	VariantDatabaseChr get(String chr) {
		if(chr.equals(this.chr)) return db;
		// Load from database file
		this.chr = chr;
		var dbFile = dbDir + "/" + chr + DB_EXT;
		db = VariantDatabaseChr.load(dbFile);
		return db;
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
}

