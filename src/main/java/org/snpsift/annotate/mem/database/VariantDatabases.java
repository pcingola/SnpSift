package org.snpsift.annotate.mem.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Log;
import org.snpsift.util.ShowProgress;


/**
 * This class creates and annotates using multiple VariantDatabases
 * 
 * emptyIfNotFound: When the DataFrame file for a chromosome is not found: 
 *     If emptyIfNotFound=true: create an empty database (i.e. silently ignore the error)
 *     If emptyIfNotFound=false: throw an exception
 */
public class VariantDatabases {

	boolean emptyIfNotFound; 
	List<String> dbFileNames; // Database file names
	List<VariantDatabase> variantDatabases; // Databases
	Map<String, List<String>> dbfile2fields; // Database name to fields mapping

	public VariantDatabases() {
		this(true);
	}

	public VariantDatabases(boolean emptyIfNotFound) {
		this.emptyIfNotFound = emptyIfNotFound;
		dbFileNames = new ArrayList<>();
		variantDatabases = new ArrayList<>();
		dbfile2fields = new HashMap<>();
	}

	/**
	 * Add a database file
	 */
	public void add(String dbFileName) {
		add(dbFileName, null);
	}

	/**
	 * Add a database file, and the fields to use
	 */
	public void add(String dbFileName, List<String> fields) {
		dbFileNames.add(dbFileName);
		String dbDir = dbDir(dbFileName, false);
		Log.info("Adding database direcory: " + dbDir);
		variantDatabases.add(new VariantDatabase(dbDir, emptyIfNotFound));
		if(fields != null) dbfile2fields.put(dbFileName, fields);
	}

	/**
	 * Get the database directory name from a database file name
	 */
	String dbDir(String dbFileName, boolean check) {
		String dbDir = dbFileName + '_' + VariantDatabase.VARIANT_DATAFRAME_EXT;
		if(check) {
			var dbDirFile =  new File(dbDir);
			if( !dbDirFile.exists() ) throw new RuntimeException("Database directory not found: '" + dbDir + "', directory path inferred from database file: '" + dbFileName + "'");
		}
		return dbDir;
	}

	/**
	 * Annotate a VCF file
	 */
	public void annotate(String vcfInput) {
		Log.info("Annotating file: " + vcfInput);

		// Create a list of databases to use
		List<VariantDatabase> variantDatabases = new ArrayList<>();
		for(String dbFile: dbFileNames) {
			String dbDir = dbDir(dbFile, true);
			variantDatabases.add(new VariantDatabase(dbDir, emptyIfNotFound));
		}

		// Annotate each VcfEntry
		int found = 0, countVcfEntries = 0, annotationsAdded = 0;
		var progress = new ShowProgress();
		for(var vcfEntry : new VcfFileIterator(vcfInput)) {
			var annotations = 0;
			for(var variantDatabase : variantDatabases) {
				annotations += variantDatabase.annotate(vcfEntry);
			}

			// Update counters
			countVcfEntries++;
			annotationsAdded += annotations;
			if( annotations > 0 ) found++;
			
			// Show progress
			progress.tick(countVcfEntries, vcfEntry);
		}

		// Show stats
		var foundPerc = (100.0 * found) / ((double) countVcfEntries);
		Log.info("Done. Processed: " + String.format("%,d", countVcfEntries) + " VCF entries" //
					+ ", found annotations for " + String.format("%,d", found) //
					+ String.format(" ( %.1f %% )", foundPerc) //
					+ ", added " + String.format("%,d", annotationsAdded) + " annotations." //
					+ ", elapsed time: " + progress.elapsedSec() //
		);
	}

	/**
	 * Create all databases
	 */
	public void create() {
		for(String dbFileName : dbFileNames) {
			// Does the directory exists and it is non-empty?
			var dbDir = dbDir(dbFileName, false);
			var dir = new File(dbDir);
			if(dir.exists() && dir.list().length > 0) {
				Log.info("Create database: Database directory exists and is non-empty, skipping: '" + dbDir + "'");
				continue;
			}
			// Create database
			List<String> fields = dbfile2fields.get(dbFileName);
			var variantDatabase = new VariantDatabase(fields.toArray(new String[0]));
			variantDatabase.create(dbFileName, dbDir);
			}
	}
}
