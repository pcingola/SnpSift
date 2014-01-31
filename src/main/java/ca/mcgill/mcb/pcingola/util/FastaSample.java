package ca.mcgill.mcb.pcingola.util;

import ca.mcgill.mcb.pcingola.fileIterator.FastaFileIterator;

/**
 * Get a region from a fasta file
 * @author pablocingolani
 *
 */
public class FastaSample {

	String fastaFile;
	String chrName;
	int start;
	int end;

	String outFile = "/tmp/fastaSample.txt";

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		FastaSample fastaSample = new FastaSample();

		fastaSample.parseArgs(args);
		fastaSample.run();
	}

	public FastaSample() {

	}

	void parseArgs(String[] args) {
		if (args.length != 4) {
			System.err.println("Usage: " + FastaSample.class.getSimpleName() + " fastaFile.fa chromosome start end");
			System.exit(1);
		}

		fastaFile = args[0];
		chrName = args[1];
		start = Gpr.parseIntSafe(args[2]) - 1;
		end = Gpr.parseIntSafe(args[3]);

		// Sanity check
		if (start > end) {
			System.err.println("Error: Start should be before end.");
			System.exit(1);
		}
	}

	public void run() {
		FastaFileIterator ffi = new FastaFileIterator(fastaFile);

		for (String seq : ffi) {
			System.err.println(ffi.getName());
			if (ffi.getName().equalsIgnoreCase(chrName)) {
				// Get substring
				String subSeq = seq.substring(start, end);
				System.out.println(subSeq);

				// Save to file?
				if (outFile != null) {
					System.err.println("Saving sequence to file '" + outFile + "'");
					Gpr.toFile(outFile, subSeq);
				}

				// We are done
				ffi.close();
				break;
			}
		}
	}
}
