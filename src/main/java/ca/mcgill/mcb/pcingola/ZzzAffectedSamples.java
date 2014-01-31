package ca.mcgill.mcb.pcingola;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.snpEffect.ChangeEffect;
import ca.mcgill.mcb.pcingola.snpSift.caseControl.VariantCounter;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEffect;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

public class ZzzAffectedSamples {

	public static final String VCF_INFO_FREQ_TYPE = "FREQ";
	public static final String VCF_INFO_FREQ_SINGLETON = "Singleton";
	public static final int MIN_NON_ZERO_COUNTS = 3;

	HashMap<String, VariantCounter> varCounters;
	List<String> sampleNames;

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		String vcfFileName = Gpr.HOME + "/fly_pvuseq/dnaSeq.eff.TET_EL.vcf";

		ZzzAffectedSamples zzz = new ZzzAffectedSamples();
		zzz.run(vcfFileName);
		zzz.showResults();
	}

	public ZzzAffectedSamples() {
		varCounters = new HashMap<String, VariantCounter>();
	}

	/**
	 * Get a VarCounter for gene 'geneName'
	 * @param geneName
	 * @param ve
	 * @return
	 */
	VariantCounter getVarCounter(String geneName, VcfEntry ve) {
		VariantCounter varCounter = varCounters.get(geneName);
		if (varCounter == null) {
			varCounter = new VariantCounter(ve.getVcfGenotypes().size());
			varCounters.put(geneName, varCounter);
		}
		return varCounter;

	}

	public void run(String vcfFileName) {
		Timer.showStdErr("Reading file " + vcfFileName);

		for (VcfEntry ve : new VcfFileIterator(vcfFileName)) {
			// Set sample names
			if (sampleNames == null) sampleNames = ve.getVcfFileIterator().getVcfHeader().getSampleNames();

			// Only singletons
			if (!ve.isSingleton()) continue;

			// Genes counted in this entry
			HashSet<String> genesCounted = new HashSet<String>();

			for (VcfEffect veff : ve.parseEffects()) {
				if ((veff.getImpact() == ChangeEffect.EffectImpact.HIGH) || (veff.getImpact() == ChangeEffect.EffectImpact.MODERATE)) {
					//if (veff.getImpact() == ChangeEffect.EffectImpact.HIGH) {
					String geneName = ve.getChromosomeName() + "\t" + veff.getGene();

					// Do not count twice
					VariantCounter variantCounter = getVarCounter(geneName, ve);
					if (!genesCounted.contains(geneName)) {
						variantCounter.parseGenotypes(ve); // Increment all genotypes that have a variant
						genesCounted.add(geneName);
					}

					variantCounter.addEffect(veff.toString());
				}
			}

		}

		Timer.showStdErr("Done");
	}

	/**
	 * Show final results
	 */
	void showResults() {
		ArrayList<String> genes = new ArrayList<String>();
		genes.addAll(varCounters.keySet());
		Collections.sort(genes);

		// Show title
		System.out.print("Chromosome\tGeneName\tAffected samples");
		for (String sample : sampleNames)
			System.out.print("\t" + sample);
		System.out.println("\tEffects");

		// Show info
		for (String gene : genes) {
			VariantCounter vc = varCounters.get(gene);
			if (vc.countNonZeroGenotypes() > MIN_NON_ZERO_COUNTS) System.out.println(gene + "\t" + vc);
		}

		System.out.println("Total\t" + genes.size());
	}
}
