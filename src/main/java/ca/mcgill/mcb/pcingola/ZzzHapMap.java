package ca.mcgill.mcb.pcingola;

import java.util.HashMap;

import ca.mcgill.mcb.pcingola.fileIterator.LineFileIterator;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.fileIterator.VcfHapMapFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.snpSift.VcfLd;
import ca.mcgill.mcb.pcingola.snpSift.hwe.VcfHwe;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

public class ZzzHapMap {

	static boolean verbose = false;

	Genome genome;
	double minPhew = 0.5;
	HashMap<String, VcfEntry> vcfEntries;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String hapMapFileName = Gpr.HOME + "/ld/hapmap3_r2_b36_fwd.consensus.qc.poly.chr22_yri.phased";
		String fastaFile = Gpr.HOME + "/ld/hg36.chr22.fa";
		String chrName = "22";
		String ldFile = Gpr.HOME + "/ld/ld_chr22_YRI.txt";

		hapMapFileName = Gpr.HOME + "/ld/test.phased";
		ldFile = Gpr.HOME + "/ld/ld_test.txt";

		//		VcfFileIndex zzz = new VcfFileIndex();
		//		zzz.zzz(hapMapFileName, fastaFile, chrName, ldFile);
	}

	public ZzzHapMap() {
		genome = new Genome("genome");
	}

	void calcToCenter() {
		//---
		// Calculate 
		//---
		VcfLd vcfLd = new VcfLd();
		int center = vcfEntries.size() / 2;
		VcfEntry eCenter = vcfEntries.get(center);
		System.out.println("h\ti\tj\tdist\tr2\tDprime");
		for (int h = 1; ((center - h) >= 0) && ((center + h) < vcfEntries.size()); h++) {
			int i = center - h;
			int j = center + h;

			VcfEntry ei = vcfEntries.get(i);
			VcfEntry ej = vcfEntries.get(j);

			vcfLd.ld(eCenter, ei);
			int dist = eCenter.getStart() - ei.getStart();
			if (vcfLd.getDprime() != Double.NaN) System.out.println(h + "\t" + i + "\t" + j + "\t" + dist + "\t" + vcfLd.getrSquare() + "\t" + vcfLd.getDprime());

			vcfLd.ld(eCenter, ej);
			dist = eCenter.getStart() - ej.getStart();
			if (vcfLd.getDprime() != Double.NaN) System.out.println(h + "\t" + i + "\t" + j + "\t" + dist + "\t" + vcfLd.getrSquare() + "\t" + vcfLd.getDprime());

		}
	}

	/**
	 * Read a hapMap file
	 * @param hapMapFileName
	 * @param fastaFile
	 */
	void readHapMapPhase(String hapMapFileName, String fastaFile) {
		// Read sequence from fasta file (only one sequence per file, so we just read the first sequence)
		Timer.showStdErr("Reading '" + hapMapFileName + "'");
		VcfHapMapFileIterator vhmFile = new VcfHapMapFileIterator(hapMapFileName, fastaFile, genome);
		vcfEntries = new HashMap<String, VcfEntry>();
		double minPhew = 0.1;
		VcfHwe vcfHwe = new VcfHwe();
		for (VcfEntry vcfEntry : vhmFile) {
			double p = vcfHwe.hwe(vcfEntry, true);

			if (p > minPhew) {
				vcfEntries.put(vcfEntry.getId(), vcfEntry);
				if (verbose) System.out.println("YES\t" + p + "\t" + vcfEntry);
			} else if (verbose) System.out.println("NO\t" + p + "\t" + vcfEntry);
		}
		Timer.showStdErr("Done: " + vcfEntries.size() + " entries added.");
	}

	/**
	 * Read VCF entries in a file,
	 * @return
	 */
	void readVcf(String vcfFileName) {
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName, genome);
		vcfFile.setCreateChromos(true); // Create chromosomes when needed

		//---
		// Read all vcfEntries
		//---
		int entryNum = 0;
		vcfEntries = new HashMap<String, VcfEntry>();
		VcfHwe vcfHwe = new VcfHwe();
		for (VcfEntry vcfEntry : vcfFile) {
			double p = vcfHwe.hwe(vcfEntry, false);

			if (p > minPhew) {
				vcfEntries.put(vcfEntry.getId(), vcfEntry);
				System.out.println("YES\t" + entryNum + "\t" + p);
			} else System.out.println("NO\t" + entryNum + "\t" + p);
			entryNum++;
		}
		System.out.println("Total added: " + vcfEntries.size());
	}

	/**
	 * Quick test method
	 * @param hapMapFileName
	 * @param chrName
	 * @param fastaFile
	 */
	void zzz(String hapMapFileName, String fastaFile, String chrName, String ldFile) {
		// Initialize
		Chromosome chr = new Chromosome(genome, 0, 0, 1, "22");
		genome.add(chr);

		// Read hapmap file/s
		readHapMapPhase(hapMapFileName, fastaFile);

		System.out.println(vcfEntries.get("rs9606669"));
		System.out.println(vcfEntries.get("rs5994258"));

		// Read LD file
		Timer.showStdErr("Reading LD file " + ldFile);
		VcfLd.debug = true;
		VcfLd vcfLd = new VcfLd();
		LineFileIterator lfi = new LineFileIterator(ldFile);
		for (String line : lfi) {
			try {
				//---
				// Parse line
				//---
				String recs[] = line.split("\\s");

				int pos1 = Gpr.parseIntSafe(recs[0]);
				int pos2 = Gpr.parseIntSafe(recs[1]);

				String id1 = recs[3];
				String id2 = recs[4];

				double dPrime = Gpr.parseDoubleSafe(recs[5]);
				double rSquare = Gpr.parseDoubleSafe(recs[6]);
				//				double lod = Gpr.parseDoubleSafe(recs[7]);
				//				double fbin = Gpr.parseDoubleSafe(recs[8]);

				//---
				// Are both of them in the hash?
				//---
				if (vcfEntries.containsKey(id1) && vcfEntries.containsKey(id2)) {
					VcfEntry vcf1 = vcfEntries.get(id1);
					VcfEntry vcf2 = vcfEntries.get(id2);

					// Sanity check: Do positions match?
					int delta1 = pos1 - 1 - vcf1.getStart();
					int delta2 = pos2 - 1 - vcf2.getStart();

					if ((delta1 == 0) && (delta2 == 0)) {
						vcfLd.ld(vcf1, vcf2);
						System.out.println(id1 + "\t" + id2 //
								+ "\n\tD'   : " + dPrime + "\t" + vcfLd.getDprime() + "\t" + vcfLd.getD() //
								+ "\n\tr^2  : " + rSquare + "\t" + vcfLd.getrSquare() //
						);
					} else {
						System.out.println("ERROR:\t" + id1 + "\t" + id2 + "\tdeltas: " + delta1 + "\t" + delta2);
					}
				}
			} catch (Exception e) {
				System.err.println("Error parsing line " + line);
			}
		}
		Timer.showStdErr("Done.");
	}
}
