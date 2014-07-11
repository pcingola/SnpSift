package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.annotate.AnnotateVcfDb;
import ca.mcgill.mcb.pcingola.snpSift.annotate.AnnotateVcfDbMem;
import ca.mcgill.mcb.pcingola.snpSift.annotate.AnnotateVcfDbSorted;
import ca.mcgill.mcb.pcingola.snpSift.annotate.AnnotateVcfDbTabix;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeader;
import ca.mcgill.mcb.pcingola.vcf.VcfInfo;

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

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	protected boolean useId; // Annotate ID fields
	protected boolean useInfoField; // Use all info fields
	protected boolean useRefAlt;
	protected AnnotationMethod method;
	protected int countBadRef = 0;
	protected String vcfFileName;
	protected String chrPrev = "";
	protected String prependInfoFieldName;
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
	 * Build headers to add
	 */
	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();

		// Read database header and add INFO fields to the output vcf header
		if (useInfoField) {
			VcfFileIterator vcfDb = new VcfFileIterator(dbFileName);
			VcfHeader vcfDbHeader = vcfDb.readHeader();

			// Add all corresponding INFO headers
			for (VcfInfo vcfInfoDb : vcfDbHeader.getVcfInfo()) {

				// Get same vcfInfo from file to annotate
				VcfInfo vcfInfoFile = vcfFile.getVcfHeader().getVcfInfo(vcfInfoDb.getId());

				// Add header entry only if...
				if (isAnnotateInfo(vcfInfoDb) // Add if it is being used to annotate
						&& !vcfInfoDb.isImplicit() //  AND it is not an "implicit" header in Db (i.e. created automatically by VcfHeader class)
						&& ((vcfInfoFile == null) || vcfInfoFile.isImplicit()) // AND it is not already added OR is already added, but it is implicit
				) newHeaders.add(vcfInfoDb.toString());
			}
		}

		return newHeaders;
	}

	/**
	 * Annotate VCF file
	 *
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	ArrayList<VcfEntry> annotate(boolean createList) {
		ArrayList<VcfEntry> list = (createList ? new ArrayList<VcfEntry>() : null);
		if (verbose) Timer.showStdErr("Annotating entries from: '" + vcfFileName + "'");

		try {
			initAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int countAnnotated = 0, count = 0;
		boolean showHeader = true;
		int pos = -1;
		String chr = "";

		for (VcfEntry vcfEntry : vcfFile) {
			try {
				// Show header?
				if (showHeader) {
					showHeader = false;
					addHeader(vcfFile);
					String headerStr = vcfFile.getVcfHeader().toString();
					if (!headerStr.isEmpty()) print(headerStr);
				}

				// Check if file is sorted
				if (vcfEntry.getChromosomeName().equals(chr) && vcfEntry.getStart() < pos) {
					fatalError("Your VCF file should be sorted!" //
							+ "\n\tPrevious entry " + chr + ":" + pos//
							+ "\n\tCurrent entry  " + vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1)//
					);
				}

				// Annotate
				boolean annotated = annotateDb.annotate(vcfEntry);

				// Show
				print(vcfEntry);
				if (list != null) list.add(vcfEntry);

				if (annotated) countAnnotated++;
				count++;

				// Update chr:pos
				chr = vcfEntry.getChromosomeName();
				pos = vcfEntry.getStart();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		annotateDb.close();

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

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		useInfoField = true; // Default: Use INFO fields
		useId = true; // Annotate ID fields
		useRefAlt = true; // Use REF and ALT fields when comparing
		method = AnnotationMethod.SORTED_VCF;

		needsConfig = true;
		needsDb = true;
		dbTabix = true;
	}

	/**
	 * Initialize annotation process
	 * @throws IOException
	 */
	public void initAnnotate() throws IOException {
		vcfFile = new VcfFileIterator(vcfFileName); // Open input VCF
		vcfFile.setDebug(debug);

		// Type of database
		switch (method) {

		case MEMORY:
			annotateDb = new AnnotateVcfDbMem(dbFileName);
			break;

		case SORTED_VCF:
			annotateDb = new AnnotateVcfDbSorted(dbFileName);
			break;

		case TABIX:
			annotateDb = new AnnotateVcfDbTabix(dbFileName);
			break;

		default:
			throw new RuntimeException("Unknwon method '" + method + "'");
		}

		// Set parameters
		annotateDb.setUseId(useId);
		annotateDb.setUseInfoField(useInfoField);
		annotateDb.setUseRefAlt(useRefAlt);
		annotateDb.setInfoFields(infoFields);
		annotateDb.setPrependInfoFieldName(prependInfoFieldName);
		annotateDb.setDebug(debug);
		annotateDb.setVerbose(verbose);

		annotateDb.open();// Open database
	}

	/**
	 * Are we annotating using this info field?
	 * @param vcfInfo
	 * @return
	 */
	boolean isAnnotateInfo(VcfInfo vcfInfo) {
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
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Command line option?
			if (isOpt(arg)) {
				if (arg.equalsIgnoreCase("-id")) useInfoField = false;
				else if (arg.equalsIgnoreCase("-info")) {
					useInfoField = true;
					infoFields = new ArrayList<String>();
					for (String infoField : args[++i].split(","))
						infoFields.add(infoField);
				} else if (arg.equalsIgnoreCase("-noId")) useId = false;
				else if (arg.equalsIgnoreCase("-name")) prependInfoFieldName = args[++i];
				else if (arg.equalsIgnoreCase("-noAlt")) useRefAlt = false;
				else if (arg.equalsIgnoreCase("-dbSnp")) {
					dbType = "dbsnp";
					method = AnnotationMethod.SORTED_VCF;
				} else if (arg.equalsIgnoreCase("-clinVar")) {
					dbType = "clinvar";
					method = AnnotationMethod.TABIX;
				} else if (arg.equalsIgnoreCase("-mem")) method = AnnotationMethod.MEMORY;
				else if (arg.equalsIgnoreCase("-sorted")) method = AnnotationMethod.SORTED_VCF;
				else if (arg.equalsIgnoreCase("-tabix")) method = AnnotationMethod.TABIX;
				else usage("Unknown command line option '" + arg + "'");
			} else {
				if (dbFileName == null) dbFileName = arg;
				else if (vcfFileName == null) vcfFileName = arg;
				else usage("Unknown extra parameter '" + arg + "'");
			}
		}

		// Sanity check
		if (vcfFileName == null) usage("Missing 'file.vcf'");
		if (dbType == null && dbFileName == null) usage("Missing database option or file: [-dbSnp | -clinVar | database.vcf ]");
	}

	/**
	 * Annotate each entry of a VCF file
	 * @throws IOException
	 */
	@Override
	public void run() {
		run(false);
	}

	/**
	 * Run annotations
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	public List<VcfEntry> run(boolean createList) {
		// Read config
		if (config == null) loadConfig();

		// Find or download database
		dbFileName = databaseFindOrDownload();

		// For tabix databases, if the 'gz' file exists, try opening that one instead
		if (method == AnnotationMethod.TABIX //
				&& !dbFileName.endsWith(".gz") //
				&& Gpr.exists(dbFileName + ".gz") //
		) dbFileName = dbFileName + ".gz";

		if (verbose) Timer.showStdErr("Annotating\n" //
				+ "\tInput file    : '" + vcfFileName + "'\n" //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		return annotate(createList);
	}

	public void setSuppressOutput(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [options] [-dbSnp | -clinvar | database.vcf] file.vcf > newFile.vcf");
		System.err.println("\nDatabase options:");
		System.err.println("\tdatabase.vcf         : Use 'database.vcf' file as annotations database. Note: The VCF file can be bgzipped and tabix-indexed.");
		System.err.println("\t-dbsnp               : Use DbSnp database.");
		System.err.println("\t-clinvar             : Use ClinVar database.");
		System.err.println("\nCommand Options:");
		System.err.println("\t-id                  : Only annotate ID field (do not add INFO field). Default: " + !useInfoField);
		System.err.println("\t-mem                 : VCF database is loaded in memory. Default: " + (method == AnnotationMethod.MEMORY));
		System.err.println("\t-sorted              : VCF database is sorted and uncompressed. Default: " + (method == AnnotationMethod.SORTED_VCF));
		System.err.println("\t-tabix               : VCF database is tabix-indexed. Default: " + (method == AnnotationMethod.TABIX));
		System.err.println("\t-noAlt               : Do not use REF and ALT fields when comparing database.vcf entries to file.vcf entries. Default: " + !useRefAlt);
		System.err.println("\t-noId                : Do not annotate ID field. Defaul: " + !useId);
		System.err.println("\t-info <list>         : Annotate using a list of info fields (list is a comma separated list of fields). Default: ALL.");
		System.err.println("\t-name str            : Prepend 'str' to all annotated INFO fields. Default: ''.");

		usageGenericAndDb();

		System.exit(1);
	}
}
