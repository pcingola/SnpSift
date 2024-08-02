package org.snpsift.annotate.mem.variantTypeCounter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.Fields;
import org.snpsift.util.ShowProgress;

/**
 * Variant type counters for each chromosome
*/
public class VariantTypeCounters {

	Fields fields; // Fields to create or annotate
    Map<String, VariantTypeCounter> counters = new HashMap<>(); // Counters per chromosome
	String latestChr = ""; // Latest chromosome
	VariantTypeCounter latestCounter = null; // Counter for the latest chromosome
	VariantTypeCounter counterAll; // Count all variants
	int count = 0;	// Total number of variants

	public VariantTypeCounters(Fields fields) {
        this.fields = fields;
		counterAll = new VariantTypeCounter(fields);
    }

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
		Log.info("Counting number of variants in '" + vcfFileName + "'");
		count(new VcfFileIterator(vcfFileName));
	}
	
	public void count(VcfFileIterator vcfFile) {
		var progress = new ShowProgress();
		int i = 0;
		for (VcfEntry vcfEntry : vcfFile) {
			count(vcfEntry);
			i++;
			progress.tick(i, vcfEntry); // Show progress
		}
		vcfFile.close();
		Log.info("\nDone, " + i + " VCF entries in " + progress.elapsedSec() + " secs\n");
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
				latestCounter = new VariantTypeCounter(fields);
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

	public Fields getFields() {
		return fields;
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
