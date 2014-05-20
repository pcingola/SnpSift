package ca.mcgill.mcb.pcingola;

import ca.mcgill.mcb.pcingola.fileIterator.DbNsfpEntry;
import ca.mcgill.mcb.pcingola.fileIterator.DbNsfpFileIterator;
import ca.mcgill.mcb.pcingola.util.Gpr;
import ca.mcgill.mcb.pcingola.vcf.VcfInfoType;

public class Zzz {

	static boolean verbose = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DbNsfpFileIterator db = new DbNsfpFileIterator(Gpr.HOME + "/snpEff/db/dbNSFP/dbNSFP2.4.head.txt.gz");

		for (DbNsfpEntry dbe : db) {

			if (db.getTypes() != null) {
				String fn[] = db.getFieldNamesSorted();
				VcfInfoType vit[] = db.getTypes();
				boolean isMult[] = db.getMultipleValues();
				for (int i = 0; i < fn.length; i++)
					System.out.println("\t" + i + "\t" + fn[i] + "\t" + vit[i] + "\t" + isMult[i]);

				break;
			}
			System.out.println(dbe);

		}
	}

	public Zzz() {
	}

}
