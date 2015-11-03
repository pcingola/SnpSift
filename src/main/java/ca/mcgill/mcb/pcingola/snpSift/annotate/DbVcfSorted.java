package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.List;

import ca.mcgill.mcb.pcingola.interval.Marker;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VariantVcfEntry;

/**
 * Use an uncompressed sorted VCF file as a database for annotations
 *
 * Note: Assumes that the VCF database file is sorted.
 *       Each VCF entry should be sorted according to position.
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2).
 *       But entries for the same chromosome should be together.
 *
 * Note: Old VCF specifications did not require VCF files to be sorted.
 *
 * @author pcingola
 */
public class DbVcfSorted extends DbVcf {

	public DbVcfSorted(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Index a VCF file
	 */
	protected void createIndex() {
		if (verbose) System.err.println("Index database file:" + dbFileName);

		//			indexDb = new FileIndexChrPos(dbFileName);
		//			indexDb.setVerbose(verbose);
		//			indexDb.setDebug(debug);
		//			indexDb.open();
		//			indexDb.index();
		//
		//		if (debug) System.err.println("Index:\n" + indexDb);
	}

	/**
	 * Open VCF database annotation file
	 */
	@Override
	public void open() {
		if (debug) Gpr.debug("Open database file:" + dbFileName);

		//		// Open database index
		//		createIndex();
		//
		//		// Re-open VCF db file
		//		try {
		//			vcfDbFile = new VcfFileIterator(new SeekableBufferedReader(dbFileName));
		//			vcfDbFile.setDebug(false); // Don't check errors, since we are doing random access (the file will appear is if it was not sorted)
		//			nextVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
		//			addNextVcfDb();
		//		} catch (IOException e) {
		//			throw new RuntimeException(e);
		//		}
	}

	@Override
	public List<VariantVcfEntry> query(Marker marker) {
		throw new RuntimeException("Unimplemented");
	}

}
