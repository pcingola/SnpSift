package org.snpsift;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpsift.annotate.mem.database.VariantDatabase;
import org.snpsift.util.ShowProgress;

/**
 * Annotate a VCF file from another VCF file (database)
 * The database file is loaded into memory.
 *
 * @author pcingola
 */
public class SnpSiftCmdAnnotateMem extends SnpSift {

	boolean create; // Create database
	boolean emptyIfNotFound; // If a database file is not found, create an empty database
	List<String> dbFileNames; // Database file names
	List<VariantDatabase> variantDatabases; // Databases
	Map<String, String[]> dbfile2fields; // Database name to fields mapping
	int found = 0, countVcfEntries = 0, annotationsAdded = 0; // Counters for simple statistics
	ShowProgress progress; // Show progress


	public SnpSiftCmdAnnotateMem() {
		super();
	}

	public SnpSiftCmdAnnotateMem(String args[]) {
		super(args);
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
	public void add(String dbFileName, String[] fields) {
		if( !dbFileNames.contains(dbFileName) ) {
			dbFileNames.add(dbFileName);
			String dbDir = dbDir(dbFileName, false);
			Log.info("Adding database direcory: " + dbDir);
			variantDatabases.add(new VariantDatabase(dbDir, emptyIfNotFound));
		}
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
	 * Annotate VCF file
	 *
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	ArrayList<VcfEntry> annotate(boolean createList) {
		ArrayList<VcfEntry> list = (createList ? new ArrayList<VcfEntry>() : null);
		if (verbose) Log.info("Annotating entries from: '" + vcfInputFile + "'");

		VcfFileIterator vcfFile = openVcfInputFile(); // Open input VCF
		try {
			annotateInit(vcfFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int pos = -1;
		String chr = "";
		for (VcfEntry vcfEntry : vcfFile) {
			try {
				processVcfHeader(vcfFile);

				// Check if file is sorted
				if (vcfEntry.getChromosomeName().equals(chr) && vcfEntry.getStart() < pos) {
					Log.error("VCF input file is not sorted!" //
							+ "\n\tPrevious entry " + chr + ":" + pos//
							+ "\n\tCurrent entry  " + vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1)//
					);
				}

				// Annotate variants
				annotate(vcfEntry);

				// Show
				if (!suppressOutput) print(vcfEntry);
				// Collect output. Typically for testing or debugging
				if (list != null) list.add(vcfEntry);

				// Update chr:pos
				chr = vcfEntry.getChromosomeName();
				pos = vcfEntry.getStart();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Finish up
		annotateFinish(vcfFile);
		return list;
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
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
		return annotations > 0;
	}

	public boolean annotateFinish() {
		// Show stats
		var foundPerc = (100.0 * found) / ((double) countVcfEntries);
		Log.info("Done. Processed: " + String.format("%,d", countVcfEntries) + " VCF entries" //
					+ ", found annotations for " + String.format("%,d", found) //
					+ String.format(" ( %.1f %% )", foundPerc) //
					+ ", added " + String.format("%,d", annotationsAdded) + " annotations." //
					+ ", elapsed time: " + progress.elapsedSec() //
		);
		return true;
	}

	/**
	 * Initialize database for annotation process
	 */
	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		// Initialize progress
		progress = new ShowProgress();
		// Create a list of databases to use
		List<VariantDatabase> variantDatabases = new ArrayList<>();
		for(String dbFile: dbFileNames) {
			String dbDir = dbDir(dbFile, true);
			variantDatabases.add(new VariantDatabase(dbDir, emptyIfNotFound));
		}
		return true;
	}

	/**
	 * Create all databases
	 */
	public void create() {
		Log.info("Create databases. " + dbFileNames.size() + " databases to create: " + dbFileNames);
		for(String dbFileName : dbFileNames) {
			String[] fields = dbfile2fields.get(dbFileName);
			Log.info("Create database from file '" + dbFileName + "', fields: " + Arrays.toString(fields));
			// Does the directory exists and it is non-empty?
			var dbDir = dbDir(dbFileName, false);
			var dir = new File(dbDir);
			if(dir.exists() && dir.list().length > 0) {
				Log.fatalError("Create database: Database directory exists and is non-empty '" + dbDir + "'");
			}
			// Create database
			var variantDatabase = new VariantDatabase(fields);
			variantDatabase.create(dbFileName, dbDir);
			}
		Log.info("Create databases: Done!");
		}

	/**
	 * Build headers to add
	 */
	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> headerInfos = super.headers();

		// // Read database header and add INFO fields to the output vcf header
		// if (useInfoField) {
		// 	// Read VCF header
		// 	VcfFileIterator vcfDb = new VcfFileIterator(dbFileName);
		// 	VcfHeader vcfDbHeader = vcfDb.readHeader();

		// 	// Add all corresponding INFO headers
		// 	for (VcfHeaderInfo vcfHeaderDb : vcfDbHeader.getVcfHeaderInfo()) {
		// 		String id = (prependInfoFieldName != null ? prependInfoFieldName : "") + vcfHeaderDb.getId();

		// 		// Get same vcfInfo from file to annotate
		// 		VcfHeaderInfo vcfHeaderFile = vcfFile.getVcfHeader().getVcfHeaderInfo(id);

		// 		// Add header entry only if...
		// 		if (isAnnotateInfo(vcfHeaderDb) // It is used for annotations
		// 				&& !vcfHeaderDb.isImplicit() //  AND it is not an "implicit" header in Db (i.e. created automatically by VcfHeader class)
		// 				&& ((vcfHeaderFile == null) || vcfHeaderFile.isImplicit()) // AND it is not already added OR is already added, but it is implicit
		// 		) {
		// 			VcfHeaderInfo newHeader = new VcfHeaderInfo(vcfHeaderDb);
		// 			if (prependInfoFieldName != null) newHeader.setId(id); // Change ID?
		// 			headerInfos.add(newHeader);
		// 		}
		// 	}
		// }

		// // Using 'exists' flag?
		// if (existsInfoField != null) {
		// 	VcfHeaderInfo existsHeader = new VcfHeaderInfo(existsInfoField, VcfInfoType.Flag, "" + 1, "Variant exists in file '" + Gpr.baseName(dbFileName) + "'");
		// 	headerInfos.add(existsHeader);
		// }

		return headerInfos;
	}

	/**
	 * Initialize
	 */
	@Override
	public void init() {
		super.init();
		emptyIfNotFound = true;
		dbFileNames = new ArrayList<>();
		variantDatabases = new ArrayList<>();
		dbfile2fields = new HashMap<>();
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		String latestDbName = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Command line option?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
					case "-create":
						create = true;
						break;
					case "-dbfile":
						if (args.length > (i + 1)) {
							latestDbName = args[++i];
							add(latestDbName);
						} else usage("Missing parameter for '-dbfile'");
						break;

					case "-fields":
						if (args.length > (i + 1)) {
							if( latestDbName == null ) usage("Missing database file name for '-fields'. Option '-fields' must be precedded by the corresponding '-dbfile' option");
							String[] fields = args[++i].split(",");
							add(latestDbName, fields);
						} else usage("Missing parameter for '-fields'");
						break;

					default:
					usage("Unknown command line option '" + arg + "'");
				}
			} else {
				if (vcfInputFile == null) vcfInputFile = arg;
				else usage("Unknown extra parameter '" + arg + "'");
			}
		}

		// Sanity check
		if (dbFileNames.isEmpty()) usage("Missing database file options: -dbfile file.vcf");
		if(create) {
			if(dbfile2fields.isEmpty()) usage("Missing fields for database creation: -fields field_1,..,field_N");
			// Check that all databases have fields
			for(String dbFileName : dbFileNames) {
				if(!dbfile2fields.containsKey(dbFileName)) usage("Missing fields for database '" + dbFileName + "', e.g: -dbfile '" + dbFileName + "' -fields field_1,..,field_N");
			}
		} else {
			if(vcfInputFile == null) usage("Missing VCF input file");
		}
	}

	/**
	 * Annotate each entry of a VCF file
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run annotations
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	public List<VcfEntry> run(boolean createList) {
		if(create) {
			create();
			return null;
		} else {
			return annotate(createList);
		}
	}

	/**
	 * Show usage message
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage:");
		System.err.println("\tCreate databases:");
		System.err.println("\t           java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " \\");
		System.err.println("\t             -create \\");
		System.err.println("\t             -dbfile database_1.vcf -fields field_1,field_2,...,field_N \\");
		System.err.println("\t             -dbfile database_2.vcf -fields field_1,field_2,...,field_N \\");
		System.err.println("\t             -dbfile database_N.vcf -fields field_1,field_2,...,field_N");
		System.err.println("\n\tAnnotate:");
		System.err.println("\t           java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " \\");
		System.err.println("\t             -dbfile database_1.vcf \\");
		System.err.println("\t             -dbfile database_2.vcf \\");
		System.err.println("\t             -dbfile database_N.vcf \\");
		System.err.println("\t             [input.vcf] > output.vcf \\");
		System.err.println("\nCommand Options:");
		System.err.println("\t-create                       : Create a database from the VCF file.");
		System.err.println("\t-dbfile file.vcf              : Use VCF file (either to create a database or to annotate).");
		System.err.println("\t-fields field_1,..,field_N    : Use VCF info fields when creating the database. Comma separated list, no spaces. Only for create command.");
		System.err.println("Note: When annotating, if 'input.vcf' is not provided, reads from STDIN.");
		System.err.println("Note: VCF files can be compressed with Gzip / Bgzip");
		System.exit(1);
	}

}
