package org.snpsift.annotate.mem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.database.VariantDatabase;
import org.snpsift.annotate.mem.variantTypeCounter.VariantTypeCounters;
import org.snpsift.util.ShowProgress;


/***
 * Test: This class loaads a "database" VCF file and then annotates another VCF file.
 */
public class Zzz {

	public static final int CHR1_NUM_ENTRIES = 86844566;
	
	String databaseFileName;	// Database file
	String[] fields;	// Fields to extract
	VariantDatabase variantDatabasePerChr;

	/**
	 * Main
	 */
	public static void main(String[] args) {
		// var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_small_chr1.vcf";
		var databaseFileName = Gpr.HOME + "/snpEff/db/GRCh38/dbSnp/dbsnp_test.vcf";
		var fields = new String[] { "RS" };

		// Load the database
		Zzz zzz = new Zzz(databaseFileName, fields);
		zzz.create();	// Load the "database" VCF file
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
	 * Count the number of variants in a VCF file
	 */
	private VariantTypeCounters countVcfVariants() {
		System.out.println("Counting number of variants in " + databaseFileName);
		var vcfFile = new VcfFileIterator(databaseFileName);
		VariantTypeCounters vc = new VariantTypeCounters();
		var progress = new ShowProgress();
		int i = 0;
		for (VcfEntry vcfEntry : vcfFile) {
			vc.count(vcfEntry);
			progress.tick(i, vcfEntry); // Show progress
			i++;
		}
		System.out.println(vc.toString());
		return vc;
	}

	/**
	 * Create index from VCF "database" file
	 */
	public void create() {
		// First, count the number of entries in the VCF file
		VariantTypeCounters vc = countVcfVariants();
		// Load data
		variantDatabasePerChr = new VariantDatabase(vc, fields);
		variantDatabasePerChr.createDb(databaseFileName);
	}

	/**
	 * Test: Find all positions
	 *  Check that all positions extracted from zzz.positions are found
	 */
	void testFindAll() {
		// // Time the total search time
		// long startTime = System.currentTimeMillis();
		// System.out.println("Testing: Find all positions...");
		// for (int i = 0; i < entriesToCheck; i++) {
		// 	int p = posIndex.get(i);
		// 	int idx = posIndex.indexOf(p);
		// 	if (idx < 0) throw new RuntimeException("ERROR: Position not found: " + p + "\tIndex: " + idx);
		// }
		// long endTime = System.currentTimeMillis();
		// double timePerEntry = ((double) (endTime - startTime)) / entriesToCheck / 1000.0;
		// System.out.println("All positions found in " + (endTime - startTime) + "ms" + "\tTime per entry: " + timePerEntry + " second");
	}
}
