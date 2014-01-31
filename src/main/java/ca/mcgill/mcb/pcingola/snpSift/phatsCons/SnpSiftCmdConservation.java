package ca.mcgill.mcb.pcingola.snpSift.phatsCons;

import ca.mcgill.mcb.pcingola.snpSift.SnpSift;

/**
 * TODO: Remove this class
 * 
 * 
 * 
 * Calculate sample pValue form a VCF file.
 * I.e.: The probability of a SNP being in N or more cases).
 * 
 * @author pablocingolani
 */
public class SnpSiftCmdConservation extends SnpSift {

	public SnpSiftCmdConservation(String[] args, String command) {
		super(args, command);
		// TODO Auto-generated constructor stub
	}

	//	String vcfFile;
	//	String wigFile;
	//
	//	public SnpSiftCmdConservation(String args[]) {
	//		super(args, "cons");
	//	}
	//
	//	@Override
	//	public void parse(String[] args) {
	//		if (args.length <= 0) usage(null);
	//
	//		int nonOpts = -1;
	//
	//		for (int argc = 0; argc < args.length; argc++) {
	//			if ((nonOpts < 0) && args[argc].startsWith("-")) { // Argument starts with '-'?
	//
	//				if (args[argc].equals("-h") || args[argc].equalsIgnoreCase("-help")) usage(null);
	//				else if (args[argc].equals("-v")) verbose = true;
	//				else if (args[argc].equals("-q")) verbose = false;
	//				else if (args[argc].equals("-t")) {
	//					numWorkers = Gpr.parseIntSafe(args[++argc]);
	//					if (numWorkers <= 0) usage("Number of threads should be a positive number.");
	//				} else usage("Unknown option '" + args[argc] + "'");
	//
	//			} else { // Other arguments
	//				if (wigFile == null) wigFile = args[argc];
	//				else if (vcfFile == null) vcfFile = args[argc];
	//				nonOpts++;
	//			}
	//		}
	//
	//		// Sanity check
	//		if (wigFile == null) usage("Missing paramter 'file.wigFix'");
	//		if (vcfFile == null) usage("Missing paramter 'file.vcf'");
	//	}
	//
	//	/**
	//	 * Run annotation
	//	 * 
	//	 * @param vcfFile
	//	 */
	//	@Override
	//	public void run() {
	//		// Read Wig file
	//		Timer.showStdErr("Reading wig file: " + wigFile);
	//		PhastCons phastCons = new PhastCons();
	//		phastCons.setVerbose(verbose);
	//		phastCons.readWigFile(wigFile);
	//		Timer.showStdErr("done");
	//
	//		// Read VCF file
	//		VcfFileIterator vcf = new VcfFileIterator(vcfFile);
	//		int entryNum = 0;
	//		for (VcfEntry vcfEntry : vcf) {
	//			// Show header before first entry
	//			if (entryNum == 0) {
	//				addHeader();
	//				String header = vcf.getVcfHeader().toString();
	//				if (!header.isEmpty()) System.out.println(header);
	//			}
	//
	//			// Get score and add it to INFO
	//			String chrom = vcfEntry.getChromosomeName();
	//			int pos = vcfEntry.getStart();
	//			float score = phastCons.get(chrom, pos);
	//			if (score >= 0) vcfEntry.addInfo("CONS", String.format("%1.3f", score));
	//
	//			// Show entry
	//			System.out.println(vcfEntry);
	//			entryNum++;
	//		}
	//	}
	//
	//	/**
	//	 * Show usage message
	//	 * @param msg
	//	 */
	//	@Override
	//	public void usage(String msg) {
	//		if (msg != null) {
	//			System.err.println("Error: " + msg);
	//			showCmd();
	//		}
	//
	//		showVersion();
	//
	//		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar cons [-v] file.wigFix file.vcf");
	//		System.err.println("\t-v       : Be verbose");
	//		System.exit(1);
	//	}
}
