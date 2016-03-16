package org.snpsift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.lang.LangFactory;
import org.snpsift.lang.Value;
import org.snpsift.lang.expression.Expression;
import org.snpsift.lang.expression.FieldIterator;

/**
 * Extract fields from VCF file to a TXT (tab separated) format
 *
 * @author pablocingolani
 */
public class SnpSiftCmdExtractFields extends SnpSift {

	public static final int SHOW = 10000;

	String vcfFile;
	String sameFieldSeparator; // Separate within field
	String emptyFieldString; // Use this string in case of empty results
	List<String> expressionStrs;
	List<Expression> expressions;

	public SnpSiftCmdExtractFields(String args[]) {
		super(args, "extractFields");
	}

	/**
	 * Iterate over all possible 'FieldIterator' values until one 'true' is found, otherwise return false.
	 */
	String evaluate(Expression expr, VcfEntry vcfEntry) {
		FieldIterator fieldIterator = FieldIterator.get();
		fieldIterator.reset();

		StringBuilder values = new StringBuilder();
		do {
			// Get value
			Value value = expr.eval(vcfEntry);

			// Separate
			if (values.length() > 0) values.append(sameFieldSeparator);

			// Append values
			String valStr = value.asString();
			if (valStr == null || valStr.isEmpty()) values.append(emptyFieldString);
			else values.append(value);

			// Iterate?
			if (fieldIterator.hasNext()) fieldIterator.next();
			else break;

		} while (true);

		return values.toString();
	}

	@Override
	public void init() {
		sameFieldSeparator = "\t";
		emptyFieldString = "";
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		expressionStrs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (isOpt(arg)) {
				if (arg.equals("-s")) sameFieldSeparator = args[++i];
				else if (arg.equals("-e")) emptyFieldString = args[++i];
			} else {
				// Non-option parameters
				if (vcfFile == null) vcfFile = arg; // VCF file
				else expressionStrs.add(arg); // Read all field names expressions
			}
		}

		if (expressionStrs.isEmpty()) usage("Missing field names");
	}

	/**
	 * Parse fields
	 */
	List<Expression> parseFields(List<String> expressionsStr) {
		List<Expression> fields = new ArrayList<Expression>();
		for (String exprStr : expressionsStr) {
			// Parse and create field
			// Field field = new Field(fieldName);
			LangFactory lf = new LangFactory();
			Expression field;
			try {
				field = lf.compile(exprStr);
			} catch (Exception e) {
				throw new RuntimeException("Error parsing expression '" + exprStr + "'", e);
			}
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Run main algorithm
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	public List<String> run(boolean createList) {
		LinkedList<String> list = new LinkedList<String>();

		// Parse fiels
		expressions = parseFields(expressionStrs);

		// Show title
		if (!createList) {
			String sep = "";
			for (String fieldName : expressionStrs) {
				System.out.print(sep + fieldName);
				sep = "\t";
			}
			System.out.println("");
		}

		//---
		// Iterate on file
		//---
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		vcf.setDebug(debug);

		for (VcfEntry ve : vcf) {
			StringBuilder out = new StringBuilder();
			for (Expression f : expressions) {
				String val = evaluate(f, ve);
				out.append(val + "\t");
			}

			// Show line
			out.deleteCharAt(out.length() - 1); // Remove last '\t'
			if (createList) list.add(out.toString());
			else System.out.println(out);
		}

		return list;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar extractFields [options] file.vcf fieldName1 fieldName2 ... fieldNameN > tabFile.txt\n" //
				+ "\nOptions:" //
				+ "\n\t-s     : Same field separator. Default: '" + sameFieldSeparator + "'" //
				+ "\n\t-e     : Empty field. Default: '" + emptyFieldString + "'" //
		);

		System.exit(1);
	}
}
