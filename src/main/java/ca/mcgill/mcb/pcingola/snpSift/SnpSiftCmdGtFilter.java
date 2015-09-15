package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.lang.LangFactory;
import ca.mcgill.mcb.pcingola.snpSift.lang.Value;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Expression;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Field;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.EffFormatVersion;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderEntry;

/**
 * Generic SnpSift genotype filter
 *
 * Removes genotypes matching the filter:
 * e.g. if the expression is "GQ < 20", all genotypes
 * with quality lower than 20 will be replaced
 * by './.' (missing)
 *
 * @author pablocingolani
 */
public class SnpSiftCmdGtFilter extends SnpSift {

	boolean inverse; // Inverse filter (i.e. do NOT show lines that match the filter)
	boolean exceptionIfNotFound; // Throw an exception of a field is not found?
	String expression; // Expression (as a string)
	String gtFieldName, gtFieldValue;
	Expression expr; // Expression (parsed expression string)
	String filterId; // FilterID string to add to FILTER field if the filter does NOT pass.
	ArrayList<HashSet<String>> sets;
	EffFormatVersion formatVersion;

	public SnpSiftCmdGtFilter() {
		super(null, "filter");
	}

	public SnpSiftCmdGtFilter(String args[]) {
		super(args, "filter");
	}

	/**
	 * Read a file as a string set
	 */
	public void addSet(String fileName) {
		// Open file and check
		String file = Gpr.readFile(fileName);
		if (file.isEmpty()) throw new RuntimeException("Could not read any entries from file '" + fileName + "'");

		// Create hash
		HashSet<String> set = new HashSet<String>();
		for (String str : file.split("\n"))
			set.add(str.trim());

		// Add set to array
		sets.add(set);
		if (verbose) System.err.println("Adding set '" + fileName + "', " + set.size() + " elements.");
	}

	/**
	 * Add string to FILTER vcf field
	 */
	void addVcfFilter(VcfEntry vcfEntry, String filterStr) {
		// Get current value
		String filter = vcfEntry.getFilterPass();
		if (filter.equals(".")) filter = ""; // Empty?
		// Append new value
		filter += (!filter.isEmpty() ? ";" : "") + filterStr; // Add this filter to the not-passed list
		vcfEntry.setFilterPass(filter);
	}

	/**
	 * Remove a string from FILTER vcf field
	 */
	void delVcfFilter(VcfEntry vcfEntry, String filterStr) {
		// Get current value
		String filter = vcfEntry.getFilterPass();
		StringBuilder sbFilter = new StringBuilder();

		// Split by semicolon and filter out the undesired values
		boolean removed = false;
		for (String f : filter.split(";")) {
			if (!f.equals(filterStr)) sbFilter.append((sbFilter.length() > 0 ? ";" : "") + f); // Append if it does not match filterStr
			else removed = true;
		}

		// Changed? Set new value
		if (removed) {
			if (debug) Gpr.debug("REMOVE:" + filter + "\t" + filterStr + "\t=>\t" + sbFilter);
			vcfEntry.setFilterPass(sbFilter.toString());
		}
	}

	/**
	 * Evaluate all genotypes for this entry
	 */
	boolean evaluate(VcfEntry vcfEntry) {
		if (debug) Gpr.debug(vcfEntry.toStringNoGt());

		boolean ok = false;
		for (VcfGenotype vgt : vcfEntry) {
			boolean change = evaluate(vcfEntry, vgt);

			if (debug) Gpr.debug("\t\tevaluate:" + change + "\t" + vgt);

			if (change) {
				ok = true;
				vgt.set(gtFieldName, gtFieldValue); // Set genotype to missing
			}
		}

		if (debug && ok) Gpr.debug("VCF entry changed:\t" + vcfEntry);
		return ok;
	}

	/**
	 * Iterate over all possible 'FieldIterator' values until one 'true' is found, otherwise return false.
	 */
	boolean evaluate(VcfEntry vcfEntry, VcfGenotype vcfGenotype) {
		FieldIterator fieldIterator = FieldIterator.get();
		fieldIterator.reset();

		boolean all = true, any = false;

		if (debug) Gpr.debug("VCF entry:" + vcfEntry.toStringNoGt() + "\t" + vcfGenotype);

		do {
			Value eval = expr.eval(vcfGenotype);
			if (debug) Gpr.debug("\tEval: " + eval + "\tFieldIterator: " + fieldIterator);

			all &= eval.asBool();
			any |= eval.asBool();

			if ((fieldIterator.getType() == Field.TYPE_ALL) && !all) {
				boolean ret = inverse ^ all;
				if (debug) Gpr.debug("\tResult [ALL]: " + ret);
				return ret;
			}

			if ((fieldIterator.getType() == Field.TYPE_ANY) && any) {
				boolean ret = inverse ^ any;
				if (debug) Gpr.debug("\tResult [ANY]: " + ret);
				return ret;
			}

			if (fieldIterator.hasNext()) fieldIterator.next(); // End of iteration?
			else break;
		} while (true);

		// Iteration type (ALL or ANY)?
		boolean ret = false;

		if (fieldIterator.getType() == Field.TYPE_ALL) {
			ret = all;
			if (debug) Gpr.debug("\tResult [ALL]: " + ret);
		} else {
			ret = any;
			if (debug) Gpr.debug("\tResult [ANY]: " + ret);
		}

		// Inverse result
		ret = inverse ^ ret;
		if (debug && inverse) Gpr.debug("\tResult [INV]: " + ret);

		return ret;
	}

	/**
	 * Filter a file
	 */
	public List<VcfEntry> filter(String fileName, String expression, boolean createList) {
		vcfInputFile = fileName;
		this.expression = expression;
		return run(createList);
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> addHeader = super.headers();
		String expr = expression.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
		if (!filterId.isEmpty()) addHeader.add(new VcfHeaderEntry("##FILTER=<ID=" + filterId + ",Description=\"" + VERSION + ", Expression used: " + expr + "\">"));
		return addHeader;
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		verbose = false;
		inverse = false;
		vcfInputFile = null;
		filterId = SnpSift.class.getSimpleName();
		sets = new ArrayList<HashSet<String>>();
		formatVersion = null; // VcfEffect.FormatVersion.FORMAT_SNPEFF_3;
		exceptionIfNotFound = false;

		// By default, replace genotype field by MISSING value (i.e. remove genotype field)
		gtFieldName = "GT";
		gtFieldValue = ".";
	}

	/**
	 * Parse command line options
	 */
	@Override
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Argument starts with '-'?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-h":
				case "-help":
					usage(null);
					break;

				case "-f":
				case "--file":
					vcfInputFile = args[++i];
					break;

				case "-s":
				case "--set":
					addSet(args[++i]);
					break;

				case "--errmissing":
					exceptionIfNotFound = true;
					break;

				case "-n":
				case "--inverse":
					inverse = true;
					break;

				case "-gn":
				case "--field":
					gtFieldName = args[++i];
					break;

				case "-gv":
				case "--value":
					gtFieldValue = args[++i];
					break;

				case "--format":
					String formatVer = args[++i];
					if (formatVer.equals("2")) formatVersion = EffFormatVersion.FORMAT_EFF_2;
					else if (formatVer.equals("3")) formatVersion = EffFormatVersion.FORMAT_EFF_3;
					else usage("Unknown format version '" + formatVer + "'");
					break;

				case "-e":
				case "--exprfile":
					String exprFile = args[++i];
					if (verbose) System.err.println("Reading expression from file '" + exprFile + "'");
					expression = Gpr.readFile(exprFile);
					break;

				default:
					usage("Unknown option '" + arg + "'");
				}
			} else if (expression == null) expression = arg;
			else if (vcfInputFile == null) vcfInputFile = arg;
			else usage("Unknown parameter '" + arg + "'");
		}

		if (expression == null) usage("Missing filter expression!");
		if (vcfInputFile == null) vcfInputFile = "-";
	}

	/**
	 * Parse expression
	 */
	public Expression parseExpression(String expression) throws Exception {
		if (debug) Gpr.debug("Parse expression: \"" + expression + "\"");

		LangFactory lf = new LangFactory();
		expr = lf.compile(expression);

		if (expr == null) {
			System.err.println("Fatal error: Cannot build expression tree.");
			System.exit(-1);
		}

		if (debug) Gpr.debug("Expression: " + expr);
		return expr;
	}

	@Override
	public void run() {
		run(false);
	}

	/**
	 * Run filter
	 * @param createList : If true, create a list with the results. If false, show results on STDOUT
	 * @return If 'createList' is true, return a list containing all vcfEntries that passed the filter. Otherwise return null.
	 */
	public List<VcfEntry> run(boolean createList) {
		// Debug mode?
		if (debug) Expression.debug = true;

		// Parse expression
		try {
			parseExpression(expression);
		} catch (Exception e) {
			e.printStackTrace();
			usage("Error parsing expression: '" + expression + "'");
		}

		// Initialize
		LinkedList<VcfEntry> passEntries = (createList ? new LinkedList<VcfEntry>() : null);

		// Open and read entries
		VcfFileIterator vcfFile = openVcfInputFile();
		showHeader = !createList;
		for (VcfEntry vcfEntry : vcfFile) {
			// Show header before first entry
			processVcfHeader(vcfFile);

			// Evaluate expression
			evaluate(vcfEntry);

			if (passEntries != null) passEntries.add(vcfEntry); // Do not show. just add to the list (this is used for debugging and testing)
			else System.out.println(vcfEntry);
		}

		return passEntries;
	}

	/**
	 * Usage message
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.out.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar filter [options] 'expression' [input.vcf]");
		System.err.println("Options:");
		System.err.println("\t-e  | --exprFile <file>    : Read expression from a file");
		System.err.println("\t-f  | --file <input.vcf>   : VCF input file. Default: STDIN");
		System.err.println("\t-gn | --field <name>       : Field name to replace if filter is true. Default: '" + gtFieldName + "'");
		System.err.println("\t-gv | --value <value>      : Field value to replace if filter is true. Default: '" + gtFieldValue + "'");
		System.err.println("\t-n  | --inverse            : Inverse. Show lines that do not match filter expression");
		System.err.println("\t-s  | --set <file>         : Create a SET using 'file'");
		System.err.println("\t--errMissing               : Error is a field is missing. Default: " + exceptionIfNotFound);
		System.err.println("\t--format <format>          : SnpEff format version: {2, 3}. Default: " + (formatVersion == null ? "Auto" : formatVersion));
		System.exit(-1);
	}

}
