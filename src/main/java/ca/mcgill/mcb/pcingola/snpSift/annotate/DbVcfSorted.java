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
public class DbVcfSorted extends DbVcf {

	protected FileIndexChrPos indexDb;

	public DbVcfSorted(String dbFileName) {
		super(dbFileName);
	}

	/**
	 * Index a VCF file
	 */
	FileIndexChrPos index(String fileName) {
		if (verbose) System.err.println("Index: " + fileName);
		FileIndexChrPos fileIndex = new FileIndexChrPos(fileName);
		fileIndex.setVerbose(verbose);
		fileIndex.setDebug(debug);
		fileIndex.open();
		fileIndex.index();
		fileIndex.close();
		return fileIndex;
	}

	/**
	 * Open database annotation file
	 */
	@Override
	public void open() {
		// Open and index database
		indexDb = index(dbFileName);

		// Re-open VCF db file
		try {
			vcfDbFile = new VcfFileIterator(new SeekableBufferedReader(dbFileName));
			vcfDbFile.setDebug(debug);
			nextVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
			addNextVcfDb();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Read all DB entries up to veInput (VCF entry from the input file)
	 */
	@Override
	public void readDb(VcfEntry veInput) {
		if (debug) Gpr.debug("Reading: Input " + veInput.toStr());

		// Data up to the latest entry can now be cleared, since the input file is beyond that limit
		if (latestVcfDb != null && latestVcfDb.isSameChromo(veInput) && latestVcfDb.getEnd() < veInput.getStart()) {
			if (debug) Gpr.debug("Reading: Input beyond DB's latest entry: Clearing.\tqInput: " + veInput.toStr() + "\tlatest: " + latestVcfDb.toStr());
			clear();
		}

		String chr = veInput.getChromosomeName();

		//---
		// Do we have a pending DB entry? (e.g. from a previous iteration)
		//---

		// Are we still in the same chromosome?
		if (nextVcfDb != null && nextVcfDb.isSameChromo(veInput)) {
			if (veInput.getEnd() < nextVcfDb.getStart()) {
				// We haven't reached next DB's position, nothing to do
				if (debug) Gpr.debug("Reading: Input has not reached DB position\n" + this);
				return;
			}

			// OK, add 'nextVcfDb'
			addNextVcfDb();
		} else if (nextVcfDb != null && latestVcfDb != null && latestVcfDb.isSameChromo(veInput)) { // Input and latestDb entry are in different chromosome?
			// Same chromosome as latest entry from DB.
			// This means that we finished reading all database entries from the current 'input' chromosome.
			// There is nothing else to do until the input VCF reaches a new chromosome
			if (debug) Gpr.debug("Reading: DB finished reading chromosome " + chr + "\n" + this);
			return;
		} else {
			// This means that we should jump to a database position matching VcfEntry's chromosome
			clear();

			if (debug) Gpr.debug("Reading: Jumping to chromosme " + chr);

			long filePos = indexDb.getStart(chr);
			if (filePos < 0) return; // The database file does not have this chromosome
			try {
				vcfDbFile.seek(filePos);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		//---
		// Read next DB entries
		//---
		for (VcfEntry vcfDb : vcfDbFile) {
			nextVcfDb = vcfDb;

			// Database has finished with this chromosome?
			if (!nextVcfDb.getChromosomeName().equals(chr)) {
				if (debug) Gpr.debug("Reading: Input has not reached DB's chromosome\n" + this);
				return;
			}

			// Read database until the current database entry is beyond the current VCF entry
			// Note: VCF allows more than one line with the same position
			if (veInput.getEnd() < nextVcfDb.getStart()) {
				if (debug) Gpr.debug("Reading: DB has passed input's position\n" + this);
				return;
			}

			// Add all fields to 'db'
			addNextVcfDb();
		}

		if (debug) Gpr.debug("Reading: No more DB entries\n" + this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Size: " + size() + "\n");
		sb.append("\tLatest VCF entry : " + latestVcfDb + "\n");
		sb.append("\tNext VCF entry   : " + nextVcfDb + "\n");
		sb.append(super.toString());
		return sb.toString();
	}

}
