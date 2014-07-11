package ca.mcgill.mcb.pcingola.snpSift;

import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Removes reference genotypes.
 * I.e. replaces the genotype string by the MISSING string ('.') if the genotype is just homozygous reference (e.g. '0/0')
 *
 * @author pablocingolani
 */
public class SnpSiftCmdRemoveReferenceGenotypes extends SnpSift {

	public static final int SHOW_EVERY = 1000;
	public static final int SHOW_EVERY_NL = 100 * SHOW_EVERY;

	String vcfFileName;

	public SnpSiftCmdRemoveReferenceGenotypes(String[] args) {
		super(args, "RemoveReferenceGenotypes");
	}

	@Override
	public void parse(String[] args) {
		if (args.length <= 0) vcfFileName = "-";
		else if (args.length == 1) vcfFileName = args[0];
		else usage("Too many arguments");
	}

	/**
	 * Analyze the file
	 */
	@Override
	public void run() {
		Timer.showStdErr("Reading STDIN");
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);
		vcfFile.setCreateChromos(true); // Create chromosomes when needed

		// Read all vcfEntries
		int entryNum = 1;
		for (VcfEntry vcfEntry : vcfFile) {
			VcfGenotype nogenotype = null;

			// Show header?
			if (entryNum == 1) {
				String headerStr = vcfFile.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
			}

			// Replace using 'nogenotype' if it is not a variant
			List<VcfGenotype> genotypes = vcfEntry.getVcfGenotypes();
			for (int i = 0; i < genotypes.size(); i++) {
				VcfGenotype genotype = genotypes.get(i);

				// Not a variant? => Replace
				if (!genotype.isVariant()) {
					if (nogenotype == null) nogenotype = new VcfGenotype(vcfEntry, vcfEntry.getFormat(), VcfFileIterator.MISSING);
					genotypes.set(i, nogenotype);
				}
			}

			// Show entry
			System.out.println(vcfEntry);

			// Show progress
			if (entryNum % SHOW_EVERY == 0) {
				if (entryNum % SHOW_EVERY_NL == 0) System.err.println('.');
				else System.err.print('.');
			}

			entryNum++;
		}

		Timer.showStdErr("Done");
	}

	/**
	 * Show usage and exit
	 */
	@Override
	public void usage(String errMsg) {
		if (errMsg != null) System.err.println("Error: " + errMsg);
		System.err.println("Usage: cat file.vcf | java -jar " + SnpSift.class.getSimpleName() + "" + ".jar rmRefGen [file.vcf] > file_noref.vcf");
		System.err.println("Options: ");
		System.err.println("\t file.vcf : Input VCF file. Default : STDIN");
		System.exit(1);
	}
}
