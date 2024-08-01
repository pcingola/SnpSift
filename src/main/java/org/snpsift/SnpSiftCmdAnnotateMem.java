package org.snpsift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderEntry;
import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfInfoType;
import org.snpsift.annotate.AnnotateVcfDb;
import org.snpsift.annotate.AnnotateVcfDbMem;
import org.snpsift.annotate.AnnotateVcfDbSorted;
import org.snpsift.annotate.AnnotateVcfDbTabix;
import org.snpsift.annotate.VcfIndexTree;

/**
 * Annotate a VCF file from another VCF file (database)
 * The database file is loaded into memory.
 *
 * @author pcingola
 */
public class SnpSiftCmdAnnotateMem extends SnpSift {


	public SnpSiftCmdAnnotateMem() {
		super();
	}

	public SnpSiftCmdAnnotateMem(String args[]) {
		super(args);
	}

	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		throw new RuntimeException("Unimplemented method!");
	}

	/**
	 * Initialize database for annotation process
	 */
	@Override
	public boolean annotateInit(VcfFileIterator vcfFile) {
		throw new RuntimeException("Unimplemented method!");
	}

	/**
	 * Build headers to add
	 */
	@Override
	protected List<VcfHeaderEntry> headers() {
		List<VcfHeaderEntry> headerInfos = super.headers();

		// // Read database header and add INFO fields to the output vcf header
		// if (useInfoField) {
		// 	// Read VCF header
		// 	VcfFileIterator vcfDb = new VcfFileIterator(dbFileName);
		// 	VcfHeader vcfDbHeader = vcfDb.readHeader();

		// 	// Add all corresponding INFO headers
		// 	for (VcfHeaderInfo vcfHeaderDb : vcfDbHeader.getVcfHeaderInfo()) {
		// 		String id = (prependInfoFieldName != null ? prependInfoFieldName : "") + vcfHeaderDb.getId();

		// 		// Get same vcfInfo from file to annotate
		// 		VcfHeaderInfo vcfHeaderFile = vcfFile.getVcfHeader().getVcfHeaderInfo(id);

		// 		// Add header entry only if...
		// 		if (isAnnotateInfo(vcfHeaderDb) // It is used for annotations
		// 				&& !vcfHeaderDb.isImplicit() //  AND it is not an "implicit" header in Db (i.e. created automatically by VcfHeader class)
		// 				&& ((vcfHeaderFile == null) || vcfHeaderFile.isImplicit()) // AND it is not already added OR is already added, but it is implicit
		// 		) {
		// 			VcfHeaderInfo newHeader = new VcfHeaderInfo(vcfHeaderDb);
		// 			if (prependInfoFieldName != null) newHeader.setId(id); // Change ID?
		// 			headerInfos.add(newHeader);
		// 		}
		// 	}
		// }

		// // Using 'exists' flag?
		// if (existsInfoField != null) {
		// 	VcfHeaderInfo existsHeader = new VcfHeaderInfo(existsInfoField, VcfInfoType.Flag, "" + 1, "Variant exists in file '" + Gpr.baseName(dbFileName) + "'");
		// 	headerInfos.add(existsHeader);
		// }

		return headerInfos;
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parseArgs(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Command line option?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-a":
					break;


				default:
					usage("Unknown command line option '" + arg + "'");
				}
			} else {
				if (dbType == null && dbFileName == null) dbFileName = arg;
				else if (vcfInputFile == null) vcfInputFile = arg;
				else usage("Unknown extra parameter '" + arg + "'");
			}
		}

		// Sanity check
		if (dbType == null && dbFileName == null)

			usage("Missing database option or file: [-dbSnp | -clinVar | database.vcf ]");
	}

	/**
	 * Annotate each entry of a VCF file
	 */
	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run annotations
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 */
	public List<VcfEntry> run(boolean createList) {
		// Read config
		if (config == null) loadConfig();

		// Annotate
		return annotate(createList);
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

		showVersion();

		System.err.println("Usage:");
		System.err.println("\tCreate databases:");
		System.err.println("\t           java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + "\\");
		System.err.println("\t             -create \\");
		System.err.println("\t             -db database_1.vcf -fields field_1,field_2,...,field_N \\");
		System.err.println("\t             -db database_2.vcf -fields field_1,field_2,...,field_N \\");
		System.err.println("\t             -db database_N.vcf -fields field_1,field_2,...,field_N");
		System.err.println("\tAnnotate:");
		System.err.println("\t           java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + "\\");
		System.err.println("\t             -db database_1.vcf \\");
		System.err.println("\t             -db database_2.vcf \\");
		System.err.println("\t             -db database_N.vcf \\");
		System.err.println("\t             input.vcf > output.vcf \\");
		System.err.println("\nCommand Options:");
		System.err.println("\t-create              : Create a database from the VCF file.");
		System.err.println("\t-db file.vcf                  : Use VCF file (either to create a database or to annotate).");
		System.err.println("\t-fields field_1,..,field_N    : Use VCF info fields when creating the database. Comma separated list, no spaces. Only for create command.");

		usageGenericAndDb();

		System.err.println("Note: According the the VCF's database format provided, SnpSift annotate uses different strategies");
		System.err.println("\t  i) plain VCF       : SnpSift indexes the VCF file (creating an index file *.sidx).");
		System.err.println("\t ii) bgzip+tabix     : SnpSift uses tabix's index.");

		System.exit(1);
	}

}
