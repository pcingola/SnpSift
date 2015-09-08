package ca.mcgill.mcb.pcingola;

import ca.mcgill.mcb.pcingola.snpSift.annotate.IntervalFile;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.util.Timer;

public class Zzz {

	static boolean verbose = false;

	public static void main(String[] args) {
		String fileName = Gpr.HOME + "/snpEff/cosmic.vcf";

		Timer.showStdErr("Indexing file:" + fileName);
		IntervalFile vcf = new IntervalFile(fileName);
		vcf.index();
		Timer.showStdErr("Done.\n" + vcf);
	}

}
