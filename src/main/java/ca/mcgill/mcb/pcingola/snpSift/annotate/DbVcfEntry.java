package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;

public class DbVcfEntry {

	protected String latestChromo = "";
	protected boolean useId = true; // Annotate ID fields
	protected boolean useInfoField = true; // Use all info fields
	protected boolean useRefAlt = true;
	protected HashMap<String, Map<String, String>> dbCurrentInfo = new HashMap<String, Map<String, String>>();
	protected List<String> infoFields; // Use only these INFO fields
	protected HashMap<String, String> dbCurrentId = new HashMap<String, String>(); // Current DB entries
	protected HashMap<String, Boolean> vcfInfoPerAllele = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis?
	protected HashMap<String, Boolean> vcfInfoPerAlleleRef = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis AND requires reference to be annotated (i.e. VCF header has Number=R)?

	/**
	 * Add 'key->id' entries to 'dbCurrent'
	 */
	public void addDbCurrent(VcfEntry vcfDb) {
		latestChromo = vcfDb.getChromosomeName();

		String alts[] = vcfDb.getAlts();

		//---
		// Add information for each ALT allele
		//---
		for (int alleleIdx = 0; alleleIdx < alts.length; alleleIdx++) {
			String key = key(vcfDb, alleleIdx);

			// Add ID field information
			dbCurrentId.put(key, vcfDb.getId());

			// Add INFO fields to DB?
			if (useInfoField) {
				// Add all INFO fields
				Map<String, String> info = dbInfoFields(vcfDb, alts[alleleIdx]);
				dbCurrentInfo.put(key, info);
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
			dbCurrentInfo.put(key, info);
		}

	}

	public boolean checkChromo(VcfEntry vcfEntry) {
		return latestChromo.equals(vcfEntry.getChromosomeName());
	}

	protected void clear() {
		dbCurrentId.clear();
		dbCurrentInfo.clear();
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
	 * Find an ID for this VCF entry
	 */
	protected String findDbId(VcfEntry vcf) {
		if (!useId || dbCurrentId.isEmpty()) return null;

		// Find for each ALT
		StringBuilder ids = null;
		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			String id = dbCurrentId.get(key);
			if (id != null) {
				if (ids == null) ids = new StringBuilder(id);
				else ids.append("," + id);
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

			if (isVcfInfoPerAlleleRef(infoFieldName)) {

			}
		}

		return results;
	}

	public String getLatestChromo() {
		return latestChromo;
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
	 * Create a hash key
	 */
	String key(VcfEntry vcfDbEntry, int altIndex) {
		if (useRefAlt) {
			if (altIndex >= 0) return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getAlts()[altIndex];
			return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart() + "_" + vcfDbEntry.getRef() + "/" + vcfDbEntry.getRef();
		}
		return vcfDbEntry.getChromosomeName() + ":" + vcfDbEntry.getStart();
	}

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
		sb.append("IDs:\n");
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

	public void updateChromo(VcfEntry vcfEntry) {
		latestChromo = vcfEntry.getChromosomeName();
	}
}
