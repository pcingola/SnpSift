package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpSift.lang.LangFactory;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.Field;
import ca.mcgill.mcb.pcingola.snpSift.lang.expression.FieldIterator;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
	ArrayList<String> fieldNames;
	ArrayList<Field> fields;

	public SnpSiftCmdExtractFields(String args[]) {
		super(args, "extractFields");
	}

	/**
	 * Iterate over all possible 'FieldIterator' values until one 'true' is found, otherwise return false.
	 * @param vcfEntry
	 * @return
	 */
	String evaluate(Field field, VcfEntry vcfEntry) {
		FieldIterator fieldIterator = FieldIterator.get();
		fieldIterator.reset();

		StringBuilder values = new StringBuilder();
		do {
			// Get value
			String value = field.getFieldString(vcfEntry);

			// Separate
			if (values.length() > 0) values.append(sameFieldSeparator);

			// Append values
			if (value == null || value.isEmpty()) values.append(emptyFieldString);
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
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		fieldNames = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (isOpt(arg)) {
				if (arg.equals("-s")) sameFieldSeparator = args[++i];
				else if (arg.equals("-e")) emptyFieldString = args[++i];
			} else {
				// Non-option parameters
				if (vcfFile == null) vcfFile = arg; // VCF file
				else fieldNames.add(arg); // Read all field names expressions
			}
		}

		if (fieldNames.isEmpty()) usage("Missing field names");
	}

	/**
	 * Parse fields
	 * @param fieldNames
	 * @return
	 */
	ArrayList<Field> parseFields(ArrayList<String> fieldNames) {
		ArrayList<Field> fields = new ArrayList<Field>();
		for (String fieldName : fieldNames) {
			// Parse and create field
			// Field field = new Field(fieldName);
			LangFactory lf = new LangFactory();
			Field field;
			try {
				field = lf.parseField(fieldName);
			} catch (Exception e) {
				throw new RuntimeException("Error parsing field '" + fieldName + "'", e);
			}
			field.setExceptionIfNotFound(false); // Otherwise exceptions are thrown
			fields.add(field);
		}
		return fields;
	}

	/**
	 * Run main algorithm
	 */
	@Override
	public void run() {
		run(false);
	}

	public List<String> run(boolean createList) {
		LinkedList<String> list = new LinkedList<String>();

		// Parse fiels
		fields = parseFields(fieldNames);

		// Show title
		if (!createList) {
			String sep = "#";
			for (String fieldName : fieldNames) {
				System.out.print(sep + fieldName);
				sep = "\t";
			}
			System.out.println("");
		}

		//---
		// Iterate on file
		//---
		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
		for (VcfEntry ve : vcf) {
			StringBuilder out = new StringBuilder();
			for (Field f : fields) {
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar extractFields [options] file.vcf filedName1 filedName2 ... filedNameN > tabFile.txt\n" //
				+ "\nOptions:" //
				+ "\n\t-s     : Same field separator. Default: '" + sameFieldSeparator + "'" //
				+ "\n\t-e     : Empty field. Default: '" + emptyFieldString + "'" //
		);

		System.exit(1);
	}
}
