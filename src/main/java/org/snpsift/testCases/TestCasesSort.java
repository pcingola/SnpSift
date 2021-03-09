package org.snpsift.testCases;

import java.util.List;

import org.snpeff.util.Log;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpsift.SnpSift;
import org.snpsift.SnpSiftCmdSort;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * SnpSift 'split' test cases
 *
 * @author pcingola
 */
public class TestCasesSort extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = false;

	/**
	 * Vcf Header bug: FORMAT and INFO having the same ID
	 */
	public void test_01() {
		Log.debug("Test");

		String args[] = { "sort", "test/test_sort_01_1.vcf", "test/test_sort_01_2.vcf" };
		SnpSift ss = new SnpSift(args);
		SnpSiftCmdSort ssSort = (SnpSiftCmdSort) ss.cmd();
		ssSort.setQuiet(!verbose);

		// Sort
		List<VcfEntry> ves = ssSort.run(true);

		// Get header
		VcfEntry ve0 = ves.get(0);
		VcfHeader vh = ve0.getVcfFileIterator().getVcfHeader();

		// Make sure the header has both 'DP': INFO and FORMAT (and that neither are 'implicit' headers)
		Assert.assertNotNull(vh.getVcfHeaderInfo("DP"));
		Assert.assertNotNull(vh.getVcfHeaderFormat("DP"));
		Assert.assertFalse(vh.getVcfHeaderInfo("DP").isImplicit());
		Assert.assertFalse(vh.getVcfHeaderFormat("DP").isImplicit());
	}

}
