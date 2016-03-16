package org.snpsift;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfGenotype;
import org.snpsift.pedigree.Individual;
import org.snpsift.pedigree.PedigreeDraw;

/**
 * Draws a pedigree using SVG according to a VCF file
 *
 * @author pablocingolani
 */
public class SnpSiftCmdPedShow extends SnpSift {

	String tfamFileName;
	String vcfFileName;
	String outputDir;
	List<String> sampleNames = null;
	HashMap<String, Boolean> chrPos = null;

	public SnpSiftCmdPedShow(String[] args) {
		super(args, "pedShow");
	}

	/**
	 * Colorize individuals according to disease
	 * @param pedigree
	 * @param vcfEntry
	 * @param sampleNames
	 */
	void colorize(PedigreeDraw pedigree, VcfEntry vcfEntry, List<String> sampleNames) {
		int i = 0;

		// Update label as genotype data
		for (VcfGenotype vcfGen : vcfEntry) {
			String sname = sampleNames.get(i);
			Individual ind = pedigree.get(sname);

			if (ind != null) {
				int code = vcfGen.getGenotypeCode();
				if (code == 0) ind.setColor("grey");
				else if (code == 1) ind.setColor("green");
				else if (code == 2) ind.setColor("red");
				else if (code < 0) ind.setColor("black");
			} else System.err.println("Individual '" + sname + "' not found in pedigree");

			i++;
		}
	}

	/**
	 * Draw pedigree
	 * @param pedigree
	 * @param vcfEntry
	 * @param outDir
	 */
	void draw(PedigreeDraw pedigree, VcfEntry vcfEntry, String outDir) {
		// Create output dir
		String dir = outDir + "/" + vcfEntry.getChromosomeName() + "_" + (vcfEntry.getStart() + 1);
		if (verbose) System.err.println("Creating dir '" + dir + "'");
		new File(dir).mkdir();

		// Draw
		String chrPosStr = vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1);
		Timer.showStdErr("Drawing pedigree for '" + chrPosStr + "', output dir: " + dir);
		pedigree.drawSvgByFamily(dir, vcfEntry.toString());
	}

	@Override
	public void parseArgs(String[] args) {
		// Parse command line
		if (args.length < 3) {
			System.err.println("Usage: " + SnpSiftCmdPedShow.class.getSimpleName() + " pedigree.txt file.vcf outDir [chr:pos1 chr:pos2 ... chr:posN]");
			System.exit(-1);
		}

		chrPos = new HashMap<String, Boolean>();
		int idx = 0;
		for (idx = 0; idx < args.length; idx++) {
			String arg = args[idx];

			if (isOpt(arg)) {
				usage("Unknown option '" + arg + "'");
			} else {
				if (tfamFileName == null) tfamFileName = args[idx];
				else if (vcfFileName == null) vcfFileName = args[idx];
				else if (outputDir == null) outputDir = args[idx];
				else chrPos.put(arg, false);
			}
		}
	}

	/**
	 * Run: Main method
	 * @param tfamFile
	 * @param vcfFileName
	 * @param outputDir
	 * @param chrPos
	 */
	@Override
	public boolean run() {
		// Initialize
		PedigreeDraw pedigree = null;
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		// Create directory if it doesn't exist
		new File(outputDir).mkdir();

		//---
		// Read VCF file
		//---
		Timer.showStdErr("Reading vcf file '" + vcfFileName + "'");
		for (VcfEntry vcfEntry : vcfFile) {
			if (sampleNames == null) sampleNames = vcfFile.getSampleNames(); // Get sampleNames

			String chrPosStr = vcfEntry.getChromosomeName() + ":" + (vcfEntry.getStart() + 1);

			// Is this position one of the ones we want to draw?
			if ((chrPos == null) || chrPos.containsKey(chrPosStr)) {

				// Load pedigree and initialize pedigree
				pedigree = new PedigreeDraw(tfamFileName);

				// Colorize according to VCF genotypes
				colorize(pedigree, vcfEntry, sampleNames);

				// Draw pedigrees
				draw(pedigree, vcfEntry, outputDir);

				// Update status
				if (chrPos != null) chrPos.put(chrPosStr, true);
			}
		}
		vcfFile.close();
		Timer.showStdErr("Done");

		// Show missing entries
		if (chrPos != null) {
			for (Map.Entry<String, Boolean> me : chrPos.entrySet())
				if (!me.getValue()) System.err.println("Entry '" + me.getKey() + "' not found.");
		}

		return true;
	}

	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar pedShow pedigree.tfam file.vcf outDir [chr:pos1 chr:pos2 ... chr:posN]");
	}
}
