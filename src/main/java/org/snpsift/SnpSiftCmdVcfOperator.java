package org.snpsift;

import java.util.ArrayList;
import java.util.List;

import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;

/**
 * Annotate a field based on an operation (max, min, etc.) of other VCF fields
 *
 * @author pcingola
 */
public class SnpSiftCmdVcfOperator extends SnpSift {

	public enum Operator {
		PLUS, MINUS, MAX, MIN, PRODUCT;
	};

	protected String fields = "";
	protected String outField = "";
	protected Operator operator;

	public SnpSiftCmdVcfOperator(String args[]) {
		super(args, "operator");
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> headerInfos = super.headers();
		headerInfos.add(new VcfHeaderInfo(outField, VcfInfoType.Float, "" + 1, "Operation '" + operator + "' applied to fileds '" + fields + "'"));
		return headerInfos;
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
					break;

				case "-op":
					String op = "";
					if ((i + 1) < args.length) op = args[++i];
					else usage("Option '-fields' without argument");
					operator = Operator.valueOf(op);
					break;

				default:
					// Unrecognized option? may be it's command specific. Let command parse it
					usage("Unknown command line option '" + arg + "'");
				}
			} else usage("Unused command line argument '" + arg + "'");
		}

		// Show version and command
		if (help) usage(null);

		// Sanity check
		if (fields.isEmpty()) usage("Missing '-fields' input field names");
		if (outField.isEmpty()) usage("Missing '-outfield' output field name");
		if (operator == null) usage("Missing '-op' operator field");
	}

	/**
	 * Create new sequences
	 */
	@Override
	public boolean run() {
		return true;
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
