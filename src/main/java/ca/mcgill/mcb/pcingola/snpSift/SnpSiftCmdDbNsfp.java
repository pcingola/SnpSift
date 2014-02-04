package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.mcb.pcingola.fileIterator.DbNsfpEntry;
import ca.mcgill.mcb.pcingola.fileIterator.DbNsfpFileIterator;
import ca.mcgill.mcb.pcingola.fileIterator.GuessTableTypes;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

/**
 * Annotate a VCF file with dbNSFP. 
 * 
 * The dbNSFP is an integrated database of functional predictions from multiple algorithms for the comprehensive 
 * collection of human non-synonymous SNPs (NSs). Its current version (ver 1.1) is based on CCDS version 20090327 
 * and includes a total of 75,931,005 NSs. It compiles prediction scores from four prediction algorithms (SIFT, 
 * Polyphen2, LRT and MutationTaster), two conservation scores (PhyloP and GERP++) and other related information.
 *   
 * References:
 *  
 * 		http://sites.google.com/site/jpopgen/dbNSFP
 * 
 * 		Paper: Liu X, Jian X, and Boerwinkle E. 2011. dbNSFP: a lightweight database of human non-synonymous SNPs and their
 * 		functional predictions. Human Mutation. 32:894-899.
 * 
 * @author lletourn
 * 
 */
public class SnpSiftCmdDbNsfp extends SnpSift {

	public static final String VCF_INFO_PREFIX = "dbNSFP_";
	public static final String DEFAULT_FIELDS_NAMES_TO_ADD = "Ensembl_transcriptid,Uniprot_acc,Interpro_domain,SIFT_score,Polyphen2_HVAR_pred,GERP++_NR,GERP++_RS,29way_logOdds,1000Gp1_AF,1000Gp1_AFR_AF,1000Gp1_EUR_AF,1000Gp1_AMR_AF,1000Gp1_ASN_AF,ESP6500_AA_AF,ESP6500_EA_AF";

	public static final int MIN_JUMP = 100;
	public static final int SHOW_ANNOTATED = 1;

	protected Map<String, String> fieldsToAdd;
	protected Map<String, String> fieldsDescription;
	protected Map<String, String> fieldsType;
	protected boolean annotateAll; // Annotate empty fields as well?
	protected boolean collapseRepeatedValues; // Collapse values if repeated?
	protected boolean tabixCheck = true;
	protected String dbNsfpFileName;
	protected String vcfFileName;
	protected int count = 0;
	protected int countAnnotated = 0;
	protected DbNsfpFileIterator dbNsfpFile;
	protected VcfFileIterator vcfFile;
	protected DbNsfpEntry currentDbEntry;
	protected String fieldsNamesToAdd;

	public SnpSiftCmdDbNsfp(String args[]) {
		super(args, "dbnsfp");
	}

	/**
	 * Add some lines to header before showing it
	 * 
	 * @param vcfFile
	 */
	@Override
	protected void addHeader(VcfFileIterator vcfFile) {
		super.addHeader(vcfFile);
		for (String fieldName : fieldsToAdd.keySet()) {
			// Get type
			String type = fieldsType.get(fieldName);
			if (type == null) {
				System.err.println("WARNING: Cannot find type for field '" + fieldName + "', using 'String'.");
				type = VcfInfoType.String.toString();
			}

			vcfFile.getVcfHeader().addLine("##INFO=<ID=" + VCF_INFO_PREFIX + fieldName + ",Number=A,Type=" + type + ",Description=\"" + fieldsToAdd.get(fieldName) + "\">");
		}
	}

	/**
	 * Annotate a vcf entry
	 * @param vcf
	 * @throws IOException
	 */
	public void annotate(VcfEntry vcf) throws IOException {
		// Find in database
		DbNsfpEntry dbEntry = findDbEntry(vcf);
		if (dbEntry == null) return;

		// Add all INFO fields that refer to this allele
		boolean annotated = false;
		StringBuilder info = new StringBuilder();
		for (String fieldKey : fieldsToAdd.keySet()) {
			boolean empty = true;
			info.setLength(0);

			// For each ALT
			for (String alt : vcf.getAlts()) {
				// Are there any values to annotate?
				if (dbEntry.hasValues(alt)) {

					// Map<String, String> values = currentDbEntry.getAltAlelleValues(alt);
					String val = dbEntry.getCsv(alt, fieldKey);

					if (val == null) {
						// No value: Don't add		
					} else if (val.isEmpty() || val.equals(".")) {
						// Empty: Mark as 'empty'
						empty = true;
					} else {
						if (info.length() > 0) info.append(',');
						info.append(val);
						empty = false;
					}
				}
			}

			if (annotateAll || !empty) {
				String infoStr = info.toString();
				if (infoStr.isEmpty()) infoStr = ".";
				infoStr = infoStr.replace(';', ',').replace('\t', '_').replace(' ', '_'); // Make sure all characters are valid for VCF field

				vcf.addInfo(VCF_INFO_PREFIX + fieldKey, infoStr);
				annotated = true;
			}
		}

		if (annotated) {
			countAnnotated++;
			if (debug) Gpr.debug("Annotated: " + vcf.getChromosomeName() + ":" + vcf.getStart());
			else if (verbose) {
				if (countAnnotated % SHOW_ANNOTATED == 0) {
					if (countAnnotated % (100 * SHOW_ANNOTATED) == 0) System.err.print(".\n" + countAnnotated + "\t" + vcf.getChromosomeName() + ":" + vcf.getStart() + "\t");
					else System.err.print('.');
				}
			}
		}
	}

	/**
	 * Check that all fields to add are available
	 * @throws IOException
	 */
	public void checkFieldsToAdd() throws IOException {
		// Check that all fields have a descriptor (used in VCF header)
		if (verbose) {
			for (String filedName : dbNsfpFile.getFieldNames())
				if (fieldsDescription.get(filedName) == null) System.err.println("WARNING: Field (column) '" + filedName + "' does not have an approriate field descriptor.");
		}

		// Check that all "field to add" are in the database
		for (String fieldKey : fieldsToAdd.keySet())
			if (!dbNsfpFile.hasField(fieldKey)) fatalError("dbNsfp does not have field '" + fieldKey + "' (file '" + dbNsfpFileName + "')");
	}

	/**
	 * Finish up annotation process
	 */
	public void endAnnotate() {
		vcfFile.close();
		dbNsfpFile.close();
	}

	/**
	 * Find a matching db entry for a vcf entry
	 * @param vcfEntry
	 * @throws IOException
	 */
	public DbNsfpEntry findDbEntry(VcfEntry vcfEntry) throws IOException {
		//---
		// Find db entry 
		//---
		if (debug) System.err.println("Looking for " + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart() + ". Current DB: " + (currentDbEntry == null ? "null" : currentDbEntry.getChromosomeName() + ":" + currentDbEntry.getStart()));
		while (true) {
			if (currentDbEntry == null) {
				currentDbEntry = dbNsfpFile.next(); // Read next DB entry
				if (currentDbEntry == null) return null; // End of database?
			}

			if (debug) Gpr.debug("Current Db Entry:" + currentDbEntry.getChromosomeName() + ":" + currentDbEntry.getStart() + "\tLooking for: " + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());

			// Find entry
			if (currentDbEntry.getChromosomeName().equals(vcfEntry.getChromosomeName())) {
				// Same chromosome

				// Same position? => Found
				if (vcfEntry.getStart() == currentDbEntry.getStart()) {
					// Found db entry! Break loop and proceed with annotations
					if (debug) Gpr.debug("Found Db Entry:" + currentDbEntry.getChromosomeName() + ":" + currentDbEntry.getStart());
					return currentDbEntry;
				} else if (vcfEntry.getStart() < currentDbEntry.getStart()) {
					// Same chromosome, but positioned after => No db entry found
					if (debug) Gpr.debug("No db entry found:\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
					return null;
				} else if ((vcfEntry.getStart() - currentDbEntry.getStart()) > MIN_JUMP) {
					// Is it far enough? Don't iterate, jump
					if (debug) Gpr.debug("Position jump:\t" + currentDbEntry.getChromosomeName() + ":" + currentDbEntry.getStart() + "\t->\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
					dbNsfpFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart());
					currentDbEntry = dbNsfpFile.next();
				} else {
					// Just read next entry to get closer
					currentDbEntry = dbNsfpFile.next();
				}
			} else if (!currentDbEntry.getChromosomeName().equals(vcfEntry.getChromosomeName())) {
				// Different chromosome? => Jump to chromosome
				if (debug) Gpr.debug("Chromosome jump:\t" + currentDbEntry.getChromosomeName() + ":" + currentDbEntry.getStart() + "\t->\t" + vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart());
				dbNsfpFile.seek(vcfEntry.getChromosomeName(), vcfEntry.getStart());
				currentDbEntry = dbNsfpFile.next();
			}
		}
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		fieldsToAdd = new HashMap<String, String>();
		fieldsType = new HashMap<String, String>();
		fieldsDescription = new HashMap<String, String>();
		annotateAll = false;
		collapseRepeatedValues = true;
	}

	/**
	 * Initialize annotation process
	 * @throws IOException
	 */
	public void initAnnotate() throws IOException {
		// Guess data types from table information
		GuessTableTypes guessTableTypes = new GuessTableTypes(dbNsfpFileName);
		guessTableTypes.guessTypes();
		if (!guessTableTypes.parsedHeader()) throw new RuntimeException("Could not parse header from file '" + dbNsfpFileName + "'");

		VcfInfoType types[] = guessTableTypes.getTypes();
		String fieldNames[] = guessTableTypes.getFieldNames();
		for (int i = 0; i < fieldNames.length; i++) {
			String type = (types[i] != null ? types[i].toString() : "String");
			fieldsType.put(fieldNames[i], type);
			fieldsDescription.put(fieldNames[i], "Field '" + fieldNames[i] + "' from dbNSFP");
		}

		// Open VCF file
		vcfFile = new VcfFileIterator(vcfFileName);

		// Check and open dbNsfp
		dbNsfpFile = new DbNsfpFileIterator(dbNsfpFileName);
		dbNsfpFile.setCollapseRepeatedValues(collapseRepeatedValues);
		if (tabixCheck && !dbNsfpFile.isTabix()) fatalError("Tabix index not found for database '" + dbNsfpFileName + "'.\n\t\tSnpSift dbNSFP only works with tabix indexed databases, please create or download index.");

		currentDbEntry = null;

		// No field names specified? Use default
		if (fieldsNamesToAdd == null) fieldsNamesToAdd = DEFAULT_FIELDS_NAMES_TO_ADD;
		for (String fn : fieldsNamesToAdd.split(",")) {
			if (fieldsDescription.get(fn) == null) usage("Error: Field name '" + fn + "' not found");
			fieldsToAdd.put(fn, fieldsDescription.get(fn));
		}
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equals("-a")) annotateAll = true;
			else if (arg.equals("-f")) fieldsNamesToAdd = args[++i]; // Filed to be used
			else if (arg.equalsIgnoreCase("-noCollapse")) {
				collapseRepeatedValues = false;
				annotateAll = true;
			} else if (dbNsfpFileName == null) dbNsfpFileName = arg;
			else if (vcfFileName == null) vcfFileName = arg;
		}

		// Sanity check
		if (dbNsfpFileName == null) usage("Missing dbNSFP file");
		if (vcfFileName == null) usage("Missing 'file.vcf'");
	}

	@Override
	public void run() {
		if (verbose) Timer.showStdErr("Annotating entries from: '" + dbNsfpFileName + "'");

		// Initialize annotations
		try {
			initAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Annotate VCF file
		boolean showHeader = true;
		for (VcfEntry vcfEntry : vcfFile) {
			try {
				// Show header?
				if (showHeader) {
					// Add VCF header
					addHeader(vcfFile);
					String headerStr = vcfFile.getVcfHeader().toString();
					if (!headerStr.isEmpty()) System.out.println(headerStr);
					showHeader = false;

					// Check that the fields we want to add are actually in the database
					checkFieldsToAdd();
				}

				// Annotate
				annotate(vcfEntry);

				// Show
				System.out.println(vcfEntry);
				count++;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		endAnnotate();

		// Show some stats
		if (verbose) {
			double perc = (100.0 * countAnnotated) / count;
			Timer.showStdErr("Done." //
					+ "\n\tTotal annotated entries : " + countAnnotated //
					+ "\n\tTotal entries           : " + count //
					+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
			);
		}
	}

	public void setTabixCheck(boolean tabixCheck) {
		this.tabixCheck = tabixCheck;
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

		StringBuilder sb = new StringBuilder();
		for (String f : DEFAULT_FIELDS_NAMES_TO_ADD.split(","))
			sb.append("\t                - " + f + "\n");

		// Show error
		showVersion();
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [-q|-v] [-a] dbNSFP.txt file.vcf > newFile.vcf\n" //
				+ "Options:\n" //
				+ "\t-a            : Annotate fields, even if the database has an empty value (annotates using '.' for empty).\n" //
				+ "\t-noCollapse   : Switch off 'collapsing' repeated values from dbNSFP (implies '-a').\n" //
				+ "\t-f            : A comma sepparated list of fields to add.\n" //
				+ "\t                Default fields to add:\n" + sb //
				+ "\n" //
		);
		System.exit(1);
	}
}
