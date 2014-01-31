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
			if (value != null) values.append(value);

			// End of iteration?
			if (fieldIterator.hasNext()) {
				values.append("\t");
				fieldIterator.next();
			} else break;
		} while (true);

		return values.toString();
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		int argNum = 0;
		if (args.length == 0) usage(null);

		if (args.length >= argNum) vcfFile = args[argNum++];
		else usage("Missing 'file.vcf'");

		// Read all field named
		fieldNames = new ArrayList<String>();
		for (int i = argNum; i < args.length; i++)
			fieldNames.add(args[i]);

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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar extractFields file.vcf filedName1 filedName2 ... filedNameN > tabFile.txt");
		System.exit(1);
	}
}
