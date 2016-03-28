package org.snpsift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.lang.expression.Expression;

import net.sf.samtools.util.RuntimeEOFException;

/**
 * Annotate a field based on an operation (max, min, etc.) of other VCF fields
 *
 * @author pcingola
 */
public class SnpSiftCmdVcfOperator extends SnpSift {

	public enum Operator {
		PLUS, MAX, MIN, PRODUCT;
	}

	private static final List<Double> EMPTY_VALS = new LinkedList<>();;

	protected String fields;
	protected String outField;
	protected Operator operator;
	protected String infoFields[];

	public SnpSiftCmdVcfOperator(String args[]) {
		super(args, "operator");
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		List<Double> values = getValues(vcfEntry);
		Double res = applyOp(values);
		if (res != null) vcfEntry.addInfo(outField, "" + res);
		return false;
	}

	public Double applyOp(List<Double> vals) {
		if (vals.isEmpty()) return null;

		double res = initialValue();
		for (Double val : vals) {
			switch (operator) {
			case MIN:
				res = Math.min(res, val);
				break;

			case MAX:
				res = Math.max(res, val);
				break;

			case PLUS:
				res += val;
				break;

			case PRODUCT:
				res *= val;
				break;

			default:
				throw new RuntimeEOFException("Unknown operator '" + operator + "'");
			}

		}

		return res;
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	public List<Double> getValues(VcfEntry vcfEntry) {
		List<Double> vals = new ArrayList<>();

		for (String field : infoFields)
			vals.addAll(getValues(vcfEntry, field));

		return vals;
	}

	/**
	 * Parse an INFO field (can be multiple valued) and return a list of Double
	 */
	public List<Double> getValues(VcfEntry vcfEntry, String field) {
		String valsStr = vcfEntry.getInfo(field);
		if (valsStr == null || valsStr.isEmpty()) return EMPTY_VALS;

		List<Double> vals = new LinkedList<>();

		// Split by comma
		for (String valStr : valsStr.split(",")) {
			if (VcfEntry.isEmpty(valStr)) continue;

			try {
				double v = Double.parseDouble(valStr);
				vals.add(v);
			} catch (Exception e) {
				// Cannot convert? Skip this value
			}
		}

		return vals;
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> headerInfos = super.headers();
		headerInfos.add(new VcfHeaderInfo(outField, VcfInfoType.Float, "" + 1, "Operation '" + operator + "' applied to fileds '" + fields + "'"));
		return headerInfos;
	}

	protected double initialValue() {
		switch (operator) {
		case MIN:
			return Double.MAX_VALUE;

		case MAX:
			return Double.MIN_VALUE;

		case PLUS:
			return 0.0;

		case PRODUCT:
			return 1.0;

		default:
			throw new RuntimeEOFException("Unknown operator '" + operator + "'");
		}
	}

	@Override
	public void parseArgs(String[] args) {
		this.args = args;
		if (args == null || args.length < 1) usage(null);

		new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// These options are available for allow all commands
			// Is it a command line option?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-fields":
					if ((i + 1) < args.length) fields = args[++i];
					else usage("Option '-fields' without argument");

					infoFields = fields.split(",");
					break;

				case "-op":
					String op = "";
					if ((i + 1) < args.length) op = args[++i];
					else usage("Option '-fields' without argument");
					try {
						operator = Operator.valueOf(op.toUpperCase());
					} catch (IllegalArgumentException e) {
						usage("Unknown operator '" + op + "'");
					}
					break;

				case "-outfield":
					if ((i + 1) < args.length) outField = args[++i];
					else usage("Option '-outField' without argument");
					break;

				default:
					// Unrecognized option? may be it's command specific. Let command parse it
					usage("Unknown command line option '" + arg + "'");
				}
			} else if (vcfInputFile == null || vcfInputFile.isEmpty()) vcfInputFile = arg;
			else usage("Unused command line argument '" + arg + "'");
		}

		// Show version and command
		if (help) usage(null);

		// Sanity check
		if (fields == null || fields.isEmpty()) usage("Missing '-fields' input field names");
		if (outField == null || outField.isEmpty()) usage("Missing '-outfield' output field name");
		if (operator == null) usage("Missing '-op' operator field");

	}

	/**
	 * Annotate
	 */
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
		LinkedList<VcfEntry> vcfEntries = (createList ? new LinkedList<VcfEntry>() : null);

		// Open and read entries
		showHeader = !createList;
		VcfFileIterator vcfFile = openVcfInputFile();
		annotateInit(vcfFile);
		for (VcfEntry vcfEntry : vcfFile) {
			processVcfHeader(vcfFile);

			// Annotate (evaluate expression)
			annotate(vcfEntry);

			if (vcfEntries != null) vcfEntries.add(vcfEntry); // Do not show. just add to the list (this is used for debugging and testing)
			else System.out.println(vcfEntry);
		}

		return vcfEntries;
	}

	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();
		System.err.println("Kew version " + VERSION);
		System.err.println("Usage: kew maxPopFreq [options] [files]");
		System.err.println("");
		System.err.println("\nCommands line options: ");
		System.err.println("\t-fields <filedNames>    : Input VCF field names (comma separated list).");
		System.err.println("\t-op <operator>          : Operator to be applied to the fields");
		System.err.println("\t-outfield <filedName>   : Ouptut field name.");

		System.exit(-1);
	}

}
