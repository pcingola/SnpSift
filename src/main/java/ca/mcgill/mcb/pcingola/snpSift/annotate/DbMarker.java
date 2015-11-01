package ca.mcgill.mcb.pcingola.snpSift.annotate;

import java.util.Collection;

import ca.mcgill.mcb.pcingola.interval.Marker;

/**
 * Use a file as a 'marker' database.
 * E.g. A VCF file, dbNSFP file/s, GTF, etc.
 * Basically any genomic data file format that could be queried
 * by genomic interval (e.g. by using Tabix, CSI or some other
 * index).
 *
 *
 * @author pcingola
 */
public interface DbMarker<T extends Marker> {

	/**
	 * Close database, free resources
	 */
	public void close();

	/**
	 * Open database (load index in memory if required)
	 */
	public abstract void open();

	/**
	 * Find matching entries in the database
	 */
	public abstract Collection<T> query(Marker marker);

	public void setDebug(boolean debug);

	public void setVerbose(boolean verbose);

}
