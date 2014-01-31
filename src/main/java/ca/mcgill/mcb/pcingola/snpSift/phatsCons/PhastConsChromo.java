package ca.mcgill.mcb.pcingola.snpSift.phatsCons;

/**
 * TODO: Remove this class
 * 
 * 
 * Conservation score for one chromosome
 * 
 * @author pcingola
 */
public class PhastConsChromo {

	//	Chromosome chromosome;
	//	ArrayList<Integer> starts;
	//	ArrayList<short[]> consScores;
	//
	//	public PhastConsChromo(Chromosome chromosome) {
	//		this.chromosome = chromosome;
	//		starts = new ArrayList<Integer>();
	//		consScores = new ArrayList<short[]>();
	//	}
	//
	//	/** 
	//	 * Add an array of values
	//	 * @param start
	//	 * @param cons
	//	 */
	//	protected void add(int start, TShortArrayList cons) {
	//		// Check that segments are in order
	//		int lastStart = starts.size() <= 0 ? 0 : starts.get(starts.size() - 1);
	//		if (start < lastStart) throw new RuntimeException("Trying to add segemnt out of order. Start: " + start + ", latest start: " + lastStart);
	//
	//		// Add 'start'
	//		starts.add(start);
	//
	//		// Add scores
	//		short scores[] = new short[cons.size()];
	//		for (int i = 0; i < cons.size(); i++)
	//			scores[i] = cons.get(i);
	//		consScores.add(scores);
	//	}
	//
	//	/**
	//	 * Get score for a given position (or -1 if not found)
	//	 * @param pos
	//	 * @return
	//	 */
	//	public float get(int pos) {
	//		short score = getRawScore(pos);
	//		return ((float) score) / PhastCons.SCALE;
	//	}
	//
	//	/**
	//	 * Get raw score from arrays
	//	 * @param pos
	//	 * @return
	//	 */
	//	protected short getRawScore(int pos) {
	//		int idx = Collections.binarySearch(starts, pos);
	//		if (idx < 0) idx = -idx - 2; // See definition of binary search..
	//
	//		short cons[] = consScores.get(idx);
	//		if (cons == null) return -1;
	//
	//		int start = starts.get(idx);
	//		int arrayIdx = pos - start;
	//		if (arrayIdx >= cons.length) return -1;
	//		if (arrayIdx < 0) throw new RuntimeException("Negative index. This should never happen!");
	//
	//		return cons[arrayIdx];
	//	}
	//
	//	@Override
	//	public String toString() {
	//		int total = 0;
	//		for (short[] cons : consScores)
	//			total += cons.length;
	//
	//		return "Chromosome: " + chromosome.getId() + "\tSegments: " + consScores.size() + "\tScores: " + total;
	//	}
	//
	//	public void writeTxt(BufferedWriter out) throws IOException {
	//		for (int i = 0; i < starts.size(); i++) {
	//			int start = starts.get(i);
	//			short cons[] = consScores.get(i);
	//			out.write(chromosome.getId() + "\t" + start);
	//			for (int j = 0; j < cons.length; j++)
	//				out.write("\t" + Integer.toHexString(cons[j]));
	//			out.write("\n");
	//		}
	//	}
}
