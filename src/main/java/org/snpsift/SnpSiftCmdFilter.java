package org.snpsift;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.EffFormatVersion;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpsift.lang.LangFactory;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.Expression;
import org.snpsift.lang.expression.Field;
import org.snpsift.lang.expression.FieldIterator;

/**
 * Generic SnpSift filter
 *
 * Filter out data based on VCF attributes:
 * 		- Chromosome, Position, etc.
 *
 * 		- Intersecting intervals (BED file)
 *
 * 		- Quality, Coverage, etc.
 *
 * 		- Any INFO field
 * 			- Parse expression
 * 			- Int, double fields: FiledZZ == N, FiledZZ < X, FiledZZ > X, FiledZZ <= X, FiledZZ >= X
 * 			- String: FiledZZ eq "someString", FiledZZ =~ "some*regex$"
 *
 * 		- Samples informations
 * 			- s50 (SNPs that appear in 50% of samples or more)
 * 			- Singletons
 * 			- Doubletons
 * 			- Tripletons
 * 			- negate all previous expressions
 * 			- pValue (Fisher exact test)
 *
 * 		- Database information
 * 			- Known (e.g. in dbSnp)
 * 			- Novel (e.g. NOT in dbSnp)
 *
 * @author pablocingolani
 */
public class SnpSiftCmdFilter extends SnpSift {

	boolean usePassField; // Use Filter field
	boolean inverse; // Inverse filter (i.e. do NOT show lines that match the filter)
	boolean exceptionIfNotFound; // Throw an exception of a field is not found?
	String expression; // Expression (as a string)
	Expression expr; // Expression (parsed expression)
	String filterId; // FilterID string to add to FILTER field if the filter does NOT pass.
	String addFilterField; // Add a string to FILTER field.
	String rmFilterField; // Remove String from FILTER field
	ArrayList<HashSet<String>> sets;
	EffFormatVersion formatVersion;

	public SnpSiftCmdFilter() {
		super(null, "filter");
	}

	public SnpSiftCmdFilter(String args[]) {
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

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		boolean eval = evaluate(vcfEntry);

		// Use FILTER field? ('PASS' or filter name)
		if (usePassField) {
			if (eval) vcfEntry.setFilter(VcfEntry.FILTER_PASS); // Filter passed: PASS
			else vcfEntry.addFilter(filterId); // Filter not passed? Show filter name
		}

		// Add or delete strings from filter field
		if (eval) {
			if (addFilterField != null) vcfEntry.addFilter(addFilterField); // Filter passed? Add to FILTER field
			if (rmFilterField != null) vcfEntry.delFilter(rmFilterField); // Filter passed? Delete string from FILTER field
		}

		return eval;
	}

	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		super.annotateInit(vcfFile);

		try {
			// Parse expression
			parseExpression(expression);
		} catch (Exception e) {
			e.printStackTrace();
			usage("Error parsing expression: '" + expression + "'");
		}
		return true;
	}

	/**
	 * Iterate over all possible 'FieldIterator' values until one 'true' is found, otherwise return false.
	 */
	boolean evaluate(VcfEntry vcfEntry) {
		FieldIterator fieldIterator = FieldIterator.get();
		fieldIterator.reset();

		boolean all = true, any = false;

		if (debug) Gpr.debug("VCF entry:" + vcfEntry.toStringNoGt());

		do {
			Value eval = expr.eval(vcfEntry);
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
		if (!filterId.isEmpty()) addHeader.add(new VcfHeaderEntry("##FILTER=<ID=" + filterId + ",Description=\"" + VERSION_NO_NAME + ", Expression used: " + expr + "\">"));
		return addHeader;
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		verbose = false;
		usePassField = false;
		inverse = false;
		vcfInputFile = null;
		filterId = SnpSift.class.getSimpleName();
		addFilterField = null;
		rmFilterField = null;
		sets = new ArrayList<HashSet<String>>();
		formatVersion = null; // VcfEffect.FormatVersion.FORMAT_SNPEFF_3;
		exceptionIfNotFound = false;
	}

	/**
	 * Parse command line options
	 */
	@Override
	public void parseArgs(String[] args) {
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

				case "-p":
				case "--pass":
					usePassField = true;
					break;

				case "--errmissing":
					exceptionIfNotFound = true;
					break;

				case "-i":
				case "--filterid":
					usePassField = true;
					filterId = args[++i];
					break;

				case "-a":
				case "--addfilter":
					addFilterField = args[++i];
					break;

				case "-r":
				case "--rmfilter":
					rmFilterField = args[++i];
					break;

				case "-n":
				case "--inverse":
					inverse = true;
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
	}

	/**
	 * Parse expression
	 */
	public Expression parseExpression(String expression) throws Exception {
		if (debug) Gpr.debug("Parse expression: \"" + expression + "\"");

		// Create a language factory
		LangFactory langFactory = new LangFactory(sets, formatVersion, exceptionIfNotFound);

		// Parse tree and create expression
		expr = langFactory.compile(expression);

		if (expression == null) {
			System.err.println("Fatal error: Cannot build expression tree.");
			System.exit(-1);
		}

		if (debug) Gpr.debug("Expression: " + expression);
		return expr;
	}

	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run filter
	 * @param createList : If true, create a list with the results. If false, show results on STDOUT
	 * @return If 'createList' is true, return a list containing all vcfEntries that passed the filter. Otherwise return null.
	 */
	public List<VcfEntry> run(boolean createList) {
		// Debug mode?
		if (debug) Expression.debug = true;

		// Initialize
		LinkedList<VcfEntry> passEntries = (createList ? new LinkedList<VcfEntry>() : null);

		// Open and read entries
		showVcfHeader = !createList;
		VcfFileIterator vcfFile = openVcfInputFile();
		annotateInit(vcfFile);
		for (VcfEntry vcfEntry : vcfFile) {
			processVcfHeader(vcfFile);

			// Annotate (evaluate expression)
			boolean show = annotate(vcfEntry);

			// Always show entries (just change FILTER field)
			if (usePassField || (addFilterField != null) || (rmFilterField != null)) show = true;

			// Show
			if (show) {
				if (passEntries != null) passEntries.add(vcfEntry); // Do not show. just add to the list (this is used for debugging and testing)
				else System.out.println(vcfEntry);
			}
		}
		annotateFinish(vcfFile);

		return passEntries;
	}

	public void setAddFilterField(String addFilterField) {
		this.addFilterField = addFilterField;
	}

	public void setExceptionIfNotFound(boolean exceptionIfNotFound) {
		this.exceptionIfNotFound = exceptionIfNotFound;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	public void setFormatVersion(EffFormatVersion formatVersion) {
		this.formatVersion = formatVersion;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public void setRmFilterField(String rmFilterField) {
		this.rmFilterField = rmFilterField;
	}

	public void setSets(ArrayList<HashSet<String>> sets) {
		this.sets = sets;
	}

	public void setUsePassField(boolean usePassField) {
		this.usePassField = usePassField;
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
		System.err.println("\t-a|--addFilter <str>  : Add a string to FILTER VCF field if 'expression' is true. Default: '' (none)");
		System.err.println("\t-e|--exprFile <file>  : Read expression from a file");
		System.err.println("\t-f|--file <input.vcf> : VCF input file. Default: STDIN");
		System.err.println("\t-i|--filterId <str>   : ID for this filter (##FILTER tag in header and FILTER VCF field). Default: '" + filterId + "'");
		System.err.println("\t-n|--inverse          : Inverse. Show lines that do not match filter expression");
		System.err.println("\t-p|--pass             : Use 'PASS' field instead of filtering out VCF entries");
		System.err.println("\t-r|--rmFilter <str>   : Remove a string from FILTER VCF field if 'expression' is true (and 'str' is in the field). Default: '' (none)");
		System.err.println("\t-s|--set <file>       : Create a SET using 'file'");
		System.err.println("\t--errMissing          : Error is a field is missing. Default: " + exceptionIfNotFound);
		System.err.println("\t--format <format>     : SnpEff format version: {2, 3}. Default: " + (formatVersion == null ? "Auto" : formatVersion));
		System.err.println("\t--galaxy              : Used from Galaxy (expressions have been sanitized).");
		System.exit(-1);
	}

}
