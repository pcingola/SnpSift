package org.snpsift.testCases;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpeff.vcf.VcfHeader;
import org.snpeff.vcf.VcfHeaderInfo;

import junit.framework.TestCase;

/**
 * Try test cases in this class before adding them to long test cases
 *
 * @author pcingola
 */
public class TestCasesZzz extends TestCase {

	public static boolean debug = false;
	public static boolean verbose = true || debug;

	public TestCasesZzz() {
	}

	/**
	 * Vcf Header bug: FORMAT and INFO having the same ID
	 */
	public void test_36_info_format_id_collision() {
		Gpr.debug("Test");

		VcfFileIterator vcf = new VcfFileIterator("test/test_36_info_format_id_collision_01.vcf");
		for (VcfEntry ve : vcf) {
			if (vcf.isHeadeSection()) {
				VcfHeader vcfHeader = vcf.getVcfHeader();
				System.out.println(vcfHeader);

				VcfHeader newVcfHeader = vcf.getVcfHeader();
				for (VcfHeaderInfo vhi : newVcfHeader.getVcfInfo())
					if (!vhi.isImplicit() && !vcfHeader.hasInfo(vhi)) {
						Gpr.debug("VHI: " + vhi);
						vcfHeader.addInfo(vhi);
					}

			}
		}
	}

}
