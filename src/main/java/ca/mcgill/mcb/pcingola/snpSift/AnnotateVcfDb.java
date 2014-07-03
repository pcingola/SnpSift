package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfInfo;

/**
 * Annotate using a VCF "database"
 *
 * @author pcingola
 *
 */
public abstract class AnnotateVcfDb {

	public static final int SHOW = 10000;
	public static final int SHOW_LINES = 100 * SHOW;
	public static final int MAX_ERRORS = 10; // Report an error no more than X times

	protected boolean verbose, debug;
	protected boolean useId = true; // Annotate ID fields
	protected boolean useInfoField = true; // Use all info fields
	protected boolean useRefAlt = true;
	protected int countBadRef = 0;
	protected String chrPrev = "";
	protected String prependInfoFieldName;
	protected String dbFileName;
	protected String latestChromo = "";
	protected HashMap<String, String> dbCurrentId = new HashMap<String, String>();
	protected HashMap<String, String> dbCurrentInfo = new HashMap<String, String>();
	protected List<String> infoFields; // Use only info fields
	protected VcfEntry latestVcfDb = null;
	protected VcfFileIterator vcfDbFile;
	protected HashMap<String, Integer> errCount;

	protected AnnotateVcfDb(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	/**
	 * Add 'key->id' entries to 'dbCurrent'
	 */
	void addDbCurrent(VcfEntry vcfDb) {
		latestChromo = vcfDb.getChromosomeName();
		String alts[] = vcfDb.getAlts();
		for (int i = 0; i < alts.length; i++) {
			String key = key(vcfDb, i);
			dbCurrentId.put(key, vcfDb.getId()); // Add ID field

			// Add INFO fields to DB?
			if (useInfoField) {
				// Add all INFO fields
				String infoFieldsStr = dbInfoFields(vcfDb, alts[i]);
				dbCurrentInfo.put(key, infoFieldsStr);
			}
		}
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(VcfEntry ve) throws IOException {
		readDb(ve); // Read database up to this point
		return annotateIdAndInfo(ve); // Add annotations to VCF entry
	}

	/**
	 * Add annotation form database into VCF entry's INFO field
	 */
	public boolean annotateIdAndInfo(VcfEntry vcfEntry) {
		// Find information in database
		String id = useId ? findDbId(vcfEntry) : null;
		String infoStr = useInfoField ? findDbInfo(vcfEntry) : null;

		// Add information to vcfEntry
		boolean annotated = annotateIds(vcfEntry, id);
		annotated |= annotateInfo(vcfEntry, infoStr);

		return annotated;
	}

	/**
	 * Add ID information. Make sure we are no repeating IDs
	 */
	protected boolean annotateIds(VcfEntry vcfEntry, String id) {
		if (id == null) return false;

		// Add IDs, make sure we are no repeating them
		// Get unique IDs (the ones not already present in vcf.id)
		boolean annotated = false;
		id = uniqueIds(id, vcfEntry.getId());
		if (!id.isEmpty()) { // Skip if no new ids found
			annotated = true;

			// Add ID
			if (!vcfEntry.getId().isEmpty()) id = vcfEntry.getId() + ";" + id;
			vcfEntry.setId(id);
		}

		return annotated;
	}

	/**
	 * Add INFO fields.
	 */
	protected boolean annotateInfo(VcfEntry vcfEntry, String infoStr) {
		if (infoStr == null) return false;
		if (prependInfoFieldName != null) infoStr = prependInfoName(infoStr);
		vcfEntry.addInfo(infoStr);
		return true;
	}

	protected void clearCurrent() {
		dbCurrentId.clear();
		dbCurrentInfo.clear();
	}

	/**
	 * Finish up annotation process
	 */
	public void close() {
		if (vcfDbFile != null) {
			vcfDbFile.close(); // We have to close vcfDbFile because it was opened using a BufferedReader (this sets autoClose to 'false')
			vcfDbFile = null;
		}
	}

	/**
	 * Extract corresponding info fields as a single string
	 */
	protected String dbInfoFields(VcfEntry vcfDb, String allele) {
		// Add some INFO fields
		StringBuilder infoSb = new StringBuilder();

		// Which INFO fields should we read?
		List<String> infoToRead = infoFields;
		if (infoFields == null) {
			// Add all INFO fields in alphabetical order
			infoToRead = new ArrayList<String>();
			infoToRead.addAll(vcfDb.getInfoKeys());
			Collections.sort(infoToRead);
		}

		// Add each field
		for (String fieldName : infoToRead) {
			if (fieldName.isEmpty()) continue; // Empty INFO field may cause this

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
	 * Find an ID for this VCF entry
	 */
	protected String findDbId(VcfEntry vcf) {
		if (dbCurrentId.isEmpty()) return null;

		// Find for each ALT
		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			String id = dbCurrentId.get(key);
			if (id != null) return id;
		}
		return null;
	}

	/**
	 * Find INFO field for this VCF entry
	 */
	protected String findDbInfo(VcfEntry vcf) {
		if (dbCurrentInfo.isEmpty()) return null;

		// Find for each ALT
		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);

			// Get info field annotations
			String info = dbCurrentInfo.get(key);

			// Any annotations?
			if ((info != null) && (!info.isEmpty())) return info;
		}

		return null;
	}

	/**
	 * Create a hash key
	 */
	String key(VcfEntry vcfDbEntry, int altIndex) {
		if (useRefAlt) return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getAlts()[altIndex];
		return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart();
	}

	/**
	 * Open database annotation file
	 */
	public abstract void open() throws IOException;

	/**
	 * Prepend 'prependInfoFieldName' to all info fields
	 */
	protected String prependInfoName(String infoStr) {
		if (infoStr == null || infoStr.isEmpty()) return infoStr;

		StringBuilder sb = new StringBuilder();
		for (String f : infoStr.split(";"))
			sb.append((sb.length() > 0 ? ";" : "") + prependInfoFieldName + f);
		return sb.toString();

	}

	/**
	 * Read all DB entries up to 'vcfEntry'
	 */
	protected abstract void readDb(VcfEntry ve);

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setInfoFields(List<String> infoFields) {
		this.infoFields = infoFields;
	}

	public void setPrependInfoFieldName(String prependInfoFieldName) {
		this.prependInfoFieldName = prependInfoFieldName;
	}

	public void setUseId(boolean useId) {
		this.useId = useId;
	}

	public void setUseInfoField(boolean useInfoField) {
		this.useInfoField = useInfoField;
	}

	public void setUseRefAlt(boolean useRefAlt) {
		this.useRefAlt = useRefAlt;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * IDs from database not present in VCF
	 */
	protected String uniqueIds(String idStrDb, String idStrVcf) {
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
	 * Show a warning message (up to MAX_ERRORS times)
	 * @param warn
	 */
	protected void warn(String warn) {
		if (!errCount.containsKey(warn)) errCount.put(warn, 0);

		int count = errCount.get(warn);
		errCount.put(warn, count + 1);

		if (count < MAX_ERRORS) System.err.println("WARNING: " + warn);
	}

}
