package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.mcgill.mcb.pcingola.Pcingola;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpEffect.commandLine.SnpEff;
import ca.mcgill.mcb.pcingola.snpSift.caseControl.SnpSiftCmdCaseControl;
import ca.mcgill.mcb.pcingola.snpSift.caseControl.SnpSiftCmdCaseControlSummary;
import ca.mcgill.mcb.pcingola.snpSift.epistasis.SnpSiftCmdEpistasis;
import ca.mcgill.mcb.pcingola.snpSift.hwe.SnpSiftCmdHwe;
import ca.mcgill.mcb.pcingola.snpSift.matrix.SnpSiftCmdAlleleMatrix;
import ca.mcgill.mcb.pcingola.snpSift.matrix.SnpSiftCmdCovarianceMatrix;
import ca.mcgill.mcb.pcingola.snpSift.matrix.SnpSiftCmdGt;
import ca.mcgill.mcb.pcingola.util.Gpr;
import flanagan.analysis.Stat;

/**
 * Generic SnpSift tool caller
 *
 * @author pablocingolani
 */
public class SnpSift {

	// Version info (in sync with SnpEff)
	public static final String BUILD = SnpEff.BUILD;
	public static final String SOFTWARE_NAME = "SnpSift";
	public static final String VERSION_MAJOR = SnpEff.VERSION_MAJOR;
	public static final String REVISION = SnpEff.REVISION;
	public static final String VERSION_SHORT = VERSION_MAJOR + REVISION;
	public static final String VERSION_NO_NAME = VERSION_SHORT + " (build " + BUILD + "), by " + Pcingola.BY;
	public static final String VERSION = SOFTWARE_NAME + " " + VERSION_NO_NAME;

	public static final int MAX_ERRORS = 10; // Report an error no more than X times

	protected boolean help; // Be verbose
	protected boolean verbose; // Be verbose
	protected boolean debug; // Debug mode
	protected boolean quiet; // Be quiet
	protected boolean log; // Log to server (statistics)
	protected boolean showHeader = true;
	protected boolean saveOutput = false; // Save output to buffer (instead of printing it to STDOUT)
	protected boolean suppressOutput = false; // Do not show output (used for debugging and test cases)
	protected String args[];
	protected String command;
	protected int numWorkers = Gpr.NUM_CORES; // Max number of threads (if multi-threaded version is available)
	protected StringBuilder output = new StringBuilder();
	protected HashMap<String, Integer> errCount;

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		Stat.igSupress(); // Otherwise we can get error messages printed to STDOUT

		SnpSift snpSift = new SnpSift(args, null);
		snpSift.run();
	}

	public SnpSift(String[] args, String command) {
		this.args = args;
		this.command = command;
		errCount = new HashMap<String, Integer>();
		init();
		if (args != null) parse(args);
	}

	/**
	 * Headers to add
	 * @return
	 */
	protected List<String> addHeader() {
		ArrayList<String> newHeaders = new ArrayList<String>();
		newHeaders.add("##SnpSiftVersion=\"" + VERSION + "\"");
		newHeaders.add("##SnpSiftCmd=\"" + commandLineStr() + "\"");
		return newHeaders;
	}

	/**
	 * Add some lines to header before showing it
	 * @param vcfFile
	 */
	protected void addHeader(VcfFileIterator vcfFile) {
		for (String h : addHeader())
			vcfFile.getVcfHeader().addLine(h);
	}

	/**
	 * Show command line
	 * @return
	 */
	protected String commandLineStr() {
		StringBuilder argsList = new StringBuilder();
		argsList.append("SnpSift " + command + " ");

		if (args != null) {
			for (String arg : args)
				argsList.append(arg + " ");
		}

		return argsList.toString();
	}

	/**
	 * Show an error message and exit
	 * @param message
	 */
	public void fatalError(String message) {
		System.err.println("Fatal error: " + message);
		System.exit(-1);
	}

	public String getOutput() {
		return output.toString();
	}

	/**
	 * Handle VCF header related issues
	 * @param vcf
	 */
	protected void handleVcfHeader(VcfFileIterator vcf) {
		if (!vcf.isHeadeSection()) return;

		addHeader(vcf); // Add lines to header
		if (showHeader) System.out.println(vcf.getVcfHeader()); // Show header
	}

	/**
	 * Initialize default values
	 */
	public void init() {
	}

	/**
	 * Is this a command line option (e.g. "-tfam" is a command line option, but "-" means STDIN)
	 * @param arg
	 * @return
	 */
	protected boolean isOpt(String arg) {
		return arg.startsWith("-") && (arg.length() > 1);
	}

	/**
	 * Parse command line arguments
	 */
	public void parse(String[] args) {
		if (args.length < 1) usage(null);

		// Get command
		command = args[0].toUpperCase();

		// Create new array shifting everything 1 position
		ArrayList<String> argsList = new ArrayList<String>();
		for (int i = 1; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-noLog")) log = false;
			else if (args[i].equals("-h") || args[i].equalsIgnoreCase("-help")) help = true;
			else if (args[i].equals("-v") || args[i].equalsIgnoreCase("-verbose")) verbose = true;
			else if (args[i].equals("-q") || args[i].equalsIgnoreCase("-quiet")) quiet = true;
			else if (args[i].equals("-d") || args[i].equalsIgnoreCase("-debug")) debug = true;
			else if (args[i].equals("-cpus")) {
				numWorkers = Gpr.parseIntSafe(args[++i]);
				if (numWorkers <= 0) usage("Error: Number of cpus must be positive");
			} else argsList.add(args[i]);
		}

		this.args = argsList.toArray(new String[0]);;
	}

	/**
	 * Print to screen or save to output buffer
	 * @param o
	 */
	void print(Object o) {
		if (saveOutput) output.append(o.toString() + "\n");
		else if (!suppressOutput) System.out.println(o.toString());
	}

	/**
	 * Run: Executes the appropriate class
	 */
	public void run() {
		SnpSift cmd = null;

		if (command.startsWith("ALLELEMAT")) cmd = new SnpSiftCmdAlleleMatrix(args);
		else if (command.startsWith("ANNM")) cmd = new SnpSiftCmdAnnotateMem(args);
		else if (command.startsWith("ANN")) cmd = new SnpSiftCmdAnnotateSorted(args);
		else if (command.startsWith("CA")) cmd = new SnpSiftCmdCaseControl(args);
		else if (command.startsWith("CCS")) cmd = new SnpSiftCmdCaseControlSummary(args);
		else if (command.startsWith("CONC")) cmd = new SnpSiftCmdConcordance(args);
		else if (command.startsWith("COVMAT")) cmd = new SnpSiftCmdCovarianceMatrix(args);
		else if (command.startsWith("DBNSFP")) cmd = new SnpSiftCmdDbNsfp(args);
		else if (command.startsWith("EX")) cmd = new SnpSiftCmdExtractFields(args);
		else if (command.startsWith("FI")) cmd = new SnpSiftCmdFilter(args);
		else if (command.startsWith("GWASCAT")) cmd = new SnpSiftCmdGwasCatalog(args);
		else if (command.startsWith("HW")) cmd = new SnpSiftCmdHwe(args);
		else if (command.startsWith("INTIDX")) cmd = new SnpSiftCmdIntervalsIndex(args);
		else if (command.startsWith("INTERS")) cmd = new SnpSiftCmdIntersect(args);
		else if (command.startsWith("INTERV")) cmd = new SnpSiftCmdIntervals(args);
		else if (command.startsWith("JOIN")) cmd = new SnpSiftCmdJoin(args);
		else if (command.startsWith("SIF")) cmd = new SnpSiftCmdAnnotateSortedSift(args);
		else if (command.startsWith("SPLIT")) cmd = new SnpSiftCmdSplit(args);
		else if (command.startsWith("TS")) cmd = new SnpSiftCmdTsTv(args);
		else if (command.startsWith("VARTYPE")) cmd = new SnpSiftCmdVarType(args);
		else if (command.startsWith("PRIVATE")) cmd = new SnpSiftCmdPrivate(args);
		else if (command.startsWith("PHASTCONS")) cmd = new SnpSiftCmdPhastCons(args);
		else if (command.startsWith("RMINFO")) cmd = new SnpSiftCmdRmInfo(args);
		else if (command.startsWith("RMREF")) cmd = new SnpSiftCmdRemoveReferenceGenotypes(args);
		else if (command.startsWith("VCF2TPED")) cmd = new SnpSiftCmdVcf2Tped(args);
		else if (command.startsWith("GENESETS")) cmd = new SnpSiftCmdGeneSets(args);
		else if (command.startsWith("GT")) cmd = new SnpSiftCmdGt(args);
		else if (command.startsWith("SIMPLIFYINDELS")) cmd = new SnpSiftCmdSimplifyIndels(args);
		else if (command.startsWith("PEDSHOW")) cmd = new SnpSiftCmdPedShow(args);
		else if (command.startsWith("EPISTASIS")) cmd = new SnpSiftCmdEpistasis(args);
		else usage("Unknown command '" + command + "'");

		// Help? Show help and exit
		if (help) {
			cmd.usage(null);
			return;
		}

		// Copy parsed parameters
		cmd.verbose = verbose;
		cmd.quiet = quiet;
		cmd.debug = debug;
		cmd.help = help;
		cmd.numWorkers = numWorkers;

		// Execute command
		cmd.run();

	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setQuiet(boolean quiet) {
		this.quiet = quiet;
	}

	public void setSaveOutput(boolean saveOutput) {
		this.saveOutput = saveOutput;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Show command line
	 */
	public void showCmd() {
		System.err.print(SnpSift.class.getSimpleName() + " " + command + " ");
		for (String a : args)
			System.err.print(a + " ");
		System.err.println("");
	}

	/**
	 * Show version number
	 */
	public void showVersion() {
		System.err.println(SnpSift.class.getSimpleName() + " version " + VERSION);
	}

	/**
	 * Convert a sanitized expression (from Galaxy) back to the original string
	 *
	 * References: http://www.mail-archive.com/galaxy-dev@lists.bx.psu.edu/msg00530.html
	 *
	 * @param str
	 * @return
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
	 * @param msg
	 */
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
				+ "\n\tepistasis     : Epistatic case control model (this feature is alpha)." //
				+ "\n\textractFields : Extract fields from VCF file into tab separated format." //
				+ "\n\tfilter        : Filter using arbitrary expressions" //
				+ "\n\tgeneSets      : Annotate using MSigDb gene sets (MSigDb includes: GO, KEGG, Reactome, BioCarta, etc.)" //
				+ "\n\tgt            : Add Genotype to INFO fields and remove genotype fields when possible." //
				+ "\n\tgwasCat       : Annotate using GWAS catalog" //
				+ "\n\thwe           : Calculate Hardy-Weimberg parameters and perform a godness of fit test." //
				+ "\n\tintersect     : Intersect intervals (genomic regions)." //
				+ "\n\tintervals     : Keep variants that intersect with intervals." //
				+ "\n\tintIdx        : Keep variants that intersect with intervals. Index-based method: Used for large VCF file and a few intervals to retrieve" //
				+ "\n\tjoin          : Join files by genomic region." //
				+ "\n\tphastCons     : Annotate using conservation scores (phastCons)." //
				+ "\n\tprivate       : Annotate if a variant is private to a family or group." //
				+ "\n\trmRefGen      : Remove reference genotypes." //
				+ "\n\trmInfo        : Remove INFO fields." //
				+ "\n\tsift          : Annotate using SIFT scores from a VCF file." //
				+ "\n\tsplit         : Split VCF by chromosome." //
				+ "\n\ttstv          : Calculate transiton to transversion ratio." //
				+ "\n\tvarType       : Annotate variant type (SNP,MNP,INS,DEL or MIXED)." //
				+ "\n\tvcf2tped      : Convert VCF to TPED." //
				);
		System.exit(1);
	}

	/**
	 * Show a warning message (up to MAX_ERRORS times)
	 * @param warn
	 */
	protected void warn(String warn) {
		if (!errCount.containsKey(warn)) errCount.put(warn, 0);

		int count = errCount.get(warn);
		errCount.put(warn, count + 1);

		if (count < MAX_ERRORS) System.err.println("WARNING: " + warn);
	}

}
