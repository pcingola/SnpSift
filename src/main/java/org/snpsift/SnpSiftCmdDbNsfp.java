package org.snpsift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.fileIterator.DbNsfp;
import org.snpsift.fileIterator.DbNsfpEntry;

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

	public static final String DBNSFP_VCF_INFO_PREFIX = "dbNSFP_";

	public static final String DEFAULT_FIELDS_NAMES_TO_ADD = "" // Default fields to add
			// DbNSFP version 2
			+ "Uniprot_acc" //
			+ ",Interpro_domain" // Domain
			+ ",SIFT_pred" // SIFT predictions
			+ ",Polyphen2_HDIV_pred,Polyphen2_HVAR_pred" // Polyphen predictions
			+ ",LRT_pred" // LRT predictions
			+ ",MutationTaster_pred" // MutationTaser predictions
			+ ",GERP++_NR" // GERP
			+ ",GERP++_RS" //
			+ ",phastCons100way_vertebrate" // Conservation
			+ ",1000Gp1_AF,1000Gp1_AFR_AF,1000Gp1_EUR_AF,1000Gp1_AMR_AF,1000Gp1_ASN_AF" // Allele frequencies 1000 Genomes project
			+ ",ESP6500_AA_AF,ESP6500_EA_AF" // Allele frequencies Exome sequencing project
	// DbNSFP version 3 fields (some fields have different names than in version 2)
			+ ",MutationTaster_pred" //
			+ ",MutationAssessor_pred" //
			+ ",FATHMM_pred" //
			+ ",PROVEAN_pred" //
			+ ",CADD_phred" //
			+ ",MetaSVM_pred" //
			+ ",1000Gp3_AC,1000Gp3_AF,1000Gp3_AFR_AC,1000Gp3_AFR_AF,1000Gp3_EUR_AC,1000Gp3_EUR_AF,1000Gp3_AMR_AC,1000Gp3_AMR_AF,1000Gp3_EAS_AC,1000Gp3_EAS_AF,1000Gp3_SAS_AC,1000Gp3_SAS_AF" //
			+ ",ESP6500_AA_AC,ESP6500_AA_AF,ESP6500_EA_AC,ESP6500_EA_AF"//
			+ ",ExAC_AC,ExAC_AF,ExAC_Adj_AC,ExAC_Adj_AF,ExAC_AFR_AC,ExAC_AFR_AF,ExAC_AMR_AC,ExAC_AMR_AF,ExAC_EAS_AC,ExAC_EAS_AF,ExAC_FIN_AC,ExAC_FIN_AF,ExAC_NFE_AC,ExAC_NFE_AF,ExAC_SAS_AC,ExAC_SAS_AF" //
			;
	public static final int MIN_JUMP = 100;

	public static final int SHOW_EVERY = 100;
	public final String CONFIG_DBNSFP_DB_NAME = "dbnsfp";

	protected Map<String, String> fieldsToAdd;
	protected Map<String, String> fieldsDescription;
	protected Map<String, String> fieldsType;
	protected boolean annotateEmpty; // Annotate empty fields as well?
	protected boolean collapseRepeatedValues; // Collapse values if repeated?
	protected boolean inverseFieldSelection; // Inverse field selection
	protected boolean tabixCheck = true;
	protected String vcfFileName;
	protected int count = 0;
	protected int countAnnotated = 0;
	protected int countVariants = 0;
	protected DbNsfp dbNsfp;
	protected VcfFileIterator vcfFile;
	protected DbNsfpEntry currentDbEntry;
	protected String fieldsNamesToAdd;
	String latestChromo = "";

	public SnpSiftCmdDbNsfp() {
		this(null);
	}

	public SnpSiftCmdDbNsfp(String args[]) {
		super(args, "dbnsfp");
	}

	/**
	 * Add some lines to header before showing it
	 */
	@Override
	public boolean addHeaders(VcfFileIterator vcfFile) {
		super.addHeaders(vcfFile);
		for (String key : fieldsToAdd.keySet()) {
			// Get type
			String type = fieldsType.get(key);
			if (type == null) {
				System.err.println("WARNING: Cannot find type for field '" + key + "', using 'String'.");
				type = VcfInfoType.String.toString();
			}

			String infoKey = VcfEntry.vcfInfoKeySafe(DBNSFP_VCF_INFO_PREFIX + key);
			vcfFile.getVcfHeader().addLine("##INFO=<ID=" + infoKey + ",Number=A,Type=" + type + ",Description=\"" + fieldsToAdd.get(key) + "\">");
		}

		return false;
	}

	/**
	 * Annotate a VCF file using dbNSFP
	 */
	ArrayList<VcfEntry> annotate(boolean createList) {
		ArrayList<VcfEntry> list = (createList ? new ArrayList<VcfEntry>() : null);

		// Open VCF file
		vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		// Initialize annotations
		try {
			annotateInit(vcfFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Annotate VCF file
		if (verbose) Timer.showStdErr("Annotating file '" + vcfFileName + "'");
		boolean showHeader = true;
		int pos = -1;
		String chr = "";
		for (VcfEntry vcfEntry : vcfFile) {
			try {
				// Show header?
				if (showHeader) {
					// Add VCF header
					addHeaders(vcfFile);
					String headerStr = vcfFile.getVcfHeader().toString();
					if (!headerStr.isEmpty()) print(headerStr);
					showHeader = false;

					// Check that the fields we want to add are actually in the database
					checkFieldsToAdd();
				}

				// Check if file is sorted
				if (vcfEntry.getChromosomeName().equals(chr) && vcfEntry.getStart() < pos) {
					fatalError("Your VCF file should be sorted!" //
							+ "\n\tPrevious entry " + chr + ":" + pos//
							+ "\n\tCurrent entry  " + vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1)//
					);
				}

				// Annotate
				annotate(vcfEntry);

				// Show
				print(vcfEntry);
				if (list != null) list.add(vcfEntry);
				count++;

				// Update chr:pos
				chr = vcfEntry.getChromosomeName();
				pos = vcfEntry.getStart();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		annotateFinish();
		vcfFile.close();

		// Show some stats
		if (verbose) {
			double perc = (100.0 * countAnnotated) / count;
			Timer.showStdErr("Done." //
					+ "\n\tTotal annotated entries : " + countAnnotated //
					+ "\n\tTotal entries           : " + count //
					+ "\n\tPercent                 : " + String.format("%.2f%%", perc) //
			);
		}

		return list;
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(Variant variant, Map<String, String> info) {
		if (verbose) Gpr.showMark(++countVariants, SHOW_EVERY);

		// dbNSFP only has SNP information
		if (!variant.isSnp()) return false;

		// Find in database
		Collection<DbNsfpEntry> dbEntries = dbNsfp.query(variant);
		if (dbEntries == null || dbEntries.isEmpty()) return false;

		// Add all INFO fields that refer to this allele
		boolean annotated = false;
		for (String fieldKey : fieldsToAdd.keySet()) {
			// Are there any values to annotate?
			String infoValue = getVcfInfo(dbEntries, fieldKey);

			// Missing or empty?
			if (annotateEmpty) {
				if (infoValue.isEmpty()) infoValue = ".";
			} else if (isDbNsfpValueEmpty(infoValue)) {
				infoValue = null;
			}

			// Add annotations
			if (infoValue != null) {
				String oldInfo = info.get(fieldKey);
				if (oldInfo == null) oldInfo = "";

				info.put(fieldKey, oldInfo + (oldInfo.isEmpty() ? "" : ",") + infoValue);
				annotated = true;
			}
		}

		// Show progress
		if (annotated) {
			countAnnotated++;
			if (debug) Gpr.debug("Annotated: " + variant.toStr());
		}

		return annotated;
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		boolean annotated = false;
		Map<String, String> info = new HashMap<>();

		// Find annotations for each variant in this VcfEntry
		for (Variant var : vcfEntry.variants())
			annotated |= annotate(var, info);

		// Add annotations to VcfEntry
		if (annotated) {
			// Sort keys and add them to VcfEntry
			ArrayList<String> keys = new ArrayList<>();
			keys.addAll(info.keySet());
			Collections.sort(keys);

			// Add INFO fields
			for (String key : keys) {
				String infoKey = VcfEntry.vcfInfoKeySafe(DBNSFP_VCF_INFO_PREFIX + key);
				vcfEntry.addInfo(infoKey, info.get(key));
			}

		}

		return annotated;
	}

	@Override
	public boolean annotateFinish() {
		if (dbNsfp != null) dbNsfp.close();
		return true;
	}

	/**
	 * Initialize annotation process
	 */
	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		this.vcfFile = vcfFile;

		// Get database name from config file?
		// Note this can happen when invoked a VcfAnnotator (e.g. form ClinEff)
		if (dbFileName == null && config != null) {
			dbFileName = config.getDatabaseLocal(CONFIG_DBNSFP_DB_NAME);
		}

		// Check and open dbNsfp
		dbNsfp = new DbNsfp(dbFileName);
		dbNsfp.setDebug(debug);
		dbNsfp.setVerbose(verbose);
		dbNsfp.open();

		// Initialize fields to annotate
		annotateInitFields();

		return true;
	}

	/**
	 * Initialize fields to annotate
	 */
	void annotateInitFields() {
		// Get field names to add from config file (unless already configure by command line
		if (fieldsNamesToAdd == null) {
			String dbNsfpFields = config.getDbNsfpFields();
			if (dbNsfpFields != null && !dbNsfpFields.isEmpty()) {
				fieldsNamesToAdd = dbNsfpFields;
				if (verbose) Timer.showStdErr("Using fields from config file: '" + fieldsNamesToAdd + "'");
			}
		}

		//---
		// Fields to use
		//---
		VcfInfoType types[] = dbNsfp.getTypes();
		String fieldNames[] = dbNsfp.getFieldNamesSorted();
		if (verbose) Timer.showStdErr("Database fields:");
		for (int i = 0; i < fieldNames.length; i++) {
			String type = (types[i] != null ? types[i].toString() : "String");
			fieldsType.put(fieldNames[i], type);
			fieldsDescription.put(fieldNames[i], "Field '" + fieldNames[i] + "' from dbNSFP");
			if (verbose) System.err.println("\t'" + fieldNames[i] + "'");
		}

		currentDbEntry = null;

		if (inverseFieldSelection) {
			// Inverted selection: Start with ALL fields and then remove the ones selected

			// Add all fields
			Set<String> fields = new HashSet<String>();
			fields.addAll(fieldsDescription.keySet());

			// Remove selected fields
			if (fieldsNamesToAdd != null) {
				for (String fn : fieldsNamesToAdd.split(","))
					fields.remove(fn);
			}

			// Sort
			ArrayList<String> fieldsSort = new ArrayList<String>();
			fieldsSort.addAll(fields);
			Collections.sort(fieldsSort);

			// Fields to be added
			for (String fn : fieldsSort)
				fieldsToAdd.put(fn, fieldsDescription.get(fn));

		} else {
			// No fields specified? Use default list of fields to add
			if (fieldsNamesToAdd == null) fieldsNamesToAdd = DEFAULT_FIELDS_NAMES_TO_ADD;

			// Add them to the list
			for (String fn : fieldsNamesToAdd.split(",")) {
				if (fieldsDescription.get(fn) == null) {
					// Field not found
					if (fieldsNamesToAdd == DEFAULT_FIELDS_NAMES_TO_ADD) {
						// Was it one of the default fields? => Ignore
						if (verbose) Timer.showStdErr("Warning: Default field name '" + fn + "' not found, ignoring");
					} else usage("Error: Field name '" + fn + "' not found");
				} else fieldsToAdd.put(fn, fieldsDescription.get(fn)); // Add field
			}
		}

		// Show selected fields
		if (verbose) {
			ArrayList<String> fieldsSort = new ArrayList<String>();
			fieldsSort.addAll(fieldsToAdd.keySet());
			Collections.sort(fieldsSort);

			Timer.showStdErr("Fields to add:");
			for (String fn : fieldsSort)
				System.err.println("\t\t\t" + fn);
		}
	}

	/**
	 * Check that all fields to add are available
	 */
	public void checkFieldsToAdd() throws IOException {
		// Check that all fields have a descriptor (used in VCF header)
		if (verbose) {
			for (String filedName : dbNsfp.getFieldNames())
				if (fieldsDescription.get(filedName) == null) System.err.println("WARNING: Field (column) '" + filedName + "' does not have an approriate field descriptor.");
		}

		// Check that all "field to add" are in the database
		for (String fieldKey : fieldsToAdd.keySet())
			if (!dbNsfp.hasField(fieldKey)) fatalError("dbNsfp does not have field '" + fieldKey + "' (file '" + dbFileName + "')");
	}

	String collapseRepeated(String csvalues) {
		String values[] = csvalues.split(",");
		if (values.length <= 1) return csvalues;

		// Only append values once
		StringBuilder sb = new StringBuilder();
		HashSet<String> valuesAdded = new HashSet<String>();
		for (String val : values)
			if (valuesAdded.add(val)) {
				if (sb.length() > 0) sb.append(',');
				sb.append(val);
			}

		return sb.toString();
	}

	public Map<String, String> getFieldsType() {
		return fieldsType;

	}

	/**
	 * Get a comma separated list of values
	 */
	String getVcfInfo(Collection<DbNsfpEntry> dbEntries, String fieldKey) {
		StringBuilder sb = new StringBuilder();
		for (DbNsfpEntry de : dbEntries) {
			String csv = de.getVcfInfo(fieldKey);

			if (annotateEmpty && csv == null) csv = ".";

			if (csv != null) {
				if (sb.length() > 0) sb.append(',');
				sb.append(csv);
			}
		}

		return collapseRepeatedValues ? collapseRepeated(sb.toString()) : sb.toString();
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		needsConfig = true;
		needsDb = true;
		dbTabix = true;
		dbType = "dbnsfp";

		fieldsToAdd = new HashMap<String, String>();
		fieldsType = new HashMap<String, String>();
		fieldsDescription = new HashMap<String, String>();
		annotateEmpty = false;
		collapseRepeatedValues = false;
	}

	/**
	 * Are all values empty?
	 */
	boolean isDbNsfpValueEmpty(String values) {
		// Single value check
		if (values == null || values.isEmpty() || values.equals(".")) return true;

		// Multiple values? Are all of them empty?
		for (String val : values.split(","))
			if (!val.isEmpty() && !val.equals(".")) return false;

		return true;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			switch (arg.toLowerCase()) {
			case "-a":
				annotateEmpty = true;
				break;

			case "-f":
				fieldsNamesToAdd = args[++i];
				break;

			case "-nocollapse":
				collapseRepeatedValues = false;
				break;

			case "-collapse":
				collapseRepeatedValues = true;
				break;

			case "-n":
				inverseFieldSelection = true;
				break;

			default:
				if (vcfFileName == null) vcfFileName = arg;
				else usage("Unknown extra parameter '" + arg + "'");
			}
		}

		// Sanity check
		if (vcfFileName == null) usage("Missing 'file.vcf'");
	}

	@Override
	public void run() {
		run(false);
	}

	/**
	 * Run annotation algorithm
	 */
	public List<VcfEntry> run(boolean createList) {
		// Read config
		if (config == null) loadConfig();

		// Find or download database
		dbFileName = databaseFind();

		if (verbose) Timer.showStdErr("Annotating\n" //
				+ "\tInput file    : '" + vcfFileName + "'\n" //
				+ "\tDatabase file : '" + dbFileName + "'" //
		);

		return annotate(createList);
	}

	public void setTabixCheck(boolean tabixCheck) {
		this.tabixCheck = tabixCheck;
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

		StringBuilder sb = new StringBuilder();
		for (String f : DEFAULT_FIELDS_NAMES_TO_ADD.split(","))
			sb.append("\t                - " + f + "\n");

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [options] file.vcf > newFile.vcf\n" //
				+ "Options:\n" //
				+ "\t-a            : Annotate fields, even if the database has an empty value (annotates using '.' for empty).\n" //
				+ "\t-collapse     : Collapse repeated values from dbNSFP. Default: " + collapseRepeatedValues + "\n" //
				+ "\t-noCollapse   : Switch off 'collapsing' repeated values from dbNSFP. Default: " + !collapseRepeatedValues + "\n" //
				+ "\t-n            : Invert 'fields to add' selection (i.e. use all fields except the ones specified in option '-f').\n" //
				+ "\t-f            : A comma separated list of fields to add.\n" //
				+ "\t                Default fields to add:\n" + sb //
		);

		usageGenericAndDb();

		System.err.println("Note: Databse (dbNSFP.txt.gz) must be bgzip and tabix indexed file.\n      The corresponding index file (dbNSFP.txt.gz.tbi) must be present.\n");

		System.exit(1);
	}
}
