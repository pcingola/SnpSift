package ca.mcgill.mcb.pcingola.snpSift;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.ped.PedPedigree;
import ca.mcgill.mcb.pcingola.ped.TfamEntry;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;
import ca.mcgill.mcb.pcingola.vcf.VcfGenotype;

/**
 * Transform a VCF to a TPED file
 *
 * @author pcingola
 */
public class SnpSiftCmdVcf2Tped extends SnpSift {

	// What to do with mssing genotypes
	public enum UseMissing {
		DO_NOT_USE // Do not use: Lines are filtered out
		, MISSING // Mark as missing in TPED file
		, REFERENCE // Mark as reference in TPED file
	};

	boolean onlySnp; // Only use SNPs in VCF files
	boolean onlyBiAllelic; // Only use bi-allelic variants.
	boolean force; // Overwrite new files if they exist
	boolean useNumbers; // Use numbers instead of letters
	UseMissing useMissing; // Do not use genotypes having missing values
	String vcfFile, tfamFile;
	String outputFileName;
	String outTpedFile, outTfamFile;
	PedPedigree pedigree;

	public SnpSiftCmdVcf2Tped(String[] args) {
		super(args, "vcf2tped");
	}

	/**
	 * Default parameters
	 */
	@Override
	public void init() {
		onlySnp = false; // Only use SNPs in VCF files
		onlyBiAllelic = false; // Only use bi-allelic variants.
		force = false; // Overwrite files
		useNumbers = false;
		useMissing = UseMissing.MISSING; // Use missing genotypes
	}

	/**
	 * Load all data
	 */
	void loadTfam() {
		if (verbose) Timer.showStdErr("Loading TFAM file '" + tfamFile + "'");
		pedigree = new PedPedigree();
		pedigree.loadTfam(tfamFile);
	}

	@Override
	public void parse(String[] args) {
		if (args.length <= 0) usage(null);

		for (int argc = 0; argc < args.length; argc++) {
			if (args[argc].equals("-useMissingRef")) useMissing = UseMissing.REFERENCE;
			else if (args[argc].equals("-useMissing")) useMissing = UseMissing.MISSING;
			else if (args[argc].equalsIgnoreCase("-num")) useNumbers = true;
			else if (args[argc].equalsIgnoreCase("-onlySnp")) onlySnp = true;
			else if (args[argc].equalsIgnoreCase("-onlyBiAllelic")) onlyBiAllelic = true;
			else if (args[argc].equalsIgnoreCase("-f")) force = true;
			else if (isOpt(args[argc])) usage("Unknown option '" + args[argc] + "'"); // Argument starts with '-'? (all default arguments are processed by SnpSift
			else if (tfamFile == null) tfamFile = args[argc];
			else if (vcfFile == null) vcfFile = args[argc];
			else if (outputFileName == null) outputFileName = args[argc];
		}

		// Sanity check
		if (tfamFile == null) usage("Missing paramter 'file.tped'");
		if (vcfFile == null) usage("Missing paramter 'file.vcf'");
		if (outputFileName == null) usage("Missing paramter 'outputName'");

		// Check input files
		if (!Gpr.canRead(vcfFile)) fatalError("Cannot read file '" + vcfFile + "'");
		if (!Gpr.canRead(tfamFile)) fatalError("Cannot read file '" + tfamFile + "'");

	}

	/**
	 * Run annotations
	 */
	@Override
	public void run() {
		// Create output file names
		outTpedFile = outputFileName + ".tped";
		outTfamFile = outputFileName + ".tfam";
		if (!force && Gpr.canRead(outTpedFile)) fatalError("VCF file '" + outTpedFile + "' already exists.");
		if (!force && Gpr.canRead(outTfamFile)) fatalError("TFAM file '" + outTfamFile + "' already exists.");

		// Convert from VCF to TPED
		vcf2Tped(vcfFile, tfamFile, outTfamFile, outTpedFile);
	}

	/**
	 * Return REF anf ALT values as if they were a SNP
	 *
	 * Important: If the variant is NOT a SNP, we create a 'fake' snp ( A -> T ).
	 * 			  This is done in order to be able to MAP InDels into PED files and keep compatibility with downstream programs (GenAble).
	 * 			  Yes, it's an awful hack. YOu've been warned!
	 */
	String snpGenotype(VcfEntry ve, VcfGenotype gen, int genoNum) {
		String base = "";

		if (ve.isSnp()) {
			// SNPs
			if (genoNum < 0) base = ve.getRef(); // Reference
			else base = gen.getGenotype(genoNum);
		} else {
			// Indel, MNP or other subsitutions
			// Create fake SNP "A -> T" and map InDel values to it
			if (genoNum < 0) base = "A"; // Reference
			else if (gen.getGenotype(genoNum).equals(ve.getRef())) base = "A"; // ALT[genoNum] == REF
			else base = "T"; // ALT[genoNum] != REF
		}

		if (!useNumbers) return base;

		// Convert to numbers
		if (base.equalsIgnoreCase("A")) return "1";
		if (base.equalsIgnoreCase("C")) return "2";
		if (base.equalsIgnoreCase("G")) return "3";
		if (base.equalsIgnoreCase("T")) return "4";
		return "0";
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar vcf2tped [options] file.tfam file.vcf outputName");
		System.err.println("Options:");
		System.err.println("\t-f             : Force. Overwrite new files if they exist. Default: " + force);
		System.err.println("\t-num           : Use only numbers {1, 2, 3, 4} instead of bases {A, C, G, T}. Default: " + useNumbers);
		System.err.println("\t-onlySnp       : Use only SNPs when converting VCF to TPED. Default: " + onlySnp);
		System.err.println("\t-onlyBiAllelic : Use only bi-allelic variants. Default: " + onlyBiAllelic);
		System.err.println("\t-useMissing    : Use entries with missing genotypes (otherwise they are filtered out). Default: " + (useMissing == UseMissing.MISSING));
		System.err.println("\t-useMissingRef : Use entries with missing genotypes marking them as 'reference' instead of 'missing'. Default: " + (useMissing == UseMissing.REFERENCE));
		System.err.println("Parameters:");
		System.err.println("\tfile.tfam      : File with genotypes and groups information (in PLINK's TFAM format)");
		System.err.println("\tfile.vcf       : A VCF file (variants and genotype data)");
		System.err.println("\toutputName     : Base name for the new TPED and TFAM files.");
		System.exit(1);
	}

	/**
	 * Convert a VCF to a TPED file
	 * @param vcfFile
	 * @param tpedFile
	 *
	 * Important: If the variant is NOT a SNP, we create a 'fake' snp ( A -> T ).
	 * 			  This is done in order to be able to MAP InDels into PED files and keep compatibility with downstream programs (GenAble).
	 * 			  Yes, it's an awful hack. YOu've been warned!
	 *
	 */
	public void vcf2Tped(String vcfFile, String tfamFile, String outTfamFile, String outTpedFile) {
		if (verbose) Timer.showStdErr("Converting file '" + vcfFile + "' to TPED format: '" + outTpedFile + "'");

		int countVcf = 1, countTped = 0;
		int skipMissing = 0, skipNotSnp = 0, skipNonBiAllelic = 0;
		boolean useSample[] = null; // Which samples should be used
		try {
			// Open files
			VcfFileIterator vcf = new VcfFileIterator(vcfFile);
			vcf.setDebug(debug);

			BufferedWriter tped = new BufferedWriter(new FileWriter(outTpedFile));

			// Convert VCF to TPED
			boolean isHeader = true;
			for (VcfEntry ve : vcf) {
				// Process header information
				if (isHeader) {
					useSample = vcfAndTfamSamples(vcf, tfamFile, outTfamFile); // Consolidate TFAM and VCF samples
					isHeader = false;
				}

				// Warning: More than one ALT is not currently supported
				// Warning: Only SNPs are supported
				try {
					if (onlyBiAllelic && (ve.getAlts().length != 1)) { // No bi-allelic? => We skip it if 'onlyBiAllelic' is true
						skipNonBiAllelic++;
						if (debug) System.err.println("Skipping line " + vcf.getLineNum() + ": Not bi-allelic");
					} else if (onlySnp && !ve.isSnp()) { // Not a SNP? skip it if 'onlySnp' is true
						skipNotSnp++;
						if (debug) System.err.println("Skipping line " + vcf.getLineNum() + ": Not a SNP");
					} else {
						boolean missingValues = false; // Any missing values in this line?

						// Prepare TPED line
						StringBuilder tpedLine = new StringBuilder();

						int pos = ve.getStart() + 1;
						String chr = ve.getChromosomeName();
						String id = chr + "_" + pos;
						// String id = "id_" + vcf.getLineNum(); // Create a unique ID

						tpedLine.append(chr + " "); // Chromosome
						tpedLine.append(id + " "); // Identifier
						tpedLine.append("0 "); // Genetic distance in Morgans (0 = missing)
						tpedLine.append(pos + " "); // Base pair position

						// Add all genotypes
						int i = 0;
						for (VcfGenotype gen : ve) {
							// Should we use this sample?
							if (useSample[i++]) {
								if (gen.getGenotypeCode() < 0) { // Missing genotype?
									missingValues = true;
									if (useMissing == UseMissing.REFERENCE) {
										String ref = snpGenotype(ve, gen, -1);
										tpedLine.append(ref + " " + ref + " "); // Mark both of them as reference
									} else tpedLine.append("0 0 "); // Mark both as missing
								} else {
									String gen0 = snpGenotype(ve, gen, 0);
									String gen1 = snpGenotype(ve, gen, 1);
									if (gen.getGenotype().length == 2) tpedLine.append(gen0 + " " + gen1 + " ");
									else {
										if (useMissing == UseMissing.REFERENCE) {
											String ref = ve.getRef();
											tpedLine.append(ref + " " + ref + " "); // Mark both of them as reference
										} else tpedLine.append("0 0 "); // Mark both as missing
									}
								}
							}
						}

						// Remove last space
						int lastChar = tpedLine.length() - 1;
						if (tpedLine.charAt(lastChar) == ' ') tpedLine.deleteCharAt(lastChar);

						tpedLine.append('\n');

						// Write to TPED file
						if ((useMissing != UseMissing.DO_NOT_USE) || !missingValues) {
							tped.write(tpedLine.toString());
							countTped++;
						} else {
							// Skipped because of misisng values?
							skipMissing++;
							if (debug) System.err.println("Skipping line " + vcf.getLineNum() + ": Missing values");
						}
					}

					countVcf++;
					if (verbose && (countVcf % 1000 == 0)) Timer.showStdErr("\tLine " + countVcf + "\t" + ve.getChromosomeName() + ":" + (ve.getStart() + 1));
				} catch (Exception e) {
					Gpr.debug("Exception processing VCF entry : " + ve);
					e.printStackTrace();
				}

			}

			// Close
			tped.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Show some info
		if (verbose) Timer.showStdErr("Done: " //
				+ "\n\tVCF entries converted     : " + countVcf //
				+ "\n\tTPED entries              : " + countTped //
				+ "\n\tSkipped Non Biallelic     : " + skipNonBiAllelic //
				+ "\n\tSkipped Non SNPs          : " + skipNotSnp //
				+ "\n\tSkipped Missing genotypes : " + skipMissing //
		);
	}

	/**
	 * Consolidate VCF and TFAM samples
	 * Create a boolean array indicating which VCf samples to use (samples not in TFAM file will be skipped in the conversions process)
	 * @param vcf
	 */
	boolean[] vcfAndTfamSamples(VcfFileIterator vcf, String tfamFile, String outTfamFile) {
		// Open TFAM file
		PedPedigree tfam = new PedPedigree(tfamFile);

		// Get VCF samples
		List<String> sampleNamesVcf = vcf.getSampleNames();

		// Create a 'common' list of samples
		HashSet<String> stfam = new HashSet<String>();
		stfam.addAll(tfam.getSampleIds());

		// Create a boolean array showing which samples to use
		boolean use[] = new boolean[sampleNamesVcf.size()];
		int i = 0;
		for (String sampleNameVcf : vcf.getSampleNames())
			use[i++] = stfam.contains(sampleNameVcf);

		//---
		// Now we have to create a new TFAM file containing ONLY the samples in both VCF and TFAM files
		// Note: The new file is sorted in the same order as the VCF.
		//---
		PedPedigree newTfam = new PedPedigree();
		for (String sampleIdVcf : vcf.getSampleNames()) {
			TfamEntry te = tfam.get(sampleIdVcf);
			if (te != null) newTfam.add(te);
		}

		// Sanity check
		// Sanity check
		if (sampleNamesVcf.size() != tfam.size()) System.err.println("WARNING: Number of samples in TFAM file and VCF file do not match" //
				+ "\n\tSamples in VCF file   : " + sampleNamesVcf.size() //
				+ "\n\tSamples in TFAM file  : " + tfam.size() //
				+ "\n\tSamples in both files : " + newTfam.size() //
		);

		if (newTfam.size() <= 0) throw new RuntimeException("New TFAM file has no entries!");

		// Save new file
		if (verbose) Timer.showStdErr("Saving new TFAM file '" + outTfamFile + "'");
		newTfam.saveTfam(outTfamFile);

		return use;
	}
}
