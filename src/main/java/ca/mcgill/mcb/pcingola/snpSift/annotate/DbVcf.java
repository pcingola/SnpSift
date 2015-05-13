package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;

/**
 * Use a VCF file as a database for annotations
 *
 * @author pcingola
 */
public abstract class DbVcf {

	protected boolean debug = false;
	protected boolean checkRepeat = true;
	protected boolean useId = true; // Annotate ID fields
	protected boolean useInfoField = true; // Use all info fields
	protected boolean useRefAlt = true;
	protected boolean verbose = false;
	protected int countBadRef = 0;
	protected String dbFileName;
	protected String latestChromo = "";
	protected VcfFileIterator vcfDbFile; // VCF File
	protected VcfEntry latestVcfDb = null; // Latest entry read form VCF (the information may have not been added/used yet)
	protected Map<String, Map<String, String>> dbCurrentInfo = new HashMap<String, Map<String, String>>();
	protected List<String> infoFields; // Use only these INFO fields
	protected Map<String, String> dbCurrentId = new HashMap<String, String>(); // Current DB entries
	protected Map<String, Boolean> vcfInfoPerAllele = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis?
	protected Map<String, Boolean> vcfInfoPerAlleleRef = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis AND requires reference to be annotated (i.e. VCF header has Number=R)?
	protected Set<String> dbVcfEntryAdded = new HashSet<String>(); // Entries that have already been added to the database

	public DbVcf(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	/**
	 * Add 'key->id' entries to 'dbCurrent'
	 */
	protected void add(VcfEntry vcfDb) {
		// VcfEntry already added?
		if (checkRepeat) {
			String keyAdded = key(vcfDb);
			if (dbVcfEntryAdded.contains(keyAdded)) return;
			dbVcfEntryAdded.add(keyAdded);
		}

		//---
		// Add information for each ALT allele
		//---
		updateChromo(vcfDb);
		String alts[] = vcfDb.getAlts();
		for (int alleleIdx = 0; alleleIdx < alts.length; alleleIdx++) {
			String key = key(vcfDb, alleleIdx);

			// Add ID field information
			addId(key, vcfDb.getId());

			// Add INFO fields to DB?
			if (useInfoField) {
				// Add all INFO fields
				Map<String, String> info = dbInfoFields(vcfDb, alts[alleleIdx]);
				addInfo(key, info);
			}
		}

		//---
		// Add information for each REF allele
		// (e.g. when INFO field has 'Number=R')
		//---
		if (useInfoField) {
			// Add all INFO fields
			int alleleIdx = -1; // Negative allele number means 'REF'
			String key = key(vcfDb, alleleIdx);
			Map<String, String> info = dbInfoFields(vcfDb, vcfDb.getRef());
			addInfo(key, info);
		}

	}

	/**
	 * Add VCF ID field
	 */
	protected void addId(String key, String id) {
		if (!dbCurrentId.containsKey(key)) dbCurrentId.put(key, id);
		else dbCurrentId.put(key, dbCurrentId.get(key) + "," + id);
	}

	/**
	 * Add VCF ID field
	 */
	protected void addInfo(String key, Map<String, String> info) {
		if (!dbCurrentInfo.containsKey(key)) dbCurrentInfo.put(key, info);
		else {
			// There is information => We need to append to each entry
			Map<String, String> infoOri = dbCurrentInfo.get(key);

			for (Entry<String, String> entry : info.entrySet()) {
				String k = entry.getKey();
				String v = entry.getValue();

				if (!infoOri.containsKey(k)) infoOri.put(k, v); // This entry is NOT in infoOri, simply add it
				else infoOri.put(k, infoOri.get(k) + "," + v); // This entry IS in infoOri, append value
			}
		}
	}

	/**
	 * Check if we are still in the latest chromosome
	 */
	protected boolean checkChromo(VcfEntry vcfEntry) {
		return latestChromo.equals(vcfEntry.getChromosomeName());
	}

	/**
	 * Clear cached db entries
	 */
	protected void clear() {
		dbCurrentId.clear();
		dbCurrentInfo.clear();
		dbVcfEntryAdded.clear();
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
	 * Extract corresponding info fields
	 */
	protected Map<String, String> dbInfoFields(VcfEntry vcfDb, String allele) {
		// Add some INFO fields
		Map<String, String> info = new HashMap<>();

		// Which INFO fields should we read?
		List<String> infoToRead = infoFields;
		if (infoFields == null) {
			// Add all INFO fields in alphabetical order
			infoToRead = new ArrayList<String>();
			infoToRead.addAll(vcfDb.getInfoKeys());
		}

		// Add each field
		for (String fieldName : infoToRead) {
			if (fieldName.isEmpty()) continue; // Empty INFO field may cause this

			// Make sure we get allele specific information (if INFO is allele specific)
			String val = null;

			// Check if fields are Number='A' or Number='R' (this also caches results for future reference)
			boolean perAlleleRef = isVcfInfoPerAlleleRef(fieldName, vcfDb);
			boolean perAllele = isVcfInfoPerAllele(fieldName, vcfDb);

			if (perAlleleRef) {
				val = vcfDb.getInfo(fieldName, allele);
			} else if (perAllele) {
				val = vcfDb.getInfo(fieldName, allele);
			} else {
				val = vcfDb.getInfo(fieldName);
			}

			// Any value? => Add
			if (val != null) info.put(fieldName, val);
		}

		return info;
	}

	/**
	 * Find if a VCF entry exists in the database
	 */
	protected boolean findDbExists(VcfEntry vcf) {
		if (dbCurrentId.isEmpty()) return false;

		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			if (dbCurrentId.containsKey(key)) return true;
		}

		return false;
	}

	/**
	 * Find an ID for this VCF entry
	 */
	protected String findDbId(VcfEntry vcf) {
		if (!useId || dbCurrentId.isEmpty()) return null;

		// Find for each ALT
		StringBuilder ids = null;
		HashSet<String> idSet = null;

		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			String id = dbCurrentId.get(key);
			if (id != null) {
				if (ids == null) ids = new StringBuilder(id);
				else {
					if (idSet == null) {
						// Initialize idSet and add previous value
						idSet = new HashSet<String>();
						idSet.add(ids.toString());
					}

					// Do not add repeated IDs
					if (!idSet.contains(id)) ids.append(";" + id);
				}
			}
		}

		return ids == null ? null : ids.toString();
	}

	/**
	 * Find INFO field for this VCF entry
	 */
	protected HashMap<String, String> findDbInfo(VcfEntry vcf) {
		if (!useInfoField || dbCurrentInfo.isEmpty()) return null;

		//---
		// Which INFO fields can we annotate?
		//---
		Set<String> infoFieldsToAdd = new HashSet<String>();
		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			Map<String, String> info = dbCurrentInfo.get(key);
			if (info != null) {
				for (String infoFieldName : info.keySet())
					infoFieldsToAdd.add(infoFieldName);
			}
		}

		//---
		// Prepare INFO field to add
		//---
		HashMap<String, String> results = new HashMap<>();
		for (String infoFieldName : infoFieldsToAdd) {

			// Allele number '-1' is reference. It's used when the INFO field has 'Number=R'
			int minAlleleNum = isVcfInfoPerAlleleRef(infoFieldName) ? -1 : 0;

			for (int i = minAlleleNum; i < vcf.getAlts().length; i++) {
				// Get info field annotations
				String key = key(vcf, i);
				Map<String, String> info = dbCurrentInfo.get(key);

				// Add each INFO
				String infoVal = info == null ? null : info.get(infoFieldName);
				String val = results.get(infoFieldName);
				val = (val == null ? "" : val + ",") + (infoVal == null ? "." : infoVal);
				results.put(infoFieldName, val);

				// Not a 'per allele' INFO field? Then we are done (no need to annotate other alleles)
				if (!isVcfInfoPerAllele(infoFieldName)) break;
			}
		}

		return results;
	}

	/**
	 * Is 'fieldName' a per-allele annotation
	 */
	boolean isVcfInfoPerAllele(String fieldName) {
		return isVcfInfoPerAllele(fieldName, null);
	}

	/**
	 * Is 'fieldName' a per-allele annotation
	 */
	boolean isVcfInfoPerAllele(String fieldName, VcfEntry vcfDb) {
		// Look up information and cache it
		if (vcfInfoPerAllele.get(fieldName) == null) {
			if (vcfDb == null) return false; // No VCF information? I cannot look it up...nothing to do

			VcfHeaderInfo vcfInfo = vcfDb.getVcfInfo(fieldName);
			boolean isPerAllele = vcfInfo != null && (vcfInfo.isNumberOnePerAllele() || vcfInfo.isNumberAllAlleles());
			vcfInfoPerAllele.put(fieldName, isPerAllele);
		}

		return vcfInfoPerAllele.get(fieldName);
	}

	/**
	 * Is this a "per-allele + REF" INFO field?
	 */
	boolean isVcfInfoPerAlleleRef(String fieldName) {
		return isVcfInfoPerAlleleRef(fieldName, null);
	}

	/**
	 * Is this a "per-allele + REF" INFO field?
	 */
	boolean isVcfInfoPerAlleleRef(String fieldName, VcfEntry vcfDb) {
		// Look up information and cache it
		if (vcfInfoPerAlleleRef.get(fieldName) == null) {
			if (vcfDb == null) return false; // No VCF information? I cannot look it up...nothing to do

			VcfHeaderInfo vcfInfo = vcfDb.getVcfInfo(fieldName);
			boolean isPerAlleleRef = (vcfInfo != null && vcfInfo.isNumberAllAlleles());
			vcfInfoPerAlleleRef.put(fieldName, isPerAlleleRef);
		}

		return vcfInfoPerAlleleRef.get(fieldName);
	}

	/**
	 * Create a 'key' string to check if an entry has already been added
	 */
	protected String key(VcfEntry ve) {
		return ve.getChromosomeName() //
				+ "\t" + ve.getStart() //
				+ "\t" + ve.getId() //
				+ "\t" + ve.getRef() //
				+ "\t" + ve.getAltsStr() //
				+ "\t" + ve.getInfoStr() //
		;
	}

	/**
	 * Create a hash key
	 */
	String key(VcfEntry vcfDbEntry, int altIndex) {
		if (useRefAlt) {
			if (altIndex >= 0) return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getAlts()[altIndex];
			return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getRef();
		}
		return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart();
	}

	/**
	 * Open database annotation file
	 */
	public abstract void open();

	public abstract void readDb(VcfEntry ve);

	public void setInfoFields(List<String> infoFields) {
		this.infoFields = infoFields;
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

	public int size() {
		return dbCurrentId.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Show IDs
		sb.append("IDs (size: " + dbCurrentId.size() + "):\n");
		for (String key : dbCurrentId.keySet())
			sb.append("\t" + key + "\t" + dbCurrentId.get(key) + "\n");

		// Show INFO
		sb.append("INFOs:\n");
		for (String key : dbCurrentInfo.keySet()) {
			sb.append("\t" + key + "\n");
			Map<String, String> vals = dbCurrentInfo.get(key);
			for (String ikey : vals.keySet())
				sb.append("\t\t" + ikey + "\t'" + vals.get(ikey) + "'\n");
		}

		return sb.toString();
	}

	/**
	 * Update latest chromosome
	 */
	protected void updateChromo(VcfEntry vcfEntry) {
		latestChromo = vcfEntry.getChromosomeName();
	}
}
