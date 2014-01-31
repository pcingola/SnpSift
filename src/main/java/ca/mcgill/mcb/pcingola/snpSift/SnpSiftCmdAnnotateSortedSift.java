package ca.mcgill.mcb.pcingola.snpSift;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate a VCF file with SIFT (Sorting Tolerant From Intolerant) from another VCF file (database)

 * Note: Assumes both the VCF file AND the database file are sorted. 
 *       Each VCF entry should be sorted according to position. 
 *       Chromosome order does not matter (e.g. all entries for chr10 can be before entries for chr2). 
 *       But entries for the same chromosome should be together.   
 * 
 * @author pcingola
 *
 */
public class SnpSiftCmdAnnotateSortedSift extends SnpSiftCmdAnnotateSorted {

	// A score is 'low confidence' is median conservation is HIGHER than this number (see referenced paper) 
	public static final double LOW_CONFIDENCE = 3.25;

	// A variant is damaging if it has a score over this number (see referenced paper)
	public static final double MAX_DAMAGING_SCORE = 0.05;

	public SnpSiftCmdAnnotateSortedSift(String args[]) {
		super(args, "sift");
	}

	/**
	 * Add annotation form database into VCF entry's INFO field
	 * @param vcf
	 */
	@Override
	protected boolean addAnnotation(VcfEntry vcf) {
		boolean annotated = false;
		// Is this change in db?
		String infoAnnotation = findDbId(vcf);
		if (infoAnnotation != null) {
			annotated = true;
			vcf.addInfo(infoAnnotation);
		}

		return annotated;
	}

	/**
	 * Add 'key->id' entries to 'db'
	 * @param vcfDb
	 */
	@Override
	void addDb(VcfEntry vcfDb) {
		for (int i = 0; i < vcfDb.getAlts().length; i++) {
			String key = key(vcfDb, i);

			String siftScore = vcfDb.getInfo("SIFT_SCORE");
			String siftCons = vcfDb.getInfo("SIFT_CONS");

			if ((siftScore != null) && (siftCons != null)) {
				double score = Gpr.parseDoubleSafe(siftScore);
				double cons = Gpr.parseDoubleSafe(siftCons);

				// Is this damaging?
				String damaging = score >= MAX_DAMAGING_SCORE ? "TOLERATED" : "DAMAGING";

				// Create annotation showing score and damaging/tolerated
				String annotation = "SIFT=" + damaging + ";SIFT_SCORE=" + siftScore;

				// Is it low confidence? => Add flag
				if (cons > LOW_CONFIDENCE) annotation += ";SIFT_LOW_CONFIDENCE";

				dbId.put(key, annotation);
			}
		}
	}

	/**
	 * Add some lines to header before showing it
	 * @param vcfFile
	 */
	@Override
	protected void addHeader(VcfFileIterator vcfFile) {
		super.addHeader(vcfFile);
		vcfFile.getVcfHeader().addLine("##INFO=<ID=SIFT,Number=1,Type=String,Description=\"SIFT prediction {TOLERATED, DAMAGING}\">");
		vcfFile.getVcfHeader().addLine("##INFO=<ID=SIFT_SCORE,Number=1,Type=Float,Description=\"SIFT score, 0 = Damaging, 1=Tolerated\">");
		vcfFile.getVcfHeader().addLine("##INFO=<ID=SIFT_LOW_CONFIDENCE,Number=1,Type=Flag,Description=\"SIFT prediction has low confidence (i.e. bad median conservation score)\">");
	}

	/**
	 * Find an annotation for this VCF entry
	 * @param vcf
	 * @return
	 */
	@Override
	protected String findDbId(VcfEntry vcf) {
		readDb(vcf);
		if (dbId.isEmpty()) return null;

		for (int i = 0; i < vcf.getAlts().length; i++) {
			String key = key(vcf, i);
			String id = dbId.get(key);
			if (id != null) return id;
		}

		return null;
	}

}
