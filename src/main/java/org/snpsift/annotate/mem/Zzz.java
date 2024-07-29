package org.snpsift.annotate.mem;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpsift.annotate.mem.database.VariantDatabase;
import org.snpsift.util.RandomUtil;
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
		RandomUtil randUtil = new RandomUtil();
		for(int i = 0; i < 10; i++) {
			System.out.println("randAcgt: " + randUtil.randVariant(VariantCategory.MIXED));
		}
		// // var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_small_chr1.vcf";
		// var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_test.vcf";
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
