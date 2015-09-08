package ca.mcgill.mcb.pcingola.snpSift.annotate;

import ca.mcgill.mcb.pcingola.interval.Marker;

/**
 * Represents a marker in a file (located at 'flieIdx' bytes since the beginning of the file)
 *
 * @author pcingola
 */
public class MarkerFile extends Marker {

	private static final long serialVersionUID = -3649469013731964066L;

	protected long fileIdx;

	public MarkerFile(Marker parent, int start, int end, long fileIdx) {
		super(parent, start, end);
		this.fileIdx = fileIdx;
	}

	public long getFileIdx() {
		return fileIdx;
	}

	@Override
	public String toString() {
		return getChromosomeName() + "\t" + start + "-" + end //
				+ " " //
				+ ((id != null) && (id.length() > 0) ? " '" + id + "'" : "") //
				+ "file_index:" + fileIdx //
				;
	}

}
