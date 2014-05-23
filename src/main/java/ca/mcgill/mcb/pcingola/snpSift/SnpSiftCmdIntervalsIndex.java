package ca.mcgill.mcb.pcingola.snpSift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.mcgill.mcb.pcingola.fileIterator.BedFileIterator;
import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.interval.Chromosome;
import ca.mcgill.mcb.pcingola.interval.Genome;
import ca.mcgill.mcb.pcingola.interval.Variant;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.FileIndexChrPos;

/**
 * Filter variants that hit intervals
 * 
 * Use an indexed VCF file. 
 * 
 * WARNIGN: File must be uncompressed
 * 
 * @author pablocingolani
 */
public class SnpSiftCmdIntervalsIndex extends SnpSift {

	boolean listCommandLine;
	List<Variant> seqChanges;
	Genome genome;
	int inOffset;
	String vcfFile;
	String bedFile;

	public SnpSiftCmdIntervalsIndex(String[] args) {
		super(args, "intidx");
	}

	@Override
	public void init() {
		genome = new Genome("genome");
		listCommandLine = false;
		seqChanges = new ArrayList<Variant>();
		inOffset = 0;
		vcfFile = null;
		bedFile = null;
	}

	/**
	 * Load all BED files
	 * @param bedFileNames
	 */
	public void loadIntervals() {
		// Read filter interval file
		if (verbose) Timer.showStdErr("Reading BED file '" + bedFile + "'");

		BedFileIterator bf = new BedFileIterator(bedFile, genome);
		bf.setCreateChromos(true);

		seqChanges = bf.load();
		Collections.sort(seqChanges); // We want the result VCF file to be sorted
		if (verbose) Timer.showStdErr("Total " + seqChanges.size() + " intervals added.");
	}

	/**
	 * Parse command line arguments
	 * @param args
	 */
	@Override
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {

			// Argument starts with '-'?
			if (isOpt(args[i])) {

				if (args[i].equals("-if")) {
					if ((i + 1) < args.length) inOffset = Gpr.parseIntSafe(args[++i]);
				} else if (args[i].equals("-i")) {
					listCommandLine = true;
					inOffset = 1;
				}

			} else {
				if (vcfFile == null) vcfFile = args[i];
				else {
					// Last argument
					if (bedFile == null) {
						if (listCommandLine) seqChanges.add(parsePos(args[i])); // Genomic positions in command line?
						else bedFile = args[i]; // Use BED file
					}
				}
			}
		}

		// Sanity check
		if (vcfFile == null) usage("Missing BED file");
		if (listCommandLine && (seqChanges.size() <= 0)) usage("Missing intervals");
		if (!listCommandLine && (bedFile == null)) usage("Missing VCF file");
	}

	/**
	 * Parse a genomic position
	 * @param pos
	 * @return
	 */
	Variant parsePos(String pos) {
		String recs[] = pos.split(":");
		if (recs.length != 2) usage("Invalid interval '" + pos + "'. Format 'chr:start-end'");
		String chr = recs[0];

		String p[] = recs[1].split("-");
		if (p.length != 2) usage("Invalid interval '" + pos + "'. Format 'chr:start-end'");
		int start = Gpr.parseIntSafe(p[0]) - inOffset;
		int end = Gpr.parseIntSafe(p[1]) - inOffset;

		Chromosome chromo = new Chromosome(genome, 0, 0, 1, chr);
		return new Variant(chromo, start, end, "");
	}

	/**
	 * Load a file compare calls
	 * 
	 * @param fileName
	 */
	@Override
	public void run() {
		if (!listCommandLine) loadIntervals();

		// Read and show header
		VcfFileIterator vcfFileIt = new VcfFileIterator(vcfFile);
		vcfFileIt.iterator().next(); // Read header (by reading first vcf entry)
		addHeader(vcfFileIt);
		String headerStr = vcfFileIt.getVcfHeader().toString();
		if (!headerStr.isEmpty()) System.out.println(headerStr);
		vcfFileIt.close();

		// Open and index file		
		// VcfFileIndexIntervals vf = new VcfFileIndexIntervals(vcfFile);
		FileIndexChrPos fileIndexChrPos = new FileIndexChrPos(vcfFile);

		if (verbose) Timer.showStdErr("Indexing file '" + vcfFile + "'");
		fileIndexChrPos.setVerbose(verbose);
		fileIndexChrPos.setDebug(debug);
		fileIndexChrPos.open();
		fileIndexChrPos.index();
		if (verbose) Timer.showStdErr("Done");

		// Find all intervals
		int scNum = 1;
		for (Variant sc : seqChanges) {
			try {
				if (verbose) Timer.showStdErr(scNum + " / " + seqChanges.size() + "\t\tFinding interval: " + sc.getChromosomeName() + ":" + (sc.getStart() + 1) + "-" + (sc.getEnd() + 1));
				fileIndexChrPos.dump(sc.getChromosomeName(), sc.getStart(), sc.getEnd(), false);
				scNum++;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		if (verbose) Timer.showStdErr("Done");
	}

	public void setInOffset(int inOffset) {
		this.inOffset = inOffset;
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

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar intidx [-if N] [-i] file.vcf ( file.bed | chr:start1-end1 chr:start2-end2 ... chr:startN-endN )");
		System.err.println("Option:");
		System.err.println("\t-if <N>   : Input offset. Default 0 (i.e. zero-based coordinates).");
		System.err.println("\t-i        : Genomic intervals in command line.");
		System.exit(1);
	}
}
