package org.snpsift.annotate.mem;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpsift.annotate.mem.database.VariantDatabase;
import org.snpsift.util.ShowProgress;


/***
 * Test: This class loaads a "database" VCF file and then annotates another VCF file.
 */
public class Zzz {

	public static final int CHR1_NUM_ENTRIES = 86844566;
	
	String databaseFileName;	// Database file
	String[] fields;	// Fields to extract
	VariantDatabase variantDatabase;

	/**
	 * Main
	 */
	public static void main(String[] args) {
		var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/GCF_000001405.40.gz";
		// var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_small_chr1.vcf";
		// var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_test.vcf";
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


		// Time how long it takes to read the Sorted VCF file
		startTime = System.currentTimeMillis();
		SortedVariantsVcfIterator sortedVariantsIterator = new SortedVariantsVcfIterator(databaseFileName);
		chrPrev = "";
		count = 0;
		pos = -1;
		countUnsorted = 0;
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
		endTime = System.currentTimeMillis();
		System.out.println("SORT Done. Count: " + count + ", unsorted: " + countUnsorted + ", time: " + (endTime - startTime) / 1000.0 + " sec");

		// var fields = new String[] { "RS" };

		// // Create the database from a VCF file
		// Zzz zzz = new Zzz(databaseFileName, fields);
		// zzz.create();
	}

	public Zzz(String dbFile, String[] fields) {
		databaseFileName = dbFile;
		this.fields = fields;
	}

	/**
	 * Annotate a VCF file
	 */
	void annotate(String vcfInput) {
		System.out.println("Annotating file: " + vcfInput);
		var found = 0;
		var count = 0;
		var progress = new ShowProgress();
		for(var vcfEntry : new VcfFileIterator(vcfInput)) {
			count++;
			progress.tick(count, vcfEntry);
		}
		System.out.println("Done. Found: " + found + " out of " + count + " entries.");
	}

	/**
	 * Create database
	 */
	public void create() {
		// Load data
		variantDatabase = new VariantDatabase(fields);
		var dbDir = databaseFileName + '_' + VariantDatabase.DB_EXT;
		variantDatabase.createDb(databaseFileName, dbDir);
	}

}
