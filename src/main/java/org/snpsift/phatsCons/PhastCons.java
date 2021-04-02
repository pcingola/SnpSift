package org.snpsift.phatsCons;


/**
 * Conservation score 
 * 
 * @author pcingola
 */
public class PhastCons {

	//	public final static int SCALE = 1000;
	//
	//	boolean verbose;
	//	Genome genome;
	//	HashMap<String, PhastConsChromo> phastConsChromoById;
	//
	//	public static void main(String[] args) {
	//		String fileName = Gpr.HOME + "/snpEff/phastCons/chr22.phastCons46way.wigFix";
	//		//String fileName = Gpr.HOME + "/snpEff/phastCons/zzz.wigFix";
	//
	//		Genome genome = new Genome("test");
	//		PhastCons phastCons = new PhastCons(genome);
	//
	//		Log.info("Loading data from file: " + fileName);
	//		phastCons.readWigFile(fileName);
	//		Log.info("done");
	//
	//		//		for (int i = 0; i < 50; i++) {
	//		//			Log.debug(i + "\tscore: " + phastCons.get("22", 16059389 + i));
	//		//		}
	//
	//		String outfile = Gpr.HOME + "/phastCons.out";
	//		Log.info("Writing to file: " + outfile);
	//		phastCons.writeTxt(outfile);
	//		Log.info("Done!");
	//
	//		Log.info("Reading to file: " + outfile);
	//		phastCons = new PhastCons(genome);
	//		phastCons.readTxt(outfile);
	//		Log.info("Done!");
	//
	//	}
	//
	//	public PhastCons() {
	//		genome = new Genome();
	//		phastConsChromoById = new HashMap<String, PhastConsChromo>();
	//	}
	//
	//	public PhastCons(Genome genome) {
	//		this.genome = genome;
	//		phastConsChromoById = new HashMap<String, PhastConsChromo>();
	//	}
	//
	//	/** 
	//	 * Add an array of values
	//	 * @param chrom
	//	 * @param start
	//	 * @param cons
	//	 */
	//	void add(String chrom, int start, TShortArrayList cons) {
	//		PhastConsChromo pcChr = getOrCreatePhastConsChromo(chrom);
	//		pcChr.add(start, cons);
	//	}
	//
	//	/**
	//	 * Get score for a chromosome position pair
	//	 * @param chrom
	//	 * @param pos
	//	 * @return Return conservation score or negative if not found
	//	 */
	//	public float get(String chrom, int pos) {
	//		PhastConsChromo pc = getPhastConsChromo(chrom);
	//		if (pc == null) return -1;
	//		return pc.get(pos);
	//	}
	//
	//	public PhastConsChromo getOrCreatePhastConsChromo(String chrom) {
	//		chrom = Chromosome.simpleName(chrom);
	//		PhastConsChromo pc = phastConsChromoById.get(chrom);
	//		if (pc == null) {
	//			pc = new PhastConsChromo(genome.getOrCreateChromosome(chrom));
	//			phastConsChromoById.put(chrom, pc);
	//		}
	//		return pc;
	//	}
	//
	//	public PhastConsChromo getPhastConsChromo(String chrom) {
	//		chrom = Chromosome.simpleName(chrom);
	//		return phastConsChromoById.get(chrom);
	//	}
	//
	//	public void readTxt(String fileName) {
	//		LineFileIterator lfi = new LineFileIterator(fileName);
	//
	//		for (String line : lfi) {
	//			// Parse line
	//			String recs[] = line.split("\t");
	//			String chromo = recs[0];
	//			int start = Gpr.parseIntSafe(recs[1]);
	//			TShortArrayList cons = new TShortArrayList();
	//			for (int i = 2; i < recs.length; i++)
	//				cons.add(Short.parseShort(recs[i], 16));
	//
	//			// Add 
	//			add(chromo, start, cons);
	//		}
	//	}
	//
	//	/**
	//	 * Read a WIG file. Typically from UCSC
	//	 * E.g.:	http://hgdownload.cse.ucsc.edu/goldenPath/hg19/phastCons46way/vertebrate
	//	 * 
	//	 * @param fileName
	//	 */
	//	public void readWigFile(String fileName) {
	//		LineFileIterator lfi = new LineFileIterator(fileName);
	//		String chrom = "";
	//		int start = -1, step = 1;
	//		TShortArrayList cons = new TShortArrayList();
	//
	//		for (String line : lfi) {
	//			// Parse 'step' lines
	//			if (line.startsWith("fixedStep")) {
	//				// Add old list
	//				if (!chrom.isEmpty()) {
	//					add(chrom, start, cons);
	//					if (verbose) Log.info("Added: " + chrom + ":" + start + "\t" + cons.size() + " scores.");
	//				}
	//
	//				// Parse line
	//				String recs[] = line.split("\\s+");
	//				chrom = "";
	//				start = -1;
	//				step = -1;
	//
	//				// Parse records
	//				for (String rec : recs) {
	//					String nv[] = rec.split("=");
	//					if (nv.length == 2) {
	//						String name = nv[0];
	//						String value = nv[1];
	//
	//						if (name.equals("chrom")) chrom = value;
	//						else if (name.equals("start")) start = Gpr.parseIntSafe(value);
	//						else if (name.equals("step")) step = Gpr.parseIntSafe(value);
	//					}
	//				}
	//
	//				// Sanity check
	//				if (chrom.isEmpty()) throw new RuntimeException("Cannot find record 'chrom' in line: " + line);
	//				if ((start < 0)) throw new RuntimeException("Cannot find record 'start' in line: " + line);
	//				if (step < 0) throw new RuntimeException("Cannot find record 'step' in line: " + line);
	//
	//				// Initialize for new phase
	//				cons = new TShortArrayList();
	//			} else {
	//				short value = (short) (SCALE * Gpr.parseFloatSafe(line));
	//				cons.add(value);
	//			}
	//		}
	//		// Add last 
	//		if (!chrom.isEmpty()) add(chrom, start, cons);
	//
	//	}
	//
	//	public void setVerbose(boolean verbose) {
	//		this.verbose = verbose;
	//	}
	//
	//	@Override
	//	public String toString() {
	//		StringBuilder sb = new StringBuilder();
	//		for (PhastConsChromo pc : phastConsChromoById.values())
	//			sb.append(pc + "\n");
	//		return sb.toString();
	//	}
	//
	//	/**
	//	 * Write to a file
	//	 * @param fileName
	//	 */
	//	public void writeTxt(String fileName) {
	//		try {
	//			BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
	//			for (PhastConsChromo pc : phastConsChromoById.values())
	//				pc.writeTxt(outFile);
	//			outFile.close();
	//		} catch (IOException e) {
	//			throw new RuntimeException(e);
	//		}
	//	}

}
