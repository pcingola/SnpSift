package org.snpsift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.AnnotateVcfDb;
import org.snpsift.annotate.AnnotateVcfDbMem;
import org.snpsift.annotate.AnnotateVcfDbSorted;
import org.snpsift.annotate.AnnotateVcfDbTabix;
import org.snpsift.annotate.VcfIndexTree;

/**
 * Annotate a VCF file with ID from another VCF file (database)

 *
 * @author pcingola
 *
 */
public class SnpSiftCmdAnnotate extends SnpSift {

	enum AnnotationMethod {
		SORTED_VCF, MEMORY, TABIX,
	}

	public static final int SHOW = 100;

	protected boolean annotateEmpty; // Annotate empty fields as well?
	protected boolean useId; // Annotate ID fields
	protected boolean useInfoField; // Use all info fields
	protected boolean useRefAlt;
	protected AnnotationMethod method;
	protected int countBadRef = 0;
	protected int maxBlockSize;
	int countAnnotated = 0, count = 0;
	protected String chrPrev = "";
	protected String prependInfoFieldName;
	protected String existsInfoField;
	protected ArrayList<String> infoFields; // Use only info fields
	protected VcfFileIterator vcfFile;
	protected AnnotateVcfDb annotateDb;

	public SnpSiftCmdAnnotate(String args[]) {
		super(args, "annotate");
	}

	protected SnpSiftCmdAnnotate(String args[], String command) {
		super(args, command);
	}

	/**
	 * Annotate VCF file
	 *
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	ArrayList<VcfEntry> annotate(boolean createList) {
		ArrayList<VcfEntry> list = (createList ? new ArrayList<VcfEntry>() : null);
		if (verbose) Timer.showStdErr("Annotating entries from: '" + vcfInputFile + "'");

		vcfFile = openVcfInputFile(); // Open input VCF
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
					System.err.println("WARNING: VCF input file is not sorted!" //
							+ "\n\tPrevious entry " + chr + ":" + pos//
							+ "\n\tCurrent entry  " + vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1)//
					);
				}

				// Annotate variants
				annotate(vcfEntry);
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

		// Show some statistics
		if (verbose) {
			double perc = (100.0 * countAnnotated) / count;
			Timer.showStdErr("Done." //
					+ "\n\tTotal annotated entries : " + countAnnotated //
					+ "\n\tTotal entries           : " + count //
					+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
					+ "\n\tErrors (bad references) : " + countBadRef //
			);
		}

		return list;
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		boolean annotated = false;

		if (vcfEntry.isVariant()) {
			try {
				annotated = annotateDb.annotate(vcfEntry);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		// Show
		if (!suppressOutput) print(vcfEntry);

		if (annotated) countAnnotated++;
		count++;
		if (verbose) Gpr.showMark(count, SHOW);

		return annotated;
	}

	/**
	 * Initialize database for annotation process
	 */
	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		this.vcfFile = vcfFile;

		// Find or download database
		dbFileName = databaseFind();

		// Guess annotation method if none is provided
		method = guessAnnotationMethod();

		dbFileName = fixDbName();
		if (verbose) Timer.showStdErr("Annotating\n" //
				+ (vcfInputFile != null ? "\tInput file    : '" + vcfInputFile + "'\n" : "") //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		if (verbose) Timer.showStdErr("Annotating method: " + method);

		// Create annotateDb object
		switch (method) {

		case MEMORY:
			annotateDb = new AnnotateVcfDbMem(dbFileName);
			break;

		case SORTED_VCF:
			annotateDb = new AnnotateVcfDbSorted(dbFileName, maxBlockSize);
			break;

		case TABIX:
			annotateDb = new AnnotateVcfDbTabix(dbFileName);
			break;

		default:
			throw new RuntimeException("Unknwon method '" + method + "'");
		}

		// Set parameters & open database file
		annotateDb.setAnnotateEmpty(annotateEmpty);
		annotateDb.setUseId(useId);
		annotateDb.setUseRefAlt(useRefAlt);
		annotateDb.setInfoFields(useInfoField, infoFields);
		annotateDb.setExistsInfoField(existsInfoField);
		annotateDb.setPrependInfoFieldName(prependInfoFieldName);
		annotateDb.setDebug(debug);
		annotateDb.setVerbose(verbose);
		annotateDb.open();// Open database

		return false;
	}

	String fixDbName() {
		// For tabix databases, if the 'gz' file exists, try opening that one instead
		if (method == AnnotationMethod.TABIX //
				&& !dbFileName.endsWith(".gz") //
				&& Gpr.exists(dbFileName + ".gz") //
		) return dbFileName + ".gz";

		return dbFileName;
	}

	/**
	 * Guess annotation (if none is provided) method and check database file
	 */
	AnnotationMethod guessAnnotationMethod() {
		if (method != null) return method;

		if (dbFileName.endsWith(".gz") //
				&& (Gpr.exists(dbFileName + ".tbi") || Gpr.exists(dbFileName + ".gz.tbi")))
			return AnnotationMethod.TABIX;
		return AnnotationMethod.SORTED_VCF;
	}

	/**
	 * Build headers to add
	 */
	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> headerInfos = super.headers();

		// Read database header and add INFO fields to the output vcf header
		if (useInfoField) {
			// Read VCF header
			VcfFileIterator vcfDb = new VcfFileIterator(dbFileName);
			VcfHeader vcfDbHeader = vcfDb.readHeader();

			// Add all corresponding INFO headers
			for (VcfHeaderInfo vcfHeaderDb : vcfDbHeader.getVcfInfo()) {
				String id = (prependInfoFieldName != null ? prependInfoFieldName : "") + vcfHeaderDb.getId();

				// Get same vcfInfo from file to annotate
				VcfHeaderInfo vcfHeaderFile = vcfFile.getVcfHeader().getVcfInfo(id);

				// Add header entry only if...
				if (isAnnotateInfo(vcfHeaderDb) // It is used for annotations
						&& !vcfHeaderDb.isImplicit() //  AND it is not an "implicit" header in Db (i.e. created automatically by VcfHeader class)
						&& ((vcfHeaderFile == null) || vcfHeaderFile.isImplicit()) // AND it is not already added OR is already added, but it is implicit
				) {
					VcfHeaderInfo newHeader = new VcfHeaderInfo(vcfHeaderDb);
					if (prependInfoFieldName != null) newHeader.setId(id); // Change ID?
					headerInfos.add(newHeader);
				}
			}
		}

		// Using 'exists' flag?
		if (existsInfoField != null) {
			VcfHeaderInfo existsHeader = new VcfHeaderInfo(existsInfoField, VcfInfoType.Flag, "" + 1, "Variant exists in file '" + Gpr.baseName(dbFileName) + "'");
			headerInfos.add(existsHeader);
		}

		return headerInfos;
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		useInfoField = true; // Default: Use INFO fields
		useId = true; // Annotate ID fields
		useRefAlt = true; // Use REF and ALT fields when comparing
		method = null; // Guess annotation method

		needsConfig = true;
		needsDb = true;
		dbTabix = true;

		maxBlockSize = VcfIndexTree.DEFAULT_MAX_BLOCK_SIZE;
	}

	/**
	 * Are we annotating using this info field?
	 */
	boolean isAnnotateInfo(VcfHeaderInfo vcfInfo) {
		// All fields selected?
		if (infoFields == null) return true;

		// Check if specified field is present
		for (String info : infoFields)
			if (vcfInfo.getId().equals(info)) return true;

		return false;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Command line option?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-a":
					annotateEmpty = true;
					break;

				case "-clinvar":
					dbType = "clinvar";
					method = AnnotationMethod.TABIX;
					break;

				case "-dbsnp":
					dbType = "dbsnp";
					method = AnnotationMethod.TABIX;
					break;

				case "-exists":
					if (args.length > (i + 1)) existsInfoField = args[++i];
					else usage("Missing parameter -exists");
					break;

				case "-id":
					useId = true;
					break;

				case "-info":
					if (args.length <= (i + 1)) usage("Missing parameter -info");
					useInfoField = true;

					infoFields = new ArrayList<String>();
					for (String infoField : args[++i].split(","))
						infoFields.add(infoField);
					break;

				case "-maxblocksize":
					if (args.length > (i + 1)) maxBlockSize = Gpr.parseIntSafe(args[++i]);
					else usage("Missing parameter -maxBlockSize");
					break;

				case "-mem":
					method = AnnotationMethod.MEMORY; // This should only be used for test cases (not in productions environments)
					break;

				case "-name":
					if (args.length > (i + 1)) prependInfoFieldName = args[++i];
					else usage("Missing parameter -name");
					break;

				case "-noalt":
					useRefAlt = false;
					break;

				case "-noid":
					useId = false;
					break;

				case "-noinfo":
					useInfoField = false;
					break;

				case "-sorted":
					method = AnnotationMethod.SORTED_VCF;
					break;

				case "-tabix":
					method = AnnotationMethod.TABIX;
					break;

				default:
					usage("Unknown command line option '" + arg + "'");
				}
			} else {
				if (dbType == null && dbFileName == null) dbFileName = arg;
				else if (vcfInputFile == null) vcfInputFile = arg;
				else usage("Unknown extra parameter '" + arg + "'");
			}
		}

		// Sanity check
		if (dbType == null && dbFileName == null)

		usage("Missing database option or file: [-dbSnp | -clinVar | database.vcf ]");
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
		// Read config
		if (config == null) loadConfig();

		// Annotate
		return annotate(createList);
	}

	public void setAnnotateEmpty(boolean annotateEmpty) {
		this.annotateEmpty = annotateEmpty;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [options] database.vcf file.vcf > newFile.vcf");
		System.err.println("\nDatabase options:");
		System.err.println("\tdatabase.vcf         : Use 'database.vcf' file as annotations database. Note: The VCF file can be bgzipped and tabix-indexed.");
		System.err.println("\t-dbsnp               : Use DbSnp database.");
		System.err.println("\t-clinvar             : Use ClinVar database.");
		System.err.println("\nCommand Options:");
		System.err.println("\t-a                   : Annotate fields, even if the database has an empty value (annotates using '.' for empty).");
		System.err.println("\t-exists <tag>        : Annotate whether the variant exists or not in the database (using 'tag' as an INFO field FLAG).");
		System.err.println("\t-id                  : Only annotate ID field (do not add INFO field). Default: " + useId);
		System.err.println("\t-info <list>         : Annotate using a list of info fields (list is a comma separated list of fields). Default: ALL.");
		System.err.println("\t-name str            : Prepend 'str' to all annotated INFO fields. Default: ''.");
		System.err.println("\t-maxBlockSize <int>  : Use 'max block size' when creating index ('-sorted' command line option). Default: " + maxBlockSize);
		System.err.println("\t-noAlt               : Do not use REF and ALT fields when comparing database.vcf entries to file.vcf entries. Default: " + !useRefAlt);
		System.err.println("\t-noId                : Do not annotate ID field. Default: " + !useId);
		System.err.println("\t-noInfo              : Do not annotate INFO fields. Default: " + !useInfoField);
		System.err.println("\t-sorted              : VCF database is sorted and uncompressed. Default: " + (method == AnnotationMethod.SORTED_VCF));
		System.err.println("\t-tabix               : VCF database is tabix-indexed. Default: " + (method == AnnotationMethod.TABIX));

		usageGenericAndDb();

		System.err.println("Note: According the the VCF's database format provided, SnpSift annotate uses different strategies");
		System.err.println("\t  i) plain VCF       : SnpSift indexes the VCF file (creating an index file *.sidx).");
		System.err.println("\t ii) bgzip+tabix     : SnpSift uses tabix's index.");

		System.exit(1);
	}

}
