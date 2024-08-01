package org.snpsift.annotate.mem;

import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;



/***
 * Test: This class loaads a "database" VCF file and then annotates another VCF file.
 * 
 * 
 * Chromosome aliases file: http://hgdownload.soe.ucsc.edu/goldenPath/hg38/database/chromAlias.txt.gz
 * 
 */
public class Zzz {

	public static final int CHR1_NUM_ENTRIES = 86844566;
	
	/**
	 * Main
	 */
	public static void main(String[] args) {
		// VariantDatabases vd = new VariantDatabases();
		// vd.add(Gpr.HOME + "/snpEff/wgs_test/db/dbSnp.151.vcf.gz", List.of("RS", "CAF"));
		// vd.add(Gpr.HOME + "/snpEff/wgs_test/db/cosmic-v92.vcf.gz", List.of("CNT", "LEGACY_ID"));
		// vd.add(Gpr.HOME + "/snpEff/wgs_test/db/clinvar.vcf.gz", List.of("CLNSIG", "CLNDN"));
		// vd.add(Gpr.HOME + "/snpEff/wgs_test/db/gnomad.genomes.v4.1.sites.chr21.vcf.gz", List.of("AF", "AF_grpmax", "AC_grpmax", "AN_grpmax"));
		// vd.create();

		// // Annotate
		// var inputVcf = Gpr.HOME + "/snpEff/wgs_test/test.vcf";
		// vd.annotate(inputVcf);
	}

	public Zzz() {
	}

	public void benchmarkSortedVariantsVcfIterator(String databaseFileName) {
		int showEvery = 100000;

		// Time how long it takes to read the VCF file
		var startTime = System.currentTimeMillis();
		int count = 0, pos = -1, countUnsorted = 0;
		String chrPrev = "";
		SortedVariantsVcfIterator sortedVariantsIterator = new SortedVariantsVcfIterator(databaseFileName);
		for(var varVcf : sortedVariantsIterator) {
			// Check if they are sorted
			if(pos > varVcf.getStart() && chrPrev.equals(varVcf.getChromosomeName())) countUnsorted++;
			chrPrev = varVcf.getChromosomeName();
			pos = varVcf.getStart();
			// Show progress
			count++;
			if(count % showEvery == 0) {
				if(count % (showEvery * 100) == 0) System.out.println("SORT Count: " + count + ", unsorted: " + countUnsorted + ", chr: " + varVcf.getChromosomeName() + ", pos: " + varVcf.getStart());
				else {
					System.out.print(".");
					System.out.flush();
				}
			}
		}
		var endTime = System.currentTimeMillis();
		System.out.println("SORT Done. Count: " + count + ", unsorted: " + countUnsorted + ", time: " + (endTime - startTime) / 1000.0 + " sec");	
	}

	public void benchmarkVcfIterator(String databaseFileName) {
		int showEvery = 100000;

		// Time how long it takes to read the VCF file
		var startTime = System.currentTimeMillis();
		int count = 0, pos = -1, countUnsorted = 0;
		String chrPrev = "";
		VcfFileIterator vcfFileIterator = new VcfFileIterator(databaseFileName);
		for(var vcfEntry : vcfFileIterator) {
			for(var variant : vcfEntry.variants()) {
				// Check if they are sorted
				if(pos > variant.getStart() && chrPrev.equals(variant.getChromosomeName())) countUnsorted++;
				chrPrev = variant.getChromosomeName();
				pos = variant.getStart();
				// Show progress
				count++;
				if(count % showEvery == 0) {
					if(count % (showEvery * 100) == 0) System.out.println("VCF Count: " + count + ", unsorted: " + countUnsorted + ", chr: " + variant.getChromosomeName() + ", pos: " + variant.getStart());
					else {
						System.out.print(".");
						System.out.flush();
					}
				}
			}
		}
		var endTime = System.currentTimeMillis();
		System.out.println("VCF Done. Count: " + count + ", unsorted: " + countUnsorted + ", time: " + (endTime - startTime) / 1000.0 + " sec\n");
	}
}
