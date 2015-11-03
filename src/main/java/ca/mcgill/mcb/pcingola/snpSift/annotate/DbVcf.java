package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.Collection;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfHeader;

/**
 * Use a VCF file as a database for annotations
 *
 * A VCF database consists of a VCF file and an index.
 * When a query is made, the index is used to quickly get the file positions
 * where matching VCF entries are. File is read, entries are parsed and returned
 * as query() result.
 *
 * TODO: If another query matches the same region of the file, then we could use
 * some sort of caching to speed up the process.
 *
 * TODO: If the same file region is matched multiple times by successive
 * queries, creating an intervalTree from the VCF entries matching the region
 * might be effective
 *
 *
 * @author pcingola
 */
public abstract class DbVcf implements DbMarker<VcfEntry> {

	protected boolean debug = false;
	protected boolean verbose = false;
	protected String dbFileName;
	protected VcfFileIterator vcfDbFile; // VCF File
	protected VcfHeader vcfHeader;
	//	protected VcfEntry latestVcfDb = null; // Latest entry added
	//	protected VcfEntry nextVcfDb = null; // Next DB entry to add
	//	protected Map<String, Map<String, String>> dbCurrentInfo = new HashMap<String, Map<String, String>>();
	//	protected Set<String> infoFields; // Use only these INFO fields
	//	protected Map<String, String> dbCurrentId = new HashMap<String, String>(); // Current DB entries
	//	protected Map<String, Boolean> vcfInfoPerAllele = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis?
	//	protected Map<String, Boolean> vcfInfoPerAlleleRef = new HashMap<String, Boolean>(); // Is a VCF INFO field annotated 'per allele' basis AND requires reference to be annotated (i.e. VCF header has Number=R)?
	//	protected Set<String> dbVcfEntryAdded = new HashSet<String>(); // Entries that have already been added to the database

	public DbVcf(String dbFileName) {
		this.dbFileName = dbFileName;
	}

	@Override
	public void close() {
		if (vcfDbFile != null) {
			vcfDbFile.close(); // We have to close vcfDbFile because it was opened using a BufferedReader (this sets autoClose to 'false')
			vcfDbFile = null;
		}
	}

	public VcfHeader getVcfHeader() {
		return vcfHeader;
	}

	@Override
	public abstract void open();

	/**
	 * Find matching entries in the database
	 */
	@Override
	public abstract Collection<VcfEntry> query(Marker marker);

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	//	/**
	//	 * Add 'key->id' entries to 'dbCurrent'
	//	 */
	//	protected void add(VcfEntry vcfDb) {
	//		if (vcfDb == null) return;
	//
	//		// VcfEntry already added?
	//		if (checkRepeat) {
	//			String keyAdded = key(vcfDb);
	//			if (dbVcfEntryAdded.contains(keyAdded)) return;
	//			dbVcfEntryAdded.add(keyAdded);
	//		}
	//
	//		if (useInfoField) discoverInfoFields(vcfDb);
	//
	//		//---
	//		// Add information for each variant
	//		//---
	//		List<Variant> vars = vcfDb.variants();
	//		for (Variant var : vars) {
	//			String key = key(var);
	//
	//			// Add ID field information
	//			addId(key, vcfDb.getId());
	//
	//			// Add INFO fields to DB?
	//			if (useInfoField) {
	//				// Add all INFO fields
	//				Map<String, String> info = dbInfoFields(vcfDb, var, false);
	//				addInfo(key, info);
	//			}
	//		}
	//
	//		//---
	//		// Add information for each REF allele
	//		// (e.g. when INFO field has 'Number=R')
	//		//---
	//		if (useInfoField) {
	//			// Add all INFO fields
	//			for (Variant var : vars) {
	//				String key = keyRef(var);
	//
	//				// Only add for different keys
	//				if (!dbCurrentInfo.containsKey(key)) {
	//					Map<String, String> info = dbInfoFields(vcfDb, var, true);
	//					addInfo(key, info);
	//				}
	//			}
	//		}
	//
	//	}
	//
	//	/**
	//	 * Add VCF ID field
	//	 */
	//	protected void addId(String key, String id) {
	//		if (!dbCurrentId.containsKey(key)) dbCurrentId.put(key, id);
	//		else dbCurrentId.put(key, dbCurrentId.get(key) + "," + id);
	//	}
	//
	//	/**
	//	 * Add VCF ID field
	//	 */
	//	protected void addInfo(String key, Map<String, String> info) {
	//		if (!dbCurrentInfo.containsKey(key)) dbCurrentInfo.put(key, info);
	//		else {
	//			// There is information => We need to append to each entry
	//			Map<String, String> infoOri = dbCurrentInfo.get(key);
	//
	//			for (Entry<String, String> entry : info.entrySet()) {
	//				String k = entry.getKey();
	//				String v = entry.getValue();
	//
	//				if (!infoOri.containsKey(k)) infoOri.put(k, v); // This entry is NOT in infoOri, simply add it
	//				else infoOri.put(k, infoOri.get(k) + "," + v); // This entry IS in infoOri, append value
	//			}
	//		}
	//	}
	//
	//	protected void addNextVcfDb() {
	//		if (debug) Gpr.debug("Adding DB entry: " + nextVcfDb);
	//		add(nextVcfDb);
	//		latestVcfDb = nextVcfDb;
	//		nextVcfDb = null;
	//	}
	//
	//	/**
	//	 * Clear cached db entries
	//	 */
	//	protected void clear() {
	//		if (debug) Gpr.debug("Clear: Current size " + size());
	//		dbCurrentId.clear();
	//		dbCurrentInfo.clear();
	//		dbVcfEntryAdded.clear();
	//		infoFields.clear();
	//	}
	//	/**
	//	 * Extract corresponding info fields
	//	 */
	//	protected Map<String, String> dbInfoFields(VcfEntry vcfDb, Variant var, boolean onlyRef) {
	//		// Add some INFO fields
	//		Map<String, String> info = new HashMap<>();
	//
	//		// Add each field
	//		for (String fieldName : infoFields) {
	//			if (fieldName.isEmpty()) continue; // Empty INFO field may cause this
	//
	//			// Make sure we get allele specific information (if INFO is allele specific)
	//			String val = null;
	//
	//			// Check if fields are Number='A' or Number='R' (this also caches results for future reference)
	//			boolean perAlleleRef = isVcfInfoPerAlleleRef(fieldName, vcfDb);
	//
	//			if (onlyRef) {
	//				if (perAlleleRef) {
	//					val = vcfDb.getInfo(fieldName, var.getReference());
	//					if (val != null) info.put(fieldName, val);
	//				}
	//			} else {
	//				boolean perAllele = isVcfInfoPerAllele(fieldName, vcfDb);
	//
	//				// Get value
	//				if (perAllele || perAlleleRef) val = vcfDb.getInfo(fieldName, var.getGenotype());
	//				else val = vcfDb.getInfo(fieldName);
	//				if (val != null) info.put(fieldName, val);
	//			}
	//		}
	//
	//		return info;
	//	}
	//
	//	/**
	//	 * If 'ALL' info fields are being used, we can try to discover
	//	 * new fields that have not already been added to the annotation
	//	 * list (e.g. implicit fields not mentioned in the VCF header)
	//	 */
	//	protected void discoverInfoFields(VcfEntry vcfEntry) {
	//		if (!useInfoFieldAll) return; // Not using INFO fields
	//
	//		// Make sure all fields are added
	//		if (infoFields == null) infoFields = new HashSet<String>();
	//		for (String info : vcfEntry.getInfoKeys()) {
	//			if (!info.isEmpty()) infoFields.add(info);
	//		}
	//	}
	//	/**
	//	 * Is 'fieldName' a per-allele annotation
	//	 */
	//	boolean isVcfInfoPerAllele(String fieldName) {
	//		return isVcfInfoPerAllele(fieldName, null);
	//	}
	//
	//	/**
	//	 * Is 'fieldName' a per-allele annotation
	//	 */
	//	boolean isVcfInfoPerAllele(String fieldName, VcfEntry vcfDb) {
	//		// Look up information and cache it
	//		if (vcfInfoPerAllele.get(fieldName) == null) {
	//			if (vcfDb == null) return false; // No VCF information? I cannot look it up...nothing to do
	//
	//			VcfHeaderInfo vcfInfo = vcfDb.getVcfInfo(fieldName);
	//			boolean isPerAllele = vcfInfo != null && (vcfInfo.isNumberOnePerAllele() || vcfInfo.isNumberAllAlleles());
	//			vcfInfoPerAllele.put(fieldName, isPerAllele);
	//		}
	//
	//		return vcfInfoPerAllele.get(fieldName);
	//	}
	//
	//	/**
	//	 * Is this a "per-allele + REF" INFO field?
	//	 */
	//	boolean isVcfInfoPerAlleleRef(String fieldName) {
	//		return isVcfInfoPerAlleleRef(fieldName, null);
	//	}
	//
	//	/**
	//	 * Is this a "per-allele + REF" INFO field?
	//	 */
	//	boolean isVcfInfoPerAlleleRef(String fieldName, VcfEntry vcfDb) {
	//		// Look up information and cache it
	//		if (vcfInfoPerAlleleRef.get(fieldName) == null) {
	//			if (vcfDb == null) return false; // No VCF information? I cannot look it up...nothing to do
	//
	//			VcfHeaderInfo vcfInfo = vcfDb.getVcfInfo(fieldName);
	//			boolean isPerAlleleRef = (vcfInfo != null && vcfInfo.isNumberAllAlleles());
	//			vcfInfoPerAlleleRef.put(fieldName, isPerAlleleRef);
	//		}
	//
	//		return vcfInfoPerAlleleRef.get(fieldName);
	//	}
	//
	//	protected String key(Variant variant) {
	//		if (useRefAlt) return variant.getChromosomeName() + ":" + variant.getStart() + "_" + variant.getReference() + "/" + variant.getAlt();
	//		return variant.getChromosomeName() + ":" + variant.getStart();
	//	}
	//
	//	/**
	//	 * Create a 'key' string to check if an entry has already been added
	//	 */
	//	protected String key(VcfEntry ve) {
	//		return ve.getChromosomeName() //
	//				+ "\t" + ve.getStart() //
	//				+ "\t" + ve.getId() //
	//				+ "\t" + ve.getRef() //
	//				+ "\t" + ve.getAltsStr() //
	//				+ "\t" + ve.getInfoStr() //
	//				;
	//	}
	//
	//	protected String keyRef(Variant variant) {
	//		if (useRefAlt) return variant.getChromosomeName() + ":" + variant.getStart() + "_" + variant.getReference() + "/" + variant.getReference();
	//		return variant.getChromosomeName() + ":" + variant.getStart();
	//	}
	//	public void setInfoFields(boolean useInfoField, Collection<String> infoFields) {
	//		this.useInfoField = useInfoField;
	//		useInfoFieldAll = false;
	//
	//		if (useInfoField) {
	//			if (infoFields == null) {
	//				this.infoFields = null;
	//
	//				// We use INFO but do not specify any particular field => Use ALL available INFO fields
	//				if (useInfoField) useInfoFieldAll = true;
	//			} else {
	//				this.infoFields = new HashSet<String>();
	//				this.infoFields.addAll(infoFields);
	//			}
	//		} else {
	//			// Do not use info fields
	//			this.infoFields = null;
	//		}
	//	}
	//
	//	public void setUseId(boolean useId) {
	//		this.useId = useId;
	//	}
	//
	//	public void setUseRefAlt(boolean useRefAlt) {
	//		this.useRefAlt = useRefAlt;
	//	}
	//
	//	public int size() {
	//		return Math.max(dbCurrentId.size(), dbCurrentInfo.size());
	//	}
}
