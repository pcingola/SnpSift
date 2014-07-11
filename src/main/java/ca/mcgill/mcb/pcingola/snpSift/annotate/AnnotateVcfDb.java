package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
	protected boolean useRefAlt = true;
	protected int countBadRef = 0;
	protected String chrPrev = "";
	protected String prependInfoFieldName;
	protected String dbFileName;
	protected DbVcfEntry dbCurrentEntry;
	protected VcfEntry latestVcfDb = null;
	protected VcfFileIterator vcfDbFile;
	protected HashMap<String, Integer> errCount;

	public AnnotateVcfDb(String dbFileName) {
		this.dbFileName = dbFileName;
		dbCurrentEntry = new DbVcfEntry();
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(VcfEntry vcfEntry) throws IOException {
		readDb(vcfEntry); // Read database up to this point

		// Add information to vcfEntry
		boolean annotated = annotateIds(vcfEntry, dbCurrentEntry.findDbId(vcfEntry));
		annotated |= annotateInfo(vcfEntry, dbCurrentEntry.findDbInfo(vcfEntry));

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
	protected boolean annotateInfo(VcfEntry vcfEntry, Map<String, String> info) {
		if (info == null || info.isEmpty()) return false;

		// Sort keys alphabetically
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(info.keySet());
		Collections.sort(keys);

		// Add keys sorted alphabetically
		for (String key : keys) {
			String value = info.get(key);
			if (prependInfoFieldName != null) key = prependInfoName(key);
			vcfEntry.addInfo(key, value);
		}

		return true;
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
		dbCurrentEntry.setInfoFields(infoFields);
	}

	public void setPrependInfoFieldName(String prependInfoFieldName) {
		this.prependInfoFieldName = prependInfoFieldName;
	}

	public void setUseId(boolean useId) {
		dbCurrentEntry.setUseId(useId);
	}

	public void setUseInfoField(boolean useInfoField) {
		dbCurrentEntry.setUseInfoField(useInfoField);
	}

	public void setUseRefAlt(boolean useRefAlt) {
		dbCurrentEntry.setUseRefAlt(useRefAlt);
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
	 */
	protected void warn(String warn) {
		if (!errCount.containsKey(warn)) errCount.put(warn, 0);

		int count = errCount.get(warn);
		errCount.put(warn, count + 1);

		if (count < MAX_ERRORS) System.err.println("WARNING: " + warn);
	}

}
