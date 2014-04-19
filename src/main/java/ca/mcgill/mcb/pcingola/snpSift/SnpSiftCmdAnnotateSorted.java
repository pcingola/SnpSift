package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeader;
import ca.mcgill.mcb.pcingola.vcf.VcfInfo;

/**
 * Annotate a VCF file with ID from another VCF file (database)

 * Note: Assumes both the VCF file AND the database file are sorted. 
 *       Each VCF entry should be sorted according to position. 
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2). 
 *       But entries for the same chromosome should be together.   
 * 
 * @author pcingola
 *
 */
public class SnpSiftCmdAnnotateSorted extends SnpSift {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;

	protected boolean useId; // Annotate ID fields
	protected boolean useInfoField; // Use all info fields
	protected boolean useRefAlt;
	protected int countBadRef = 0;
	protected String vcfDbFileName;
	protected String vcfFileName;
	protected String chrPrev = "";
	protected String prependInfoFieldName;
	protected HashMap<String, String> dbId = new HashMap<String, String>();
	protected HashMap<String, String> dbInfo = new HashMap<String, String>();
	protected FileIndexChrPos indexDb;
	protected ArrayList<String> infoFields; // Use only info fields
	protected VcfEntry latestVcfDb = null;
	protected VcfFileIterator vcfFile, vcfDbFile;

	public SnpSiftCmdAnnotateSorted(String args[]) {
		super(args, "annotate");
	}

	protected SnpSiftCmdAnnotateSorted(String args[], String command) {
		super(args, command);
	}

	/**
	 * Add annotation form database into VCF entry's INFO field
	 * @param vcf
	 */
	protected boolean addAnnotation(VcfEntry vcf) {
		boolean annotated = false;

		// Is this entry in db?
		String id = useId ? findDbId(vcf) : null;
		String infoStr = useInfoField ? findDbInfo(vcf) : null;

		if (id != null) {
			// Get unique ids (the ones not already present in vcf.id)
			id = uniqueIds(id, vcf.getId());
			if (!id.isEmpty()) { // Skip if no new ids found
				annotated = true;

				// Add ID
				if (!vcf.getId().isEmpty()) id = vcf.getId() + ";" + id;
				vcf.setId(id);
			}
		}

		// Add INFO fields (if any)
		if (infoStr != null) {
			if (prependInfoFieldName != null) infoStr = prependInfoName(infoStr);
			vcf.addInfo(infoStr);
			annotated = true;
		}

		return annotated;
	}

	/**
	 * Add 'key->id' entries to 'db'
	 * @param vcfDb
	 */
	void addDb(VcfEntry vcfDb) {
		String alts[] = vcfDb.getAlts();
		for (int i = 0; i < alts.length; i++) {
			String key = key(vcfDb, i);
			dbId.put(key, vcfDb.getId()); // Add ID field

			// Add INFO fields to DB?
			if (useInfoField) {
				// Add all INFO fields
				String infoFieldsStr = dbInfoFields(vcfDb, alts[i]);
				dbInfo.put(key, infoFieldsStr);
			}
		}
	}

	/**
	 * Build headers to add
	 */
	@Override
	protected List<String> addHeader() {
		List<String> newHeaders = super.addHeader();

		// Read database header and add INFO fields to the output vcf header
		if (useInfoField) {
			VcfFileIterator vcfDb = new VcfFileIterator(vcfDbFileName);
			VcfHeader vcfDbHeader = vcfDb.readHeader();

			// Add all corresponding INFO headers
			for (VcfInfo vcfInfoDb : vcfDbHeader.getVcfInfo()) {

				// Get same vcfInfo from file to annotate
				VcfInfo vcfInfoFile = vcfFile.getVcfHeader().getVcfInfo(vcfInfoDb.getId());

				// Add header entry only if... 
				if (isAnnotateInfo(vcfInfoDb) // Add if it is being used to annotate 
						&& !vcfInfoDb.isImplicit() //  AND it is not an "implicit" header in Db (i.e. created automatically by VcfHeader class)
						&& ((vcfInfoFile == null) || vcfInfoFile.isImplicit()) // AND it is not already added OR is already added, but it is implicit
				) newHeaders.add(vcfInfoDb.toString());
			}
		}

		return newHeaders;
	}

	/**
	 * Annotate a VCF entry
	 * @param vcf
	 * @throws IOException
	 */
	public boolean annotate(VcfEntry vcf) throws IOException {

		// Do we have to seek in db file?
		String chr = vcf.getChromosomeName();
		if (!chr.equals(chrPrev)) {
			// Get to the beginning of the new chromosome
			long start = indexDb.getStart(chr);

			// No such chromosome?
			if (start < 0) {
				warn("Chromosome '" + chr + "' not found in database.");
				return false;
			}

			// Seek 
			vcfDbFile.seek(start);
			latestVcfDb = null;
			if (verbose) Timer.showStdErr("Chromosome: '" + chr + "'");
		}
		chrPrev = chr;

		// Read database up to this point
		readDb(vcf);

		// Create annotation
		return addAnnotation(vcf);
	}

	protected void clear() {
		dbId.clear();
		if (useInfoField) dbInfo.clear();
	}

	/**
	 * Extract corresponding info fields as a single string
	 * @param vcfDb
	 * @return
	 */
	String dbInfoFields(VcfEntry vcfDb, String allele) {
		// Add some INFO fields
		StringBuilder infoSb = new StringBuilder();

		// Whih INFO fields should we read?
		List<String> infoToRead = infoFields;
		if (infoFields == null) {
			// Add all INFO fields in alphabetical order
			infoToRead = new ArrayList<String>();
			infoToRead.addAll(vcfDb.getInfoKeys());
			Collections.sort(infoToRead);
		}

		// Add each field
		for (String fieldName : infoToRead) {
			VcfInfo vcfInfo = vcfDb.getVcfInfo(fieldName);

			String val = null;
			if (vcfInfo != null && (vcfInfo.isNumberOnePerAllele() || vcfInfo.isNumberAllAlleles())) val = vcfDb.getInfo(fieldName, allele);

			else val = vcfDb.getInfo(fieldName);

			// Any value? => Add
			if (val != null) {
				if (infoSb.length() > 0) infoSb.append(";");
				infoSb.append(fieldName + "=" + val);
			}
		}

		return infoSb.toString();
	}

	/**
	 * Finish up annotation process
	 */
	public void endAnnotate() {
		vcfDbFile.close(); // We have to close vcfDbFile because it was opened using a BufferedReader (this sets autoClose to 'false')
	}

	/**
	 * Find an ID for this VCF entry
	 * @param vcf
	 * @return
	 */
	protected String findDbId(VcfEntry vcf) {
		if (dbId.isEmpty()) return null;

		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			String id = dbId.get(key);
			if (id != null) return id;
		}
		return null;
	}

	/**
	 * Find INFO field for this VCF entry
	 * @param vcf
	 * @return
	 */
	protected String findDbInfo(VcfEntry vcf) {
		if (dbInfo.isEmpty()) return null;

		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);

			// Get info field annotations
			String info = dbInfo.get(key);

			// Any annotations?
			if ((info != null) && (!info.isEmpty())) return info;
		}

		return null;
	}

	/**
	 * Index a VCF file
	 * @param fileName
	 * @return
	 */
	FileIndexChrPos index(String fileName) {
		if (verbose) System.err.println("Index: " + fileName);
		FileIndexChrPos fileIndex = new FileIndexChrPos(fileName);
		fileIndex.setVerbose(verbose);
		fileIndex.setDebug(debug);
		fileIndex.open();
		fileIndex.index();
		fileIndex.close();
		return fileIndex;
	}

	/**
	 * Initialize default values
	 */
	@Override
	public void init() {
		useInfoField = true; // Default: Use INFO fields
		useId = true; // Annotate ID fields
		useRefAlt = true; // Use REF and ALT fields when comparing
	}

	/**
	 * Initialize annotation process
	 * @throws IOException
	 */
	public void initAnnotate() throws IOException {
		initInputVcf();

		// Open and index database
		indexDb = index(vcfDbFileName);

		// Re-open VCF db file
		vcfDbFile = new VcfFileIterator(new SeekableBufferedReader(vcfDbFileName));
		latestVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
	}

	/**
	 * Initialize annotation process
	 * @throws IOException
	 */
	protected void initInputVcf() throws IOException {
		// Index and open VCF files
		vcfFile = new VcfFileIterator(vcfFileName);
	}

	/**
	 * Are we annotating using this info field?
	 * @param vcfInfo
	 * @return
	 */
	boolean isAnnotateInfo(VcfInfo vcfInfo) {
		// All fields seleceted?
		if (infoFields == null) return true;

		// Check if specified field is present
		for (String info : infoFields)
			if (vcfInfo.getId().equals(info)) return true;

		return false;
	}

	/**
	 * Create a hash key
	 * @param vcfDbEntry
	 * @param altIndex
	 * @return
	 */
	String key(VcfEntry vcfDbEntry, int altIndex) {
		if (useRefAlt) return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getAlts()[altIndex];
		return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart();
	}

	/**
	 * Parse command line arguments
	 */
	@Override
	public void parse(String[] args) {
		if (args.length == 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Command line option?
			if (isOpt(arg)) {
				if (arg.equals("-id")) useInfoField = false;
				else if (arg.equals("-info")) {
					useInfoField = true;
					infoFields = new ArrayList<String>();
					for (String infoField : args[++i].split(","))
						infoFields.add(infoField);
				} else if (arg.equals("-noId")) useId = false;
				else if (arg.equals("-name")) prependInfoFieldName = args[++i];
				else if (arg.equals("-noAlt")) useRefAlt = false;
				else usage("Unknown command line option '" + arg + "'");
			} else if (vcfDbFileName == null) vcfDbFileName = arg;
			else if (vcfFileName == null) vcfFileName = arg;
		}

		// Sanity check
		if (vcfDbFileName == null) usage("Missing 'database.vcf'");
		if (vcfFileName == null) usage("Missing 'file.vcf'");
	}

	/**
	 * Prepend 'prependInfoFieldName' to all info fields
	 * @param infoStr
	 * @return
	 */
	String prependInfoName(String infoStr) {
		if (infoStr == null || infoStr.isEmpty()) return infoStr;

		StringBuilder sb = new StringBuilder();
		for (String f : infoStr.split(";"))
			sb.append((sb.length() > 0 ? ";" : "") + prependInfoFieldName + f);
		return sb.toString();

	}

	/**
	 * Read all db entries up to 'vcf'
	 * @param vcf
	 */
	void readDb(VcfEntry vcf) {
		String chr = vcf.getChromosomeName();

		// Add latest to db?
		if (latestVcfDb != null) {
			if (latestVcfDb.getChromosomeName().equals(chr)) {
				if (vcf.getStart() < latestVcfDb.getStart()) {
					clear();
					return;
				}

				if (vcf.getStart() == latestVcfDb.getStart()) addDb(latestVcfDb);
			}
		} else clear();

		// Read more entries from db
		for (VcfEntry vcfDb : vcfDbFile) {
			latestVcfDb = vcfDb;

			String chrDb = vcfDb.getChromosomeName();
			if (!chrDb.equals(chr)) return;

			if (vcf.getStart() < vcfDb.getStart()) return;
			if (vcf.getStart() == vcfDb.getStart()) {
				// Sanity check: Check that references match
				if (!vcf.getRef().equals(vcfDb.getRef()) //
						&& !vcf.getRef().startsWith(vcfDb.getRef()) //  
						&& !vcfDb.getRef().startsWith(vcf.getRef()) //
				) {
					System.err.println("WARNING: Reference in database file '" + vcfDbFileName + "' is '" + vcfDb.getRef() + "' and reference in input file '" + vcfFileName + "' is '" + vcf.getRef() + "' at " + chr + ":" + (vcf.getStart() + 1));
					countBadRef++;
				}

				addDb(vcfDb); // Same position: Add all keys to 'db'. Note: VCF allows more than one line with the same position
			}
		}
	}

	/**
	 * Annotate each entry of a VCF file
	 * @throws IOException
	 */
	@Override
	public void run() {
		run(false);
	}

	/**
	 * Run annotations
	 * @param createList : If true, return a list with all annotated entries (used for test cases & debugging)
	 * @return
	 */
	public List<VcfEntry> run(boolean createList) {
		ArrayList<VcfEntry> list = (createList ? new ArrayList<VcfEntry>() : null);
		if (verbose) Timer.showStdErr("Annotating entries from: '" + vcfFile + "'");

		try {
			initAnnotate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		int countAnnotated = 0, count = 0;
		boolean showHeader = true;
		int pos = -1;
		String chr = "";

		for (VcfEntry vcfEntry : vcfFile) {
			try {
				// Show header?
				if (showHeader) {
					showHeader = false;
					addHeader(vcfFile);
					String headerStr = vcfFile.getVcfHeader().toString();
					if (!headerStr.isEmpty()) print(headerStr);
				}

				// Check if file is sorted
				if (vcfEntry.getChromosomeName().equals(chr) && vcfEntry.getStart() < pos) {
					fatalError("Your VCF file should be sorted!" //
							+ "\n\tPrevious entry " + chr + ":" + pos//
							+ "\n\tCurrent entry  " + vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1)//
					);
				}

				// Annotate
				boolean annotated = annotate(vcfEntry);

				// Show
				print(vcfEntry);
				if (list != null) list.add(vcfEntry);

				if (annotated) countAnnotated++;
				count++;

				// Update chr:pos
				chr = vcfEntry.getChromosomeName();
				pos = vcfEntry.getStart();

			} catch (Exception e) {
				e.printStackTrace();
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
					+ "\n\tErrors (bad references) : " + countBadRef //
			);
		}

		return list;
	}

	public void setSuppressOutput(boolean suppressOutput) {
		this.suppressOutput = suppressOutput;
	}

	/** 
	 * IDs from database not present in VCF
	 * @param idStrDb
	 * @param idStrVcf
	 * @return
	 */
	String uniqueIds(String idStrDb, String idStrVcf) {
		StringBuilder sbId = new StringBuilder();
		String idsDb[] = idStrDb.split(";");
		String idsVcf[] = idStrVcf.split(";");

		for (String idDb : idsDb) {
			// Add only if there is no other matching ID in VCF 
			boolean skip = false;
			for (String idVcf : idsVcf)
				skip |= idDb.equals(idVcf);

			// Append ID?
			if (!skip) sbId.append((sbId.length() > 0 ? ";" : "") + idDb);
		}

		return sbId.toString();
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar " + command + " [options] database.vcf file.vcf > newFile.vcf");
		System.err.println("Options:");
		System.err.println("\t-id          : Only annotate ID field (do not add INFO field). Default: " + !useInfoField);
		System.err.println("\t-noAlt       : Do not use REF and ALT fields when comparing database.vcf entries to file.vcf entries. Default: " + !useRefAlt);
		System.err.println("\t-noId        : Do not annotate ID field. Defaul: " + !useId);
		System.err.println("\t-info <list> : Annotate using a list of info fields (list is a comma separated list of fields). Default: ALL.");
		System.err.println("\t-name str    : Prepend 'str' to all annotated INFO fields. Default: ''.");

		System.exit(1);
	}
}
