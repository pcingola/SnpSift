package org.snpsift.annotate;

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

	public AnnotateVcfDbSorted(String dbFileName, int maxBlockSize) {
		super();
		DbVcfSorted dbVcfSorted = new DbVcfSorted(dbFileName);
		dbVcfSorted.setMaxBlockSize(maxBlockSize);
		dbVcf = dbVcfSorted;
	}

}
