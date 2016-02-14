package org.snpsift.annotate;

/**
 * Annotate using a tabix indexed VCF "database"
 *
 * @author pcingola
 *
 */
public class AnnotateVcfDbTabix extends AnnotateVcfDb {

	public AnnotateVcfDbTabix(String dbFileName) {
		super();
		dbVcf = new DbVcfTabix(dbFileName);
	}
}
