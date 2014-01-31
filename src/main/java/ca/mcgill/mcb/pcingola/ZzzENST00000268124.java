package ca.mcgill.mcb.pcingola;

import java.io.IOException;
import java.util.Random;

import ca.mcgill.mcb.pcingola.interval.Exon;
import ca.mcgill.mcb.pcingola.interval.Gene;
import ca.mcgill.mcb.pcingola.interval.Transcript;
import ca.mcgill.mcb.pcingola.snpEffect.Config;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.GprSeq;

public class ZzzENST00000268124 {

	public static void main(String[] args) throws IOException {
		Config config = new Config("testHg3770Chr22", Gpr.HOME + "/snpEff/" + Config.DEFAULT_CONFIG_FILE);
		config.loadSnpEffectPredictor();

		Random rand = new Random(20140129);
		StringBuilder out = new StringBuilder();

		int count = 0;
		for (Gene g : config.getGenome().getGenes()) {
			for (Transcript tr : g) {
				if (tr.getId().equals("ENST00000445220")) {
					Gpr.debug(g.getGeneName() + "\t" + tr.getId() + "\t" + tr.isProteinCoding() + "\t" + tr.numChilds() + "\t" + tr.cds().length() + "\t" + tr.isStrandPlus());
					for (Exon e : tr) {
						for (int i = e.getStart(); i < e.getEnd(); i++) {
							if (rand.nextDouble() < 0.1) {

								// Insertion length
								int insLen = rand.nextInt(10) + 1;
								if (i + insLen > e.getEnd()) insLen = e.getEnd() - i;

								int idx = i - e.getStart();

								String ref = e.basesAt(idx, 1);
								String alt = ref + GprSeq.randSequence(rand, insLen);

								String line = e.getChromosomeName() + "\t" + i + "\t.\t" + ref + "\t" + alt + "\t.\t.\tAC=1\tGT\t0/1";
								System.out.println(line);
								out.append(line + "\n");
								count++;
							}
						}
					}
				}
			}
		}

		System.err.println("Count:" + count);
		Gpr.toFile(Gpr.HOME + "/snpEff/testENST00000445220.vcf", out);
	}
}
