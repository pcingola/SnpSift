package org.snpsift;

import java.util.HashSet;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Timer;
import org.snpeff.vcf.VcfEntry;

/**
 * Removes INFO fields
 *
 * @author pablocingolani
 */
public class SnpSiftCmdRmInfo extends SnpSift {

	String vcfFileName;
	HashSet<String> infos;
	boolean rmId;

	public SnpSiftCmdRmInfo(String[] args) {
		super(args, "rmInfo");
	}

	@Override
	public void parse(String[] args) {
		infos = new HashSet<String>();
		rmId = false;

		if (args.length == 0) usage(null);

		for (String arg : args) {
			if (isOpt(arg)) {
				if (arg.equals("-id")) rmId = true;
				else usage("Unknown option " + arg);
			} else if (vcfFileName == null) vcfFileName = arg;
			else infos.add(arg);
		}

		// Sanity check
		if ((infos.size() <= 0) && (!rmId)) usage("No INFO field names provided.");
	}

	/**
	 * Analyze the file
	 */
	@Override
	public void run() {
		Timer.showStdErr("Reading STDIN");
		VcfFileIterator vcfFile = new VcfFileIterator(vcfFileName);
		vcfFile.setDebug(debug);

		// Read all vcfEntries
		int entryNum = 1;
		for (VcfEntry vcfEntry : vcfFile) {
			// Show header?
			if (entryNum == 1) {
				String headerStr = vcfFile.getVcfHeader().toString();
				if (!headerStr.isEmpty()) System.out.println(headerStr);
			}

			for (String info : infos)
				vcfEntry.rmInfo(info);

			if (rmId) vcfEntry.setId("");

			// Show entry
			System.out.println(vcfEntry);
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
		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + "" + ".jar rmInfo [options] file.vcf infoField_1 infoField_2 ... infoField_N > file_out.vcf");
		System.exit(1);
	}
}
