package org.snpsift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.snpeff.Pcingola;
import org.snpeff.SnpEff;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.snpEffect.Config;
import org.snpeff.snpEffect.VcfAnnotator;
import org.snpeff.util.Download;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpsift.caseControl.SnpSiftCmdCaseControl;
import org.snpsift.caseControl.SnpSiftCmdCaseControlSummary;
import org.snpsift.hwe.SnpSiftCmdHwe;

/**
 * Generic SnpSift tool caller
 *
 * @author pablocingolani
 */
public class SnpSift implements VcfAnnotator {

	// Version info (in sync with SnpEff)
	public static final String BUILD = SnpEff.BUILD;

	public static final String SOFTWARE_NAME = "SnpSift";
	public static final String VERSION_MAJOR = SnpEff.VERSION_MAJOR;
	public static final String REVISION = SnpEff.REVISION;
	public static final String VERSION_SHORT = VERSION_MAJOR + REVISION;
	public static final String VERSION = VERSION_SHORT + " (build " + BUILD + "), by " + Pcingola.BY;
	public static final String VERSION_NO_NAME = SOFTWARE_NAME + " " + VERSION;
	public static final int MAX_ERRORS = 10; // Report an error no more than X times
	public static int SHOW_EVERY_VCFLINES = 100; // Show a mark every N vcf lines processed
	public static final String[] EMPTY_ARGS = new String[0];

	protected boolean dbTabix; // Is this database supposed to be in tabix indexed form?
	protected boolean debug; // Debug mode
	protected boolean download = true; // Download database, if not available
	protected boolean help; // Be verbose
	protected boolean log; // Log to server (statistics)
	protected boolean needsConfig; // Does this command need a config file?
	protected boolean needsDb; // Does this command need a database file?
	protected boolean needsGenome; // Does this command need a genome version?
	protected boolean quiet; // Be quiet
	protected boolean saveOutput = false; // Save output to buffer (instead of printing it to STDOUT)
	protected boolean showVcfHeader = true; // Should VCF header be shown
	protected boolean showVersion = true; // Show version number and exit
	protected boolean suppressOutput = false; // Do not show output (used for debugging and test cases)
	protected boolean vcfHeaderProcessed = false; // Has the VCF header been processed?
	protected boolean vcfHeaderAddProgramVersion = true; // Add program verison and command line to VCF header
	protected boolean verbose; // Be verbose
	protected String args[];
	protected String command;
	protected String vcfInputFile; // VCF Input file
	protected String dbFileName;
	protected String dbType;
	protected String genomeVersion;
	protected int numWorkers = 1; //  Max number of threads (if multi-threaded version is available)
	protected StringBuilder output = new StringBuilder();
	protected HashMap<String, Integer> errCount;
	protected String configFile; // Config file
	protected Config config; // Configuration
	protected String dataDir; // Override data_dir in config file

	/**
	 * Main
	 */
	public static void main(String[] args) {
		SnpSift snpSift = new SnpSift(args, null);
		snpSift.run();
	}

	public SnpSift(String[] args, String command) {
		this.args = args;
		this.command = command;
		errCount = new HashMap<>();
		init();
		if (args != null) parseArgs(args);
	}

	/**
	 * Add VCF headers
	 */
	@Override
	public boolean addHeaders(VcfFileIterator vcfFile) {
		for (VcfHeaderEntry hinf : headers())
			vcfFile.getVcfHeader().add(hinf);

		return false;
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		throw new RuntimeException("Unimplemented method!");
	}

	@Override
	public boolean annotateFinish(VcfFileIterator vcfFile) {
		if (vcfFile != null) {
			// Empty VCF file (with only header) might have not processed the header at this stage
			if (!vcfHeaderProcessed) processVcfHeader(vcfFile);
			vcfFile.close();
		}
		return true; // By default nothing is done
	}

	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		return true; // By default nothing is done
	}

	/**
	 * Show command line
	 */
	protected String commandLineStr() {
		if (args == null) return "";

		StringBuilder argsList = new StringBuilder();
		argsList.append("SnpSift " + command + " ");

		if (args != null) {
			for (String arg : args) {
				arg = arg.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
				if (arg.indexOf(' ') > 0) arg = "'" + arg + "'";
				argsList.append(arg + " ");
			}
		}

		return argsList.toString().trim();
	}

	/**
	 * Download a database
	 */
	protected boolean databaseDownload() {
		if (!download) return false;

		String dbUrl = config.getDatabaseRepository(dbType);
		if (dbUrl == null) fatalError("Database URL name is missing (missing entry in config file?).");

		Timer.showStdErr("Downlading database from " + dbUrl);
		Download download = new Download();
		download.setVerbose(verbose);
		download.setDebug(debug);
		boolean ok = download.download(dbUrl, dbFileName);

		if (!ok) return false;

		// Download tabix index?
		if (dbTabix) {
			String indexUrl = config.getDatabaseRepository(dbType) + ".tbi";
			Timer.showStdErr("Downlading index from " + indexUrl);
			download = new Download();
			download.setVerbose(verbose);
			download.setDebug(debug);
			ok &= download.download(indexUrl, dbFileName + ".tbi");
		}

		return ok;
	}

	/**
	 * Find database file name.
	 */
	protected String databaseFind() {
		if (dbType == null && dbFileName == null) throw new RuntimeException("Neither database type nor database file name set: This should never happen!");

		// Database file name
		if (dbFileName == null || dbFileName.isEmpty()) {
			dbFileName = config.getDatabaseLocal(dbType);

			// Still empty: Something is wrong!
			if (dbFileName == null || dbFileName.isEmpty()) {
				String coordinates = config.getCoordinates();
				fatalError("Database file name is empty. Missing '" + Config.KEY_DATABASE_LOCAL + "." + dbType + "." + coordinates + "' entry in SnpEff's config file?");
			}
		}

		return dbFileName;
	}

	/**
	 * Show an error (if not 'quiet' mode)
	 */
	public void error(Throwable e, String message) {
		if (verbose && (e != null)) e.printStackTrace();
		if (!quiet) System.err.println(message);
	}

	/**
	 * Show an error message and exit
	 */
	public void fatalError(String message) {
		System.err.println("Fatal error: " + message);
		System.exit(-1);
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	public Config getConfig() {
		return config;
	}

	public String getConfigFile() {
		return configFile;
	}

	public String getOutput() {
		return output.toString();
	}

	/**
	 * Headers to add
	 */
	protected List<VcfHeaderEntry> headers() {
		ArrayList<VcfHeaderEntry> newHeaders = new ArrayList<>();
		if (vcfHeaderAddProgramVersion) {
			newHeaders.add(new VcfHeaderEntry("##SnpSiftVersion=\"" + VERSION_NO_NAME + "\""));
			newHeaders.add(new VcfHeaderEntry("##SnpSiftCmd=\"" + commandLineStr() + "\""));
		}
		return newHeaders;
	}

	/**
	 * Initialize default values
	 */
	public void init() {
		genomeVersion = "";
	}

	/**
	 * Is this a command line option (e.g. "-tfam" is a command line option, but "-" means STDIN)
	 */
	protected boolean isOpt(String arg) {
		return arg.startsWith("-") && (arg.length() > 1);
	}

	/**
	 * Read config file
	 */
	protected void loadConfig() {
		if (config != null) return; // Already loaded?

		// Read config file
		if (configFile == null || configFile.isEmpty()) configFile = Config.DEFAULT_CONFIG_FILE; // Default config file

		if (verbose) Timer.showStdErr("Reading configuration file '" + configFile + "'");

		config = new Config(genomeVersion, configFile, dataDir, null); // Read configuration
		if (verbose) Timer.showStdErr("done");

		// Set some parameters
		config.setDebug(debug);
		config.setVerbose(verbose);
	}

	/**
	 * Open VCF input file
	 */
	protected VcfFileIterator openVcfInputFile() {
		if (vcfInputFile == null || vcfInputFile.isEmpty() || vcfInputFile.equals("-")) vcfInputFile = "-";
		if (verbose) Timer.showStdErr("Opening VCF input '" + (vcfInputFile.equals("-") ? "STDIN" : vcfInputFile) + "'");
		VcfFileIterator vcf = new VcfFileIterator(vcfInputFile);
		vcf.setDebug(debug);
		return vcf;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length < 1) usage(null);

		// Get command
		command = args[0];

		// Create new array shifting everything 1 position
		ArrayList<String> argsList = new ArrayList<>();
		for (int i = 1; i < args.length; i++) {
			String arg = args[i];

			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-c":
				case "-config":
					if ((i + 1) < args.length) configFile = args[++i];
					else usage("Option '-c' without config file argument");
					break;

				case "-cpus":
					if (args.length <= i) usage("Missing argument for command line option '-cpus'");
					numWorkers = Gpr.parseIntSafe(args[++i]);
					if (numWorkers <= 0) usage("Error: Number of cpus must be positive");
					break;

				case "-d":
				case "-debug":
					debug = verbose = true;
					break;

				case "-datadir":
					if ((i + 1) < args.length) dataDir = args[++i];
					else usage("Option '-dataDir' without data_dir argument");
					break;

				case "-db":
				case "-database":
					if (args.length <= i) usage("Missing argument for command line option '-db'");
					dbFileName = args[++i];
					break;

				case "-h":
				case "-help":
					help = true;
					break;

				case "-g":
				case "-genome":
					if ((i + 1) < args.length) genomeVersion = args[++i];
					else usage("Option '-g' without argument");
					break;

				case "-nodownload":
					download = false;
					break;// Do not download genome

				case "-nolog":
					log = false;
					break;

				case "-noout":
					suppressOutput = true;
					break;

				case "-q":
				case "-quiet":
					quiet = true;
					break;

				case "-v":
				case "-verbose":
					verbose = true;
					break;

				case "-version":
					// Show version number and exit
					System.out.println(VERSION_SHORT);
					System.exit(0);
					break;

				default:
					argsList.add(args[i]);
				}
			} else argsList.add(args[i]);
		}

		this.args = argsList.toArray(new String[0]);
	}

	/**
	 * Print to screen or save to output buffer
	 */
	void print(Object o) {
		if (saveOutput) output.append(o.toString() + "\n");
		else if (!suppressOutput) System.out.println(o.toString());
	}

	/**
	 * Process VCF header related issues
	 */
	protected String processVcfHeader(VcfFileIterator vcf) {
		if (vcfHeaderProcessed // Already processed? Skip
				|| (!vcf.isHeadeSection() && vcf.getLineNum() > 1) // First line is always a header
		) return "";

		// Add lines to header
		addHeaders(vcf);
		vcfHeaderProcessed = true;

		if (showVcfHeader) {
			String headerStr = vcf.getVcfHeader().toString();
			if (!headerStr.isEmpty()) print(headerStr);
			return headerStr;
		}

		return "";
	}

	@Override
	public boolean run() {
		SnpSift cmd = snpSiftCmd();

		// Execute command
		cmd.run();

		return true;
	}

	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public void setDbFileName(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public void setSaveOutput(boolean saveOutput) {
		this.saveOutput = saveOutput;
	}

	public void setShowVcfHeader(boolean showVcfHeader) {
		this.showVcfHeader = showVcfHeader;
	}

	public void setShowVersion(boolean showVersion) {
		this.showVersion = showVersion;
	}

	public void setSuppressOutput(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	public void setVcfHeaderAddProgramVersion(boolean vcfHeaderAddProgramVersion) {
		this.vcfHeaderAddProgramVersion = vcfHeaderAddProgramVersion;
	}

	@Override
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Show command line
	 */
	public void showCmd() {
		System.err.print(SnpSift.class.getSimpleName() + " " + command + " ");
		if (args != null) {
			for (String a : args)
				System.err.print(a + " ");
		}
		System.err.println("");
	}

	/**
	 * Show version number
	 */
	public void showVersion() {
		System.err.println(SnpSift.class.getSimpleName() + " version " + VERSION + "\n");
	}

	/**
	 * Run: Executes the appropriate class
	 */
	public SnpSift snpSiftCmd() {
		SnpSift cmd = null;

		command = command.trim().toUpperCase();

		if (command.startsWith("ALL")) cmd = new SnpSiftCmdAlleleMatrix(args);
		else if (command.startsWith("ANN")) cmd = new SnpSiftCmdAnnotate(args);
		else if (command.startsWith("CA")) cmd = new SnpSiftCmdCaseControl(args);
		else if (command.startsWith("CCS")) cmd = new SnpSiftCmdCaseControlSummary(args);
		else if (command.startsWith("CONC")) cmd = new SnpSiftCmdConcordance(args);
		else if (command.startsWith("COVMAT")) cmd = new SnpSiftCmdCovarianceMatrix(args);
		else if (command.startsWith("DBNSFP")) cmd = new SnpSiftCmdDbNsfp(args);
		else if (command.startsWith("EX")) cmd = new SnpSiftCmdExtractFields(args);
		else if (command.startsWith("FILTERC")) cmd = new SnpSiftCmdFilterChrPos(args);
		else if (command.startsWith("FI")) cmd = new SnpSiftCmdFilter(args);
		else if (command.startsWith("GENESETS")) cmd = new SnpSiftCmdGeneSets(args);
		else if (command.startsWith("GTF")) cmd = new SnpSiftCmdFilterGt(args);
		else if (command.startsWith("GT")) cmd = new SnpSiftCmdGt(args);
		else if (command.startsWith("GWASCAT")) cmd = new SnpSiftCmdGwasCatalog(args);
		else if (command.startsWith("HW")) cmd = new SnpSiftCmdHwe(args);
		else if (command.startsWith("INTIDX")) cmd = new SnpSiftCmdIntervalsIndex(args);
		else if (command.startsWith("INTERS")) cmd = new SnpSiftCmdIntersect(args);
		else if (command.startsWith("INTERV")) cmd = new SnpSiftCmdIntervals(args);
		else if (command.startsWith("JOIN")) cmd = new SnpSiftCmdJoin(args);
		else if (command.startsWith("OP")) cmd = new SnpSiftCmdVcfOperator(args);
		else if (command.startsWith("PEDSHOW")) cmd = new SnpSiftCmdPedShow(args);
		else if (command.startsWith("PHASTCONS")) cmd = new SnpSiftCmdPhastCons(args);
		else if (command.startsWith("PRIVATE")) cmd = new SnpSiftCmdPrivate(args);
		else if (command.startsWith("RMINFO")) cmd = new SnpSiftCmdRmInfo(args);
		else if (command.startsWith("RMREF")) cmd = new SnpSiftCmdRemoveReferenceGenotypes(args);
		else if (command.startsWith("SORT")) cmd = new SnpSiftCmdSort(args);
		else if (command.startsWith("SPLIT")) cmd = new SnpSiftCmdSplit(args);
		else if (command.startsWith("TS")) cmd = new SnpSiftCmdTsTv(args);
		else if (command.startsWith("VARTYPE")) cmd = new SnpSiftCmdVarType(args);
		else if (command.startsWith("VCF2TPED")) cmd = new SnpSiftCmdVcf2Tped(args);
		else if (command.startsWith("VCFCHECK")) cmd = new SnpSiftCmdVcfCheck(args);
		else usage("Unknown command '" + command + "'");

		// Help? Show help and exit
		if (help) cmd.usage(null);

		// Show version and command
		if (!help && (verbose || debug) && showVersion) {
			Timer.showStdErr("SnpSift version " + VERSION);
			Timer.showStdErr("Command: '" + command + "'");
		}

		// Copy parsed parameters
		cmd.config = config;
		cmd.configFile = configFile;
		cmd.debug = debug;
		cmd.download = download;
		cmd.genomeVersion = genomeVersion;
		cmd.help = help;
		cmd.log = log;
		cmd.needsConfig = needsConfig;
		cmd.needsDb = needsDb;
		cmd.needsGenome = needsGenome;
		cmd.numWorkers = numWorkers;
		cmd.quiet = quiet;
		cmd.showVcfHeader = cmd.showVcfHeader;
		cmd.suppressOutput = suppressOutput;
		cmd.vcfHeaderAddProgramVersion = vcfHeaderAddProgramVersion;
		cmd.verbose = verbose;

		if (cmd.dbFileName == null) cmd.dbFileName = dbFileName;
		if (cmd.dbType == null) cmd.dbType = dbType;

		// Execute command
		return cmd;
	}

	/**
	 * Convert a sanitized expression (from Galaxy) back to the original string
	 *
	 * References: http://www.mail-archive.com/galaxy-dev@lists.bx.psu.edu/msg00530.html
	 */
	public String unSanitize(String str) {
		str = str.replaceAll("__lt__", "<");
		str = str.replaceAll("__gt__", ">");
		str = str.replaceAll("__sq__", "'");
		str = str.replaceAll("__dq__", "\"");
		str = str.replaceAll("__ob__", "[");
		str = str.replaceAll("__cb__", "]");
		str = str.replaceAll("__oc__", "{");
		str = str.replaceAll("__cc__", "}");
		str = str.replaceAll("__oc__", "{");
		str = str.replaceAll("__at__", "@");
		str = str.replaceAll("__cn__", "\n");
		str = str.replaceAll("__cr__", "\r");
		str = str.replaceAll("__tc__", "\t");
		return str;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar [command] params..." //
				+ "\nCommand is one of:" //
				+ "\n\talleleMat     : Create an allele matrix output." //
				+ "\n\tannotate      : Annotate 'ID' from a database (e.g. dbSnp). Assumes entries are sorted." //
				+ "\n\tannMem        : Annotate 'ID' from a database (e.g. dbSnp). Loads db in memory. Does not assume sorted entries." //
				+ "\n\tcaseControl   : Compare how many variants are in 'case' and in 'control' groups; calculate p-values." //
				+ "\n\tccs           : Case control summary. Case and control summaries by region, allele frequency and variant's functional effect." //
				+ "\n\tconcordance   : Concordance metrics between two VCF files." //
				+ "\n\tcovMat        : Create an covariance matrix output (allele matrix as input)." //
				+ "\n\tdbnsfp        : Annotate with multiple entries from dbNSFP." //
				+ "\n\textractFields : Extract fields from VCF file into tab separated format." //
				+ "\n\tfilter        : Filter using arbitrary expressions" //
				+ "\n\tgeneSets      : Annotate using MSigDb gene sets (MSigDb includes: GO, KEGG, Reactome, BioCarta, etc.)" //
				+ "\n\tgt            : Add Genotype to INFO fields and remove genotype fields when possible." //
				+ "\n\tgtfilter      : Filter genotype using arbitrary expressions." //
				+ "\n\tgwasCat       : Annotate using GWAS catalog" //
				+ "\n\thwe           : Calculate Hardy-Weimberg parameters and perform a godness of fit test." //
				+ "\n\tintersect     : Intersect intervals (genomic regions)." //
				+ "\n\tintervals     : Keep variants that intersect with intervals." //
				+ "\n\tintIdx        : Keep variants that intersect with intervals. Index-based method: Used for large VCF file and a few intervals to retrieve" //
				+ "\n\tjoin          : Join files by genomic region." //
				+ "\n\top            : Annotate using an operator." //
				+ "\n\tphastCons     : Annotate using conservation scores (phastCons)." //
				+ "\n\tprivate       : Annotate if a variant is private to a family or group." //
				+ "\n\trmRefGen      : Remove reference genotypes." //
				+ "\n\trmInfo        : Remove INFO fields." //
				+ "\n\tsort          : Sort VCF file/s (if multiple input VCFs, merge and sort)." //
				+ "\n\tsplit         : Split VCF by chromosome." //
				+ "\n\ttstv          : Calculate transiton to transversion ratio." //
				+ "\n\tvarType       : Annotate variant type (SNP,MNP,INS,DEL or MIXED)." //
				+ "\n\tvcfCheck      : Check that VCF file is well formed." //
				+ "\n\tvcf2tped      : Convert VCF to TPED." //
		);

		usageGenericAndDb();

		System.exit(1);
	}

	/**
	 * Options common to all commands
	 */
	protected void usageGenericAndDb() {
		System.err.println("\nOptions common to all SnpSift commands:\n" //
				+ (needsConfig ? "\t-c , -config <file>  : Specify config file\n" : "") //
				+ "\t-d                   : Debug.\n" //
				+ (needsDb ? "\t-db <file>           : Database file name (for commands that require databases).\n" : "") //
				+ "\t-download            : Download database, if not available locally. Default: " + download + ".\n" //
				+ (needsGenome ? "\t-g <name>            : Genome version (for commands that require databases).\n" : "") //
				+ "\t-noDownload          : Do not download a database, if not available locally.\n" //
				+ "\t-noLog               : Do not report usage statistics to server.\n" //
				+ "\t-h                   : Help.\n" //
				+ "\t-v                   : Verbose.\n" //
		);
	}

	/**
	 * Show a warning message (up to MAX_ERRORS times)
	 */
	protected void warn(String warn) {
		if (!errCount.containsKey(warn)) errCount.put(warn, 0);

		int count = errCount.get(warn);
		errCount.put(warn, count + 1);

		if (count < MAX_ERRORS) System.err.println("WARNING: " + warn);
	}

}
