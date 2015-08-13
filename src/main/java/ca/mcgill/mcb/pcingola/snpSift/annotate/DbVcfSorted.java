package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;

import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
public class DbVcfSorted extends DbVcfIndex {

	public static final int MIN_SEEK = 1000;

	protected FileIndexChrPos indexDb;

	public DbVcfSorted(String dbFileName) {
		super(dbFileName);
	}

	@Override
	public void close() {
		super.close();
		indexDb.close();
	}

	/**
	 * Index a VCF file
	 */
	protected void createIndex() {
		if (verbose) System.err.println("Index database file:" + dbFileName);

		indexDb = new FileIndexChrPos(dbFileName);
		indexDb.setVerbose(verbose);
		indexDb.setDebug(debug);
		indexDb.open();
		indexDb.index();

		if (debug) System.err.println("Index:\n" + indexDb);
	}

	@Override
	protected boolean dbSeek(String chr, int pos) {
		long filePosChr = indexDb.getStart(chr);
		if (filePosChr < 0) return false; // The database file does not have this chromosome

		try {
			long filePos = indexDb.find(chr, pos, true);
			if (filePos < 0) {
				// The database file does not have this position
				vcfDbFile.seek(filePosChr); // Jump to chromosome
				return false;
			}

			// Jump to position
			vcfDbFile.seek(filePos);

			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Open VCF database annotation file
	 */
	@Override
	public void open() {
		if (debug) Gpr.debug("Open database file:" + dbFileName);

		// Open database index
		createIndex();

		// Re-open VCF db file
		try {
			vcfDbFile = new VcfFileIterator(new SeekableBufferedReader(dbFileName));
			vcfDbFile.setDebug(false); // Don't check errors, since we are doing random access (the file will appear is if it was not sorted)
			nextVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
			addNextVcfDb();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected boolean shouldSeek(VcfEntry vcfEntry) {
		return ((vcfEntry.getEnd() - nextVcfDb.getStart()) > MIN_SEEK);
	}

}
