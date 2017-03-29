package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdIntervals;

import junit.framework.TestCase;

/**
 * SnpSIft intervals
 *
 * @author pcingola
 */
public class TestCasesIntervals extends TestCase {

	public static boolean verbose = false;

	/**
	 * Filter VCF entries
	 */
	public void test_01() {
		Gpr.debug("Test");

		// Run command
		String args[] = { //
				"intervals" //
				, "-i", "test/annotate_5.vcf" // This file has a few VCF entries before 1,000,000 and a few after
				, "test/interval.bed" // BED file intervals cover chr1:1-1,000,000
		};
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdIntervals cmd = (SnpSiftCmdIntervals) snpSift.cmd();
		cmd.setVerbose(verbose);
		cmd.setSuppressOutput(!verbose);
		List<VcfEntry> results = cmd.run(true);

		// Check results
		for (VcfEntry ve : results)
			if (ve.getStart() > 1000000) throw new RuntimeException("This entry should be filtered out!\n\t" + ve);

	}

	/**
	 * Filter VCF entries
	 */
	public void test_02() {
		Gpr.debug("Test");

		// Run command
		String args[] = { //
				"intervals"//
				, "-x" // Exclude entries in the BED file intervals
				, "-i", "test/annotate_5.vcf" // This file has a few VCF entries before 1,000,000 and a few after
				, "test/interval.bed" // BED file intervals cover chr1:1-1,000,000
		};
		SnpSift snpSift = new SnpSift(args);
		SnpSiftCmdIntervals cmd = (SnpSiftCmdIntervals) snpSift.cmd();
		cmd.setVerbose(verbose);
		cmd.setSuppressOutput(!verbose);
		List<VcfEntry> results = cmd.run(true);

		// Check results
		for (VcfEntry ve : results)
			if (ve.getStart() < 1000000) throw new RuntimeException("This entry should be filtered out!\n\t" + ve);

	}
}
