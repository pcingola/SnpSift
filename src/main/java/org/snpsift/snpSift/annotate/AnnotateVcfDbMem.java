package org.snpsift.snpSift.annotate;

/**
 * Annotate using a VCF "database"
 *
 * Note: Reads and loads the whole VCF file into memory
 *
 * @author pcingola
 */
public class AnnotateVcfDbMem extends AnnotateVcfDb {

	public AnnotateVcfDbMem(String dbFileName) {
		super();
		dbVcf = new DbVcfMem(dbFileName);
	}

}
