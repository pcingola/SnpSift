package org.snpsift;

import java.util.LinkedList;
import java.util.List;

import org.snpeff.collections.AutoHashMap;
import org.snpeff.fileIterator.LineFileIterator;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Chromosome;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;

import gnu.trove.set.hash.TIntHashSet;

/**
 * Filter using CHROM:POS only
 *
 *
 * @author pablocingolani
 */
public class SnpSiftCmdFilterChrPos extends SnpSift {

	AutoHashMap<String, TIntHashSet> chrpos;
	String chrPosFile;
	int countChrPosLines = 0, countChrPosAdded = 0, countVcfFiltered = 0;

	public SnpSiftCmdFilterChrPos() {
		super();
		chrpos = new AutoHashMap<String, TIntHashSet>(new TIntHashSet());
	}

	public SnpSiftCmdFilterChrPos(String args[]) {
		super(args);
		chrpos = new AutoHashMap<String, TIntHashSet>(new TIntHashSet());
	}

	/**
	 * Filter this line?
	 * @return true if the filter is satisfied, false otherwise
	 */
	protected boolean filter(VcfEntry vcfEntry) {
		return hasPos(vcfEntry.getChromosomeName(), vcfEntry.getStart());
	}

	/**
	 * Is this 'chr:pos' entry present?
	 */
	boolean hasPos(String chr, int pos) {
		TIntHashSet posSet = chrpos.get(chr);
		if (posSet == null) return false;
		return posSet.contains(pos);
	}

	/**
	 * Load cho:pos set
	 */
	void loadChrPos() {
		if (verbose) Log.info("Reading 'chr:pos' from file '" + chrPosFile + "'.");
		LineFileIterator lfi = new LineFileIterator(chrPosFile);

		for (String line : lfi) {
			countChrPosLines++;
			if (line.startsWith("#")) continue;

			// Parse line
			String fields[] = line.split("\t", 3);
			String chr = fields[0];
			int pos = Gpr.parseIntSafe(fields[1]);
			if (pos <= 0) continue;

			// Add entry to list
			pos--; // Convert to zero based coordinates
			chr = Chromosome.simpleName(chr);
			if (chrpos.getOrCreate(chr).add(pos)) countChrPosAdded++;
		}

		if (verbose) Log.info("Done.\n\t\tLines         : " + countChrPosLines + "\n\t\tEntries added : " + countChrPosAdded);
	}

	/**
	 * Parse command line options
	 */
	@Override
	public void parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Argument starts with '-'?
			if (isOpt(arg)) {
				// No command line options available for this command
			} else if (chrPosFile == null) chrPosFile = arg;
			else if (vcfInputFile == null) vcfInputFile = arg;
			else usage("Unknown parameter '" + arg + "'");
		}

		if (chrPosFile == null) usage("No chrpos.txt file provided.");
	}

	@Override
	public boolean run() {
		run(false);
		return true;
	}

	/**
	 * Run filter
	 * @param createList : If true, create a list with the results. If false, show results on STDOUT
	 * @return If 'createList' is true, return a list containing all vcfEntries that passed the filter. Otherwise return null.
	 */
	public List<VcfEntry> run(boolean createList) {
		// Initialize
		LinkedList<VcfEntry> passEntries = (createList ? new LinkedList<VcfEntry>() : null);

		// Load chrpos entries
		loadChrPos();

		// Open and read entries
		showVcfHeader = !createList;
		VcfFileIterator vcfFile = openVcfInputFile();
		annotateInit(vcfFile);
		int countVcfLines = 0;
		for (VcfEntry vcfEntry : vcfFile) {
			countVcfLines++;
			processVcfHeader(vcfFile);

			// Store entries? (for debugging purposes)
			if (filter(vcfEntry)) {
				print(vcfEntry);
				countVcfFiltered++;
				if (passEntries != null) passEntries.add(vcfEntry);
			}

			if (verbose) Gpr.showMark(countVcfLines, SHOW_EVERY_VCFLINES);
		}

		annotateFinish(vcfFile);

		if (verbose) Log.info("Done filtering." //
				+ "\n\t\tVCF lines                : " + countVcfLines //
				+ "\n\t\tVCF lines passing filter : " + countVcfFiltered //
				+ "\n\t\tChrPos lines             : " + countChrPosLines //
				+ "\n\t\tChrPos entries added     : " + countChrPosAdded //
		);

		return passEntries;
	}

	/**
	 * Usage message
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.out.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar filter [options] chrpos.txt [input.vcf]");
		System.err.println("\nFile 'chrpos.txt' is parsed as follows:");
		System.err.println("\t- First two (tab-separated) columns are 'chr' and 'pos'.");
		System.err.println("\t- Other columns are ignored.");
		System.err.println("\t- Lines starting with '#' are ignored.");
		System.err.println("\t- Positions are one-based (same as in VCF files).");
		System.exit(-1);
	}

}
