package ca.mcgill.mcb.pcingola.snpSift.testCases;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalog;
import ca.mcgill.mcb.pcingola.snpSift.gwasCatalog.GwasCatalogEntry;

/**
 * Test GWAS catalog classes
 * 
 * @author pcingola
 */
public class TestCasesGwasCatalog extends TestCase {

	public static boolean verbose = false;

	public void test_01() {
		// Load catalog
		GwasCatalog gwasCatalog = new GwasCatalog("./gwasCatalog/gwascatalog.txt.gz");

		// Search by chr:pos
		List<GwasCatalogEntry> list = gwasCatalog.get("20", 1759590 - 1);
		Assert.assertEquals(list.get(0).snps, "rs6080550");

		// Search by RS number
		list = gwasCatalog.getByRs("rs6080550");
		Assert.assertEquals(list.get(0).snps, "rs6080550");
	}

}
