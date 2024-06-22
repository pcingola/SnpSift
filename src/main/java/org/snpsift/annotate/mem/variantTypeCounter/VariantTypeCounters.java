package org.snpsift.annotate.mem.variantTypeCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.util.ShowProgress;

/**
 * Variant type counters for each chromosome
*/
public class VariantTypeCounters {

	Map<String, VariantTypeCounter> counters = new HashMap<>(); // Counters per chromosome
	String latestChr = ""; // Latest chromosome
	VariantTypeCounter latestCounter = null; // Counter for the latest chromosome
	VariantTypeCounter counterAll = new VariantTypeCounter(); // Count all variants
	int count = 0;	// Total number of variants

	/**
	 * Load from file
	 */
	public static VariantTypeCounters load(String fileName) {
		// Deserialize data from a file
		try {
			var file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			var vc = (VariantTypeCounters) ois.readObject();
			ois.close();
			return vc;
		} catch (Exception e) {
			throw new RuntimeException("Cannot load from file '" + fileName + "'", e);
		}
	}

	/**
	 * Count the number of variants in a VCF file
	 */
	public void count(String vcfFileName) {
		System.out.println("Counting number of variants in " + vcfFileName);
		var vcfFile = new VcfFileIterator(vcfFileName);
		var progress = new ShowProgress();
		int i = 0;
		for (VcfEntry vcfEntry : vcfFile) {
			count(vcfEntry);
			progress.tick(i, vcfEntry); // Show progress
			i++;
		}
		vcfFile.close();
		System.out.println(this);
	}

	/**
	 * Count the number of variants in a VCF file
	 */
	public void count(VcfEntry vcfEntry) {
		// Count per chromosome
		var chr = vcfEntry.getChromosomeName();
		if(!chr.equals(latestChr) || latestCounter == null) {
			latestChr = chr;
			latestCounter = counters.get(chr);
			if(latestCounter == null) {
				latestCounter = new VariantTypeCounter();
				counters.put(chr, latestCounter);
			}
		}
		latestCounter.count(vcfEntry);

		// Count all
		counterAll.count(vcfEntry);
		count++;
	}

	public VariantTypeCounter get(String chr) {
        return counters.get(chr);
    }

	public void save(String fileName) {
		// Serialize data to a file
		// Open file
		try {
			var file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException("Cannot save to file '" + fileName + "'", e);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total variants: " + count + "\n");
		sb.append(counterAll.toString());
		for(var chr : counters.keySet()) {
			sb.append("Chromosome: " + chr + "\n");
			sb.append(counters.get(chr).toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
