package org.snpsift.annotate.mem;

import java.nio.charset.StandardCharsets;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;
import org.snpsift.annotate.mem.arrays.StringArray;
import org.snpsift.annotate.mem.database.VariantDatabase;
import org.snpsift.util.ShowProgress;


/***
 * Test: This class loaads a "database" VCF file and then annotates another VCF file.
 * 
 * 
 * Chromosome aliases file: http://hgdownload.soe.ucsc.edu/goldenPath/hg38/database/chromAlias.txt.gz
 * 
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
		// DbSnp database
		// var databaseFileName = Gpr.HOME + "/snpEff/wgs_test/db/dbSnp.151.vcf";
		// var fields = new String[] { "RS", "CAF" };

		// // Cosmic database
		// var databaseFileName = Gpr.HOME + "/snpEff/wgs_test/db/cosmic-v92.vcf";
		// var fields = new String[] { "CNT", "LEGACY_ID" };

		// Clinvar database
		var databaseFileName = Gpr.HOME + "/snpEff/wgs_test/db/clinvar.vcf";
		var fields = new String[] { "CLNSIG", "CLNDN" };
		// // Clinvar database
		// var databaseFileName = Gpr.HOME + "/snpEff/wgs_test/db/clinvar.chr1.vcf";
		// var fields = new String[] { "CLNDN" };
		
		// var inputVcf = Gpr.HOME + "/snpEff/z.vcf";


		// StringArray sa = new StringArray(379, 144270);
		// String fieldname = "CLNDN";
		// int count = 0, size = 0, offset = 0;
		// StringBuffer sb = new StringBuffer();
		// for(VcfEntry vcfEntry : new VcfFileIterator(databaseFileName)) {
		// 	for(var variant : vcfEntry.variants()) {
		// 		if( VariantCategory.of(variant) == VariantCategory.MIXED) {
		// 			count++;
		// 			String value = vcfEntry.getInfo(fieldname);
		// 			sa.add(value);
		// 			// Append to a string buffer
		// 			if(value == null) value = "";
		// 			var valueUtf8 = new String(value.getBytes(StandardCharsets.UTF_8));
		// 			offset += value.getBytes(StandardCharsets.UTF_8).length + 1;
		// 			sb.append(valueUtf8 + "\n");
		// 			size += (valueUtf8 != null ? valueUtf8.length() : 0);
		// 			System.out.println( //
		// 								(sb.length() != sa.getOffset() ? "ERROR\t" : "") //
		// 								+ count //
		// 								+ "\tvalue='" + value + "'" //
		// 								+ "\tvalueUtf8='" + valueUtf8 + "'" //
		// 								+ "\tsb.len: " + sb.length() //
		// 								+ "\tsa.offset: " + sa.getOffset() //
		// 								+ "\toffset: " + offset //
		// 								);
		// 		}
		// 	}
		// }
		// System.out.println("Count: " + count + ", size: " + size + ", sb.length: " + sb.length());

		// count = 0;
		// size = 0;
		// for(VariantVcfEntry varVcf: new SortedVariantsVcfIterator(databaseFileName)) {
		// 	if( VariantCategory.of(varVcf) == VariantCategory.MIXED) {
		// 		count++;
		// 		String value = varVcf.getVcfEntry().getInfo(fieldname);
		// 		size += (value != null ? value.length() : 0);
		// 	}
		// }
		// System.out.println("Count: " + count + ", size: " + size);


		// Create the database from a VCF file
		Zzz zzz = new Zzz(databaseFileName, fields);
		zzz.create();
		// // // zzz.annotate(inputVcf);
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
			variantDatabase.annotate(vcfEntry);
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
		var dbDir = databaseFileName + '_' + VariantDatabase.VARIANT_DATAFRAME_EXT;
		variantDatabase.create(databaseFileName, dbDir);
		System.out.println(variantDatabase);
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
