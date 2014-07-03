package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.io.IOException;

import ca.mcgill.mcb.pcingola.fileIterator.SeekableBufferedReader;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate using a VCF "database"
 *
 * Note: Assumes that the VCF database file is sorted.
 *       Each VCF entry should be sorted according to position.
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2).
 *       But entries for the same chromosome should be together.
 *
 * @author pcingola
 *
 */
public class AnnotateVcfDbSorted extends AnnotateVcfDb {

	protected FileIndexChrPos indexDb;

	public AnnotateVcfDbSorted(String dbFileName) {
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
	public void open() throws IOException {
		// Open and index database
		indexDb = index(dbFileName);

		// Re-open VCF db file
		vcfDbFile = new VcfFileIterator(new SeekableBufferedReader(dbFileName));
		latestVcfDb = vcfDbFile.next(); // Read first VCf entry from DB file (this also forces to read headers)
		addDbCurrent(latestVcfDb);
	}

	/**
	 * Read all DB entries up to 'vcf'
	 */
	@Override
	protected void readDb(VcfEntry ve) {
		String chr = ve.getChromosomeName();

		// Do we have a DB entry from our previous iteration?
		if (latestVcfDb != null) {
			// Are we still in the same chromosome?
			if (latestVcfDb.getChromosomeName().equals(chr)) {
				latestChromo = chr;
				if (ve.getStart() < latestVcfDb.getStart()) {
					clearCurrent();
					return;
				}

				if (ve.getStart() == latestVcfDb.getStart()) addDbCurrent(latestVcfDb);
			} else {
				// VCFentry and latestDb entry are in different chromosomes
				if (latestChromo.equals(chr)) {
					// This means that we finished reading all database entries from the previous chromosome.
					// There is nothing else to do until ve reaches a new chromosome
					return;
				} else {
					// This means that we should jump to a database position matching VcfEntry's chromosome
					clearCurrent();

					long filePos = indexDb.getStart(chr);
					if (filePos < 0) return;
					try {
						vcfDbFile.seek(filePos);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} else clearCurrent();

		// Read more entries from db
		for (VcfEntry vcfDb : vcfDbFile) {
			latestVcfDb = vcfDb;

			String chrDb = vcfDb.getChromosomeName();
			if (!chrDb.equals(chr)) return;

			if (ve.getStart() < vcfDb.getStart()) return;
			if (ve.getStart() == vcfDb.getStart()) {
				// Sanity check: Check that references match
				if (!ve.getRef().equals(vcfDb.getRef()) //
						&& !ve.getRef().startsWith(vcfDb.getRef()) //
						&& !vcfDb.getRef().startsWith(ve.getRef()) //
				) {
					System.err.println("WARNING: Reference in database file '" + dbFileName + "' is '" + vcfDb.getRef() + "' and reference in input file is " + ve.getRef() + "' at " + chr + ":" + (ve.getStart() + 1));
					countBadRef++;
				}

				addDbCurrent(vcfDb); // Same position: Add all keys to 'db'. Note: VCF allows more than one line with the same position
			}
		}
	}
}
