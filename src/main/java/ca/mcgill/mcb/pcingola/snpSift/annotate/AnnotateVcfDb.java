package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate using a VCF "database"
 *
 * @author pcingola
 *
 */
public abstract class AnnotateVcfDb {

	public static final int MAX_ERRORS = 10; // Report an error no more than X times

	protected boolean verbose, debug;
	protected boolean useRefAlt = true;
	protected String chrPrev = "";
	protected String existsInfoField = null;
	protected String prependInfoFieldName;
	protected DbVcf dbVcf;
	protected VcfFileIterator vcfDbFile;
	protected HashMap<String, Integer> errCount;

	public AnnotateVcfDb() {
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(VcfEntry vcfEntry) throws IOException {
		dbVcf.readDb(vcfEntry); // Read database up to this point

		// Add information to vcfEntry
		boolean annotated = false;

		Set<String> idSet = new HashSet<>();
		Map<String, String> infos = new HashMap<>();

		// Annotate all info fields
		List<Variant> vars = vcfEntry.variants();
		for (Variant var : vars) {
			dbVcf.findDbId(var, idSet);
			dbVcf.findDbInfo(var, infos);
			//			if (existsInfoField != null && dbVcf.findDbExists(var)) annotateExists(var);
		}

		annotated |= annotateIds(vcfEntry, idSet);
		annotated |= annotateInfo(vcfEntry, infos);

		return annotated;
	}

	/**
	 * Add 'exists' flag to INFO fields
	 * @param vcfEntry
	 */
	protected void annotateExists(VcfEntry vcfEntry) {
		vcfEntry.addInfo(existsInfoField, null);
	}

	/**
	 * Add ID information. Make sure we are no repeating IDs
	 */
	protected boolean annotateIds(VcfEntry vcfEntry, Set<String> idSet) {
		if (idSet.isEmpty()) return false;

		// Add IDs, make sure we are no repeating them
		// Get unique IDs (the ones not already present in vcf.id)
		boolean annotated = false;
		String id = uniqueIds(idSet, vcfEntry.getId());
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

	public void close() {
		dbVcf.close();
	}

	public void open() {
		dbVcf.open();
	}

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

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setExistsInfoField(String existsInfoField) {
		this.existsInfoField = existsInfoField;
	}

	public void setInfoFields(boolean useInfoFields, Collection<String> infoFields) {
		dbVcf.setInfoFields(useInfoFields, infoFields);
	}

	public void setPrependInfoFieldName(String prependInfoFieldName) {
		this.prependInfoFieldName = prependInfoFieldName;
	}

	public void setUseId(boolean useId) {
		dbVcf.setUseId(useId);
	}

	public void setUseRefAlt(boolean useRefAlt) {
		dbVcf.setUseRefAlt(useRefAlt);
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * IDs from database not present in VCF
	 */
	protected String uniqueIds(Set<String> idSetDb, String idStrVcf) {
		// Remove currently annotated IDs
		String idsVcf[] = idStrVcf.split(";");
		for (String id : idsVcf)
			idSetDb.remove(id);

		// Add all remaining IDs
		StringBuilder sbId = new StringBuilder();
		for (String id : idSetDb)
			sbId.append((sbId.length() > 0 ? ";" : "") + id);

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
