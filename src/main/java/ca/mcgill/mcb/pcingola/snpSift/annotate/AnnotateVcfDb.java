package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeaderInfo;

/**
 * Annotate using a VCF "database"
 *
 * @author pcingola
 *
 */
public abstract class AnnotateVcfDb {

	public static final int MAX_ERRORS = 10; // Report an error no more than X times

	protected boolean verbose, debug;
	protected boolean annotateEmpty; // Use empty fields to annotate
	protected boolean useId = true; // Annotate ID fields
	protected boolean useInfoFields = true; // Use info fields
	protected boolean useAllInfoFields = true; // Annotate all info fields
	protected boolean useRefAlt = true; // Match REF and ALT fields
	protected String chrPrev = "";
	protected String existsInfoField = null;
	protected String prependInfoFieldName;
	protected DbVcf dbVcf;
	protected VcfFileIterator vcfDbFile;
	protected HashMap<String, Integer> errCount;
	protected Set<String> infoFields; // Use only these INFO fields
	protected Map<String, Boolean> vcfInfoPerAllele = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis?
	protected Map<String, Boolean> vcfInfoPerAlleleRef = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis AND requires reference to be annotated (i.e. VCF header has Number=R)?

	public AnnotateVcfDb() {
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(VcfEntry vcfEntry) throws IOException {
		// Add information to vcfEntry
		boolean annotated = false;

		Set<String> idSet = new HashSet<>();
		Map<String, String> infos = new HashMap<>();
		boolean exists = false;

		// Annotate all info fields
		for (Variant var : vcfEntry.variants()) {
			// Query database
			Collection<VcfEntry> results = query(var);

			boolean matched = false;

			if (results != null) {
				// Check if each results matches the variant, add ID, INFO and exists information accordingly
				for (VcfEntry dbVcfEntry : results) {
					if (useInfoFields) discoverInfoFields(dbVcfEntry);

					if (match(var, dbVcfEntry)) {
						matched = true;
						if (useId) findDbId(var, idSet, dbVcfEntry);
						if (useInfoFields) findDbInfo(var, infos, dbVcfEntry);
						if (existsInfoField != null) exists |= findDbExists(var, dbVcfEntry);
					}
				}
			}

			// No match, we still need to annotate INFO fields
			if (useInfoFields && !matched) findDbInfo(var, infos, null);
		}

		// Annotate input vcfEntry
		annotated |= annotateIds(vcfEntry, idSet);
		annotated |= annotateInfo(vcfEntry, infos);
		if (exists) annotateExists(vcfEntry);

		return annotated;
	}

	/**
	 * Add 'exists' flag to INFO fields
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

			// Skip empty fields?
			if (!annotateEmpty && (value == null || value.equals(VcfFileIterator.MISSING))) continue;

			// Add INFO entry
			if (prependInfoFieldName != null) key = prependInfoName(key);
			vcfEntry.addInfo(key, value);
		}

		return true;
	}

	public void close() {
		dbVcf.close();
	}

	/**
	 * If 'ALL' info fields are being used, we can try to discover
	 * new fields that have not already been added to the annotation
	 * list (e.g. implicit fields not mentioned in the VCF header)
	 */
	protected void discoverInfoFields(VcfEntry vcfEntry) {
		if (!useAllInfoFields) return; // Not using INFO fields

		// Make sure all fields are added
		if (infoFields == null) infoFields = new HashSet<String>();
		for (String info : vcfEntry.getInfoKeys()) {
			if (!info.isEmpty()) infoFields.add(info);
		}
	}

	public List<VcfEntry> find(Variant var) {
		return null;
	}

	/**
	 * Find matching entries in the database
	 */
	public List<VcfEntry> find(VcfEntry vcfEntry) {
		List<Variant> vars = vcfEntry.variants();
		List<VcfEntry> ves = new LinkedList<>();

		for (Variant var : vars)
			ves.addAll(find(var));

		return ves;
	}

	/**
	 * Should we annotate using 'exists' field?
	 */
	protected boolean findDbExists(Variant var, VcfEntry dbVcfEntry) {
		return false;
		//		if (dbCurrentId.isEmpty()) return false;
		//
		//		String key = key(var);
		//		return dbCurrentId.containsKey(key);
	}

	/**
	 * Find an ID for this variant and add them to idSet
	 */
	protected void findDbId(Variant var, Set<String> idSet, VcfEntry dbVcfEntry) {
		for (String id : dbVcfEntry.getId().split(";"))
			idSet.add(id);
	}

	/**
	 * Find INFO fields for this VCF entry
	 */
	protected void findDbInfo(Variant var, Map<String, String> info, VcfEntry dbVcfEntry) {
		Gpr.debug("VAR:" + var);
		for (String infoFieldName : infoFields) {
			// Do we need to take care of the 'REF' allele?
			if (isVcfInfoPerAlleleRef(infoFieldName)) {
				// Did we already did it in the previous iteration?
				if (!info.containsKey(infoFieldName)) {
					String val = dbVcfEntry != null ? dbVcfEntry.getInfo(infoFieldName, dbVcfEntry.getRef()) : null;
					if (val != null) info.put(infoFieldName, val);
				}
			}

			// Get info field annotations

			// Not a 'per allele' INFO field? Then we are done (no need to annotate other alleles)
			if (isVcfInfoPerAllele(infoFieldName)) {
				// Append INFO values for each 'ALT'
				String newValue = dbVcfEntry != null ? dbVcfEntry.getInfo(infoFieldName, var.getAlt()) : null;
				String oldValue = info.get(infoFieldName);
				String val = (oldValue == null ? "" : oldValue + ",") + (newValue == null ? "." : newValue);
				Gpr.debug("\tvalue: " + val);
				info.put(infoFieldName, val);
			} else {
				// Add only one INFO
				if (!info.containsKey(infoFieldName)) {
					String newValue = dbVcfEntry != null ? dbVcfEntry.getInfo(infoFieldName) : null; // Get full INFO field
					if (newValue != null) info.put(infoFieldName, newValue);
				} else {
					// This INFO field has only one entry (not 'per allele') and
					// we have already added the value in the previous 'variant'
					// iteration, so we can skip it this time
				}
			}
		}
	}

	/**
	 * Is 'fieldName' a per-allele annotation
	 */
	boolean isVcfInfoPerAllele(String fieldName) {
		// Look up information and cache it
		if (vcfInfoPerAllele.get(fieldName) == null) {
			VcfHeaderInfo vcfInfo = dbVcf.getVcfHeader().getVcfInfo(fieldName);
			boolean isPerAllele = vcfInfo != null && (vcfInfo.isNumberOnePerAllele() || vcfInfo.isNumberAllAlleles());
			vcfInfoPerAllele.put(fieldName, isPerAllele);
		}

		return vcfInfoPerAllele.get(fieldName);
	}

	/**
	 * Is this a "per-allele + REF" INFO field?
	 */
	boolean isVcfInfoPerAlleleRef(String fieldName) {
		// Look up information and cache it
		if (vcfInfoPerAlleleRef.get(fieldName) == null) {
			VcfHeaderInfo vcfInfo = dbVcf.getVcfHeader().getVcfInfo(fieldName);
			boolean isPerAlleleRef = (vcfInfo != null && vcfInfo.isNumberAllAlleles());
			vcfInfoPerAlleleRef.put(fieldName, isPerAlleleRef);
		}

		return vcfInfoPerAlleleRef.get(fieldName);
	}

	/**
	 * Does database entry 'dbVcfEntry' match 'variant'?
	 */
	boolean match(Variant inputVariant, VcfEntry dbVcfEntry) {
		// Try to match each variant
		for (Variant var : dbVcfEntry.variants()) {
			Gpr.debug(inputVariant + "\t\t" + dbVcfEntry);
			// Do coordinates match?
			if (inputVariant.getChromosomeName().equals(var.getChromosomeName()) //
					&& inputVariant.getStart() == var.getStart() //
					&& inputVariant.getEnd() == var.getEnd() //
			) {
				if (useRefAlt) {
					Gpr.debug("MATCH: " //
							+ "\n\tREF :\t" + inputVariant.getReference() + " = " + var.getReference() //
							+ "\n\tALT: \t" + inputVariant.getAlt() + " = " + var.getAlt() //
					);
					// COmpare Ref & Alt
					if (inputVariant.getReference().equalsIgnoreCase(var.getReference()) //
							&& inputVariant.getAlt().equalsIgnoreCase(var.getAlt()) //
					) return true;
				} else {
					// No need to use Ref & Alt, it's a match
					return true;
				}
			}
		}

		return false;
	}

	public void open() {
		dbVcf.open();

		// Discover some INFO fields
		if (useAllInfoFields) {
			infoFields = new HashSet<String>();
			for (VcfHeaderInfo vcfInfo : dbVcf.getVcfHeader().getVcfInfo())
				infoFields.add(vcfInfo.getId());
		}
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

	Collection<VcfEntry> query(Variant variant) {
		return dbVcf.query(variant);
	}

	public void setAnnotateEmpty(boolean annotateEmpty) {
		this.annotateEmpty = annotateEmpty;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
		dbVcf.setDebug(debug);
	}

	public void setExistsInfoField(String existsInfoField) {
		this.existsInfoField = existsInfoField;
	}

	public void setInfoFields(boolean useInfoFields, Collection<String> infoFields) {
		this.useInfoFields = useInfoFields;
		useAllInfoFields = false;

		if (useInfoFields) {
			if (infoFields == null) {
				this.infoFields = null;

				// We use INFO but do not specify any particular field => Use ALL available INFO fields
				if (useInfoFields) useAllInfoFields = true;
			} else {
				this.infoFields = new HashSet<String>();
				this.infoFields.addAll(infoFields);
			}
		} else {
			// Do not use info fields
			this.infoFields = null;
		}
	}

	public void setPrependInfoFieldName(String prependInfoFieldName) {
		this.prependInfoFieldName = prependInfoFieldName;
	}

	public void setUseId(boolean useId) {
		this.useId = useId;
	}

	public void setUseRefAlt(boolean useRefAlt) {
		this.useRefAlt = useRefAlt;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
		dbVcf.setVerbose(verbose);
	}

	@Override
	public String toString() {
		return "Annotate VCF db:\n" //
				+ "\n\tannotateEmpty        :" + annotateEmpty //
				+ "\n\texistsInfoField      :" + existsInfoField //
				+ "\n\tprependInfoFieldName :" + prependInfoFieldName //
				+ "\n\tuseRefAlt            :" + useRefAlt //
				+ "\n\tdbVcf:\n" + Gpr.prependEachLine("\t\t", dbVcf) //
				;
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

		// Sort alphabetically
		ArrayList<String> idsSorted = new ArrayList<>();
		idsSorted.addAll(idSetDb);
		Collections.sort(idsSorted);

		// Add all items
		for (String id : idsSorted)
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
