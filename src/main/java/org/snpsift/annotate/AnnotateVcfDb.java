package org.snpsift.annotate;

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

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeaderInfo;

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
	protected boolean hasVcfInfoPerAlleleRef = false;
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
	protected Map<String, Boolean> vcfInfoPerAllele = new HashMap<>(); // Is a VCF INFO field annotated 'per allele' basis?
	protected Map<String, Boolean> vcfInfoPerAlleleRef = new HashMap<>(); // Is a VCF INFO field annotated 'per allele' basis AND requires reference to be annotated (i.e. VCF header has Number=R)?

	public AnnotateVcfDb() {
	}

	/**
	 * Annotate a VCF entry
	 */
	public boolean annotate(VcfEntry vcfEntry) throws IOException {
		boolean annotated = false;
		Set<String> idSet = new HashSet<>();
		Map<String, String> infos = new HashMap<>();
		boolean exists = false;

		//---
		// Find all matching database entries
		// Note that QueryResult.variantVcfEntry can be 'null'
		//---
		List<QueryResult> queryResults = new LinkedList<>();
		Set<VcfEntry> uniqueVcfEntries = new HashSet<>();
		for (Variant var : vcfEntry.variants()) {
			// Skip huge structural variants
			if (var.isStructuralHuge()) continue;

			// Query database
			Collection<VariantVcfEntry> results = query(var);

			// Make sure we add all found VcfEntries
			for (VariantVcfEntry dbEntry : results)
				uniqueVcfEntries.add(dbEntry.getVcfEntry());

			// Add query and result
			QueryResult qr = new QueryResult(var, results);
			queryResults.add(qr);
			if (debug) Gpr.debug("Adding QueryResult: " + qr);
		}

		// Try to find INFO fields that we might have not seen before
		if (useAllInfoFields) {
			for (VcfEntry ve : uniqueVcfEntries)
				discoverInfoFields(ve);
		}

		// Add INFO fields using 'REF' data
		findDbInfoRef(infos, uniqueVcfEntries);

		//---
		// Annotate all fields
		//---
		for (QueryResult qr : queryResults) {
			if (debug) Gpr.debug("Processing QueryResult: " + qr);

			if (useId) findDbId(idSet, qr);
			if (existsInfoField != null) exists |= findDbExists(qr);
			if (useInfoFields) findDbInfo(infos, qr);
		}

		// Annotate input vcfEntry
		annotated |= annotateIds(vcfEntry, idSet);
		annotated |= annotateInfo(vcfEntry, infos);
		if (exists) annotated |= annotateExists(vcfEntry);

		return annotated;
	}

	/**
	 * Add 'exists' flag to INFO fields
	 */
	protected boolean annotateExists(VcfEntry vcfEntry) {
		vcfEntry.addInfo(existsInfoField, null);
		return true;
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
		boolean annotated = false;

		// Sort keys alphabetically
		ArrayList<String> keys = new ArrayList<>();
		keys.addAll(info.keySet());
		Collections.sort(keys);

		// Add keys sorted alphabetically
		for (String key : keys) {
			String value = info.get(key);

			// Skip empty fields?
			if (!annotateEmpty && VcfEntry.isEmpty(value)) continue;

			// Add INFO entry
			if (prependInfoFieldName != null) key = prependInfoName(key);
			vcfEntry.addInfo(key, value);
			annotated = true;
		}

		return annotated;
	}

	public void close() {
		dbVcf.close();
	}

	protected void discoverInfoFields() {
		if (infoFields == null) infoFields = new HashSet<>();

		// Discover some INFO fields
		if (!useAllInfoFields) return;

		// Find INFO form VcfHeader
		if (dbVcf != null && dbVcf.getVcfHeader() != null) {
			for (VcfHeaderInfo vcfInfo : dbVcf.getVcfHeader().getVcfHeaderInfo()) {

				// Don't add implicit fields at this stage
				// Note: They are added if they are found in a VCF entry later
				if (!vcfInfo.isImplicit()) {
					String infoFieldName = vcfInfo.getId();
					infoFields.add(infoFieldName);

					// Cache values for future use
					isVcfInfoPerAllele(infoFieldName);
					isVcfInfoPerAlleleRef(infoFieldName);
				}
			}
		}
	}

	/**
	 * If 'ALL' info fields are being used, we can try to discover
	 * new fields that have not already been added to the annotation
	 * list (e.g. implicit fields not mentioned in the VCF header)
	 */
	protected void discoverInfoFields(VcfEntry dbVcfEntry) {
		// Make sure all fields are added
		for (String info : dbVcfEntry.getInfoKeys())
			if (!info.isEmpty()) infoFields.add(info);

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
	protected boolean findDbExists(QueryResult qr) {
		return qr.results != null && !qr.results.isEmpty();
	}

	/**
	 * Find an ID for this variant and add them to idSet
	 */
	protected void findDbId(Set<String> idSet, QueryResult qr) {
		for (VariantVcfEntry dbEntry : qr.results) {
			if (dbEntry == null) return;

			for (String id : dbEntry.getVcfEntry().getId().split(";"))
				idSet.add(id);
		}
	}

	/**
	 * Find INFO fields for this VCF entry
	 */
	protected void findDbInfo(Map<String, String> info, QueryResult qr) {
		for (String infoFieldName : infoFields) {
			// Is this a 'per allele' INFO field?
			if (isVcfInfoPerAllele(infoFieldName)) {
				// Append INFO values for each 'ALT'
				String newValue = findDbInfoAlt(infoFieldName, qr);
				findDbInfoAddValue(info, infoFieldName, newValue);
			} else {
				// Add only one INFO
				if (!info.containsKey(infoFieldName)) {
					String newValue = findDbInfo(infoFieldName, qr);
					findDbInfoAddValue(info, infoFieldName, newValue);
				} else {
					// This INFO field has only one entry (not 'per allele') and
					// we have already added the value in the previous 'variant'
					// iteration, so we can skip it this time
				}
			}
		}
	}

	/**
	 * Find all non-empty INFO fields 'infoFieldName' in results
	 */
	protected String findDbInfo(String infoFieldName, QueryResult qr) {
		if (debug) Gpr.debug("Finding DB data for INFO field: " + infoFieldName);
		StringBuilder sb = new StringBuilder();

		for (VariantVcfEntry varVe : qr.results) {
			if (varVe != null) {
				String val = varVe.getVcfEntry().getInfo(infoFieldName);
				if (!VcfEntry.isEmpty(val)) {
					if (debug) Gpr.debug("\tFound: " + val);
					if (sb.length() > 0) sb.append(',');
					sb.append(val);
				}
			}
		}

		return sb.length() <= 0 ? null : sb.toString();
	}

	/**
	 * Add a value to INFO hash for field 'infoFieldName'
	 */
	void findDbInfoAddValue(Map<String, String> info, String infoFieldName, String newValue) {
		if (newValue == null && !annotateEmpty) return;
		if (debug) Gpr.debug("\tINFO:" + infoFieldName + "\tnewValue: " + newValue);
		String oldValue = info.get(infoFieldName);
		String val = (oldValue == null ? "" : oldValue + ",") + (newValue != null ? newValue : ".");
		info.put(infoFieldName, val);
	}

	/**
	 * Find the first non-empty INFO field 'infoFieldName' in results
	 * Note: ALT must match
	 */
	protected String findDbInfoAlt(String infoFieldName, QueryResult qr) {
		for (VariantVcfEntry varVe : qr.results) {
			if (varVe == null) continue;

			// REMOVE COMMNENT !!!!!!!!!!!!!!!!!
			// IMPORTANT: When a variant is parse, the original 'ALT' entry is stored in
			//            the 'Variant.genotype' whereas 'variant.alt' contains
			//            a 'minimal ALT'. E.g. if we have
			//                vcfEntry.ref = 'AC'
			//                vcfEntry.alt = 'A'
			//            Then
			//                variant.ref = 'C'
			//                variant.alt = ''
			//                variant.genotype = 'A'   <-- This is the 'original' ALT field from vcfEntry
			//            That's why we use 'var.getGenotype()' in the following 'getInfo()' method.
			//
			//			String vcfAlt = qr.variant.getGenotype();
			//			String val = varVe.getVcfEntry().getInfo(infoFieldName, vcfAlt);

			String val = varVe.getVcfEntry().getInfo(infoFieldName, qr.variant);
			if (!VcfEntry.isEmpty(val)) return val;
		}

		return VcfFileIterator.MISSING;
	}

	/**
	 * Fill values for INFO fields requiring 'REF' value
	 */
	protected void findDbInfoRef(Map<String, String> info, Set<VcfEntry> uniqueVcfEntries) {
		if (!useInfoFields || !hasVcfInfoPerAlleleRef) return; // Nothing to do

		for (String infoFieldName : infoFields) {
			// Does this field require 'REF' annotation?
			if (!isVcfInfoPerAlleleRef(infoFieldName)) continue;

			// Try to find 'REF' information in any entry
			String val = null;
			for (VcfEntry dbVcfEntry : uniqueVcfEntries) {
				val = dbVcfEntry.getInfo(infoFieldName, dbVcfEntry.getRef());

				if (VcfEntry.isEmpty(val)) val = null; // Only add non-empty
				else break; // We need only one value
			}

			// Nothing found? Use 'MISSING' value
			if (val == null) val = VcfFileIterator.MISSING;

			// Store value
			info.put(infoFieldName, val);
		}
	}

	/**
	 * Is 'fieldName' a per-allele annotation
	 */
	boolean isVcfInfoPerAllele(String fieldName) {
		// Look up information and cache it
		if (vcfInfoPerAllele.get(fieldName) == null) {
			VcfHeaderInfo vcfInfo = dbVcf.getVcfHeader().getVcfHeaderInfo(fieldName);
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
			VcfHeaderInfo vcfInfo = dbVcf.getVcfHeader().getVcfHeaderInfo(fieldName);
			boolean isPerAlleleRef = (vcfInfo != null && vcfInfo.isNumberAllAlleles());
			vcfInfoPerAlleleRef.put(fieldName, isPerAlleleRef);

			hasVcfInfoPerAlleleRef |= isPerAlleleRef; // Do we have any INFO field requiring 'REF' annotation?
		}

		return vcfInfoPerAlleleRef.get(fieldName);
	}

	/**
	 * Does database entry 'dbVcfEntry' match 'variant'?
	 */
	protected boolean match(Variant var, VariantVcfEntry dbEntry) {
		// Do coordinates match?
		if (var.getChromosomeName().equals(dbEntry.getChromosomeName()) //
				&& var.getStart() == dbEntry.getStart() //
				&& var.getEnd() == dbEntry.getEnd() //
		) {
			if (useRefAlt) {
				// Compare Ref & Alt
				if (var.getReference().equalsIgnoreCase(dbEntry.getReference()) //
						&& var.getAlt().equalsIgnoreCase(dbEntry.getAlt()) //
				) return true;
			} else {
				// No need to use Ref & Alt, it's a match
				return true;
			}
		}

		return false;
	}

	public void open() {
		dbVcf.open();
		discoverInfoFields();
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

	/**
	 * Query database and find results matching 'variant'
	 */
	protected Collection<VariantVcfEntry> query(Variant variant) {
		// Query database
		Collection<VariantVcfEntry> results = dbVcf.query(variant);

		// Filter results to match 'variant'
		List<VariantVcfEntry> list = new LinkedList<>();
		for (VariantVcfEntry dbEntry : results) {
			if (match(variant, dbEntry)) {
				if (debug) Gpr.debug("dbEntry matches query\n\tvariant: " + variant + "\n\tdbEntry: " + dbEntry);
				list.add(dbEntry);
			} else {
				if (debug) Gpr.debug("dbEntry does NOT match query\n\tvariant: " + variant + "\n\tdbEntry: " + dbEntry);
			}

		}

		if (debug) Gpr.debug("Match query results: " + list.size());
		return list;
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
				this.infoFields = new HashSet<>();
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
