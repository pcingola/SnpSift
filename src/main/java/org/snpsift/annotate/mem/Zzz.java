package org.snpsift.annotate.mem;

import java.util.ArrayList;
import java.util.List;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
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
	
	/**
	 * Main
	 */
	public static void main(String[] args) {
		// DbSnp database
		var databaseFileNameDnSnp = Gpr.HOME + "/snpEff/wgs_test/db/dbSnp.151.vcf";
		var fieldsDbSnp = new String[] { "RS", "CAF" };
		var createDbSnp = false;
		
		// Cosmic database
		var databaseFileNameCosmic = Gpr.HOME + "/snpEff/wgs_test/db/cosmic-v92.vcf";
		var fieldsCosmic = new String[] { "CNT", "LEGACY_ID" };
		var createCosmic = false;

		// Clinvar database
		var databaseFileNameClinvar = Gpr.HOME + "/snpEff/wgs_test/db/clinvar.vcf";
		var fieldsClinvar = new String[] { "CLNSIG", "CLNDN" };
		var createClinvar = false;

		// Gnomad database
		var databaseFileNameGnomad = Gpr.HOME + "/snpEff/wgs_test/db/gnomad.genomes.v4.1.sites.chr21.vcf";
		var fieldsGnomad = new String[] { "AF", "AF_grpmax", "AC_grpmax", "AN_grpmax" };
		var createGnomad = false;

		// Annotate
		var annotate = true;
		boolean emptyIfNotFound = true;
		var inputVcf = Gpr.HOME + "/snpEff/wgs_test/test.vcf";

		// Create databases
		Zzz zzz;
		if( createDbSnp ) {
			// DbSnp database
			zzz = new Zzz();
			zzz.create(databaseFileNameDnSnp, fieldsDbSnp);
		}
		if( createCosmic ) {
			// Cosmic database
			zzz = new Zzz();
			zzz.create(databaseFileNameCosmic, fieldsCosmic);
		}
		if( createClinvar ) {
			// Clinvar database
			zzz = new Zzz();
			zzz.create(databaseFileNameClinvar, fieldsClinvar);
		}
		if( createGnomad ) {
			// Gnomad database
			zzz = new Zzz();
			zzz.create(databaseFileNameGnomad, fieldsGnomad);
		}

		if( annotate ) {
			// Annotate the database from a VCF file
			zzz = new Zzz();
			String[] dbs = { databaseFileNameDnSnp, databaseFileNameCosmic, databaseFileNameClinvar, databaseFileNameGnomad };
			// String[] dbs = { databaseFileNameClinvar };
			zzz.annotate(dbs, inputVcf, emptyIfNotFound);
		}
	}

	public Zzz() {
	}

	/**
	 * Annotate a VCF file
	 */
	void annotate(String[] dbs, String vcfInput, boolean emptyIfNotFound) {
		Log.info("Annotating file: " + vcfInput);

		// Create a list of databases to use
		List<VariantDatabase> variantDatabases = new ArrayList<>();
		for(String dbFile: dbs) {
			String dbDir = dbFile + '_' + VariantDatabase.VARIANT_DATAFRAME_EXT;
			variantDatabases.add(new VariantDatabase(dbDir, emptyIfNotFound));
		}

		// Annotate each VcfEntry
		int found = 0, countVcfEntries = 0, annotationsAdded = 0;
		var progress = new ShowProgress();
		for(var vcfEntry : new VcfFileIterator(vcfInput)) {
			var annotations = 0;
			for(var variantDatabase : variantDatabases) {
				annotations += variantDatabase.annotate(vcfEntry);
			}

			// Update counters
			countVcfEntries++;
			annotationsAdded += annotations;
			if( annotations > 0 ) found++;
			
			// Show progress
			progress.tick(countVcfEntries, vcfEntry);
		}

		// Show stats
		var foundPerc = (100.0 * found) / ((double) countVcfEntries);
		Log.info("Done. Processed: " + String.format("%,d", countVcfEntries) + " VCF entries" //
					+ ", found annotations for " + String.format("%,d", found) //
					+ String.format(" ( %.1f %% )", foundPerc) //
					+ ", added " + String.format("%,d", annotationsAdded) + " annotations." //
					+ ", elapsed time: " + progress.elapsedSec() //
		);
	}

	/**
	 * Create database
	 */
	public void create(String databaseFileName, String[] fields) {
		// Load data
		var variantDatabase = new VariantDatabase(fields);
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
