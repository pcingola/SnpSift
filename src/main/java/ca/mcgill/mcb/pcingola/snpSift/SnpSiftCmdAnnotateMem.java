package ca.mcgill.mcb.pcingola.snpSift;

import java.io.IOException;

import ca.mcgill.mcb.pcingola.fileIterator.VcfFileIterator;
import ca.mcgill.mcb.pcingola.util.Timer;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Annotate a VCF file with ID from another VCF file (database)
 * 
 * Loads db file in memory, thus it makes no assumption about order.
 * Requires tons of memory
 * 
 * @author pablocingolani
 */
public class SnpSiftCmdAnnotateMem extends SnpSiftCmdAnnotateSorted {

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		SnpSiftCmdAnnotateMem vcfAnnotate = new SnpSiftCmdAnnotateMem(args);
		vcfAnnotate.run();
	}

	public SnpSiftCmdAnnotateMem(String args[]) {
		super(args, "annotateMem");
	}

	/**
	 * Annotate a single vcf entry
	 * @param vcfEntry
	 * @return
	 */
	@Override
	public boolean annotate(VcfEntry vcfEntry) {
		// Anything found? => Annotate
		boolean annotated = false;
		for (int i = 0; i < vcfEntry.getAlts().length; i++) {
			String key = key(vcfEntry, i);

			String id = useId ? dbId.get(key) : null;
			String info = (useInfoField ? dbInfo.get(key) : null);

			// Add ID
			if (id != null) {
				id = uniqueIds(id, vcfEntry.getId());
				if (!id.isEmpty()) {
					if (!vcfEntry.getId().isEmpty()) id = vcfEntry.getId() + ";" + id; // Append if there is already an entry
					vcfEntry.setId(id);
					annotated = true;
				}
			}

			// Add INFO fields
			if (info != null) {
				vcfEntry.addInfo(info);
				annotated = true;
			}
		}

		return annotated;
	}

	/**
	 * Finish up annotation process
	 */
	@Override
	public void endAnnotate() {
		// Nothing to do
	}

	/**
	 * Initialize annotation process
	 * @throws IOException
	 */
	@Override
	public void initAnnotate() throws IOException {
		initInputVcf();
		readDb();
	}

	/**
	 * Read database
	 */
	public void readDb() {
		if (verbose) Timer.showStdErr("Loading database: '" + vcfDbFileName + "'");
		VcfFileIterator dbFile = new VcfFileIterator(vcfDbFileName);

		int count = 1;
		for (VcfEntry vcfDbEntry : dbFile) {

			for (int i = 0; i < vcfDbEntry.getAlts().length; i++) {
				String key = key(vcfDbEntry, i);

				// Add ID
				if (dbId.containsKey(key)) {
					String multipleId = dbId.get(key) + ";" + vcfDbEntry.getId();
					dbId.put(key, multipleId);
				} else dbId.put(key, vcfDbEntry.getId());

				// Add INFO fields
				if (useInfoField) {
					if (infoFields == null) {
						// Add all INFO fields
						dbInfo.put(key, vcfDbEntry.getInfoStr()); // Add INFO field
					} else {
						// Add some INFO fields
						StringBuilder infoSb = new StringBuilder();

						// Add all fields
						for (String fieldName : infoFields) {
							String val = vcfDbEntry.getInfo(fieldName);

							// Any value? => Add
							if (val != null) {
								if (infoSb.length() > 0) infoSb.append(";");
								infoSb.append(fieldName + "=" + val);
							}
						}

						dbInfo.put(key, infoSb.toString());
					}

				}
			}

			count++;
			if (verbose) {
				if (count % SHOW_LINES == 0) System.err.print("\n" + count + "\t.");
				else if (count % SHOW == 0) System.err.print('.');
			}
		}

		// Show time
		if (verbose) {
			System.err.println("");
			Timer.showStdErr("Done. Database size: " + dbId.size());
		}
	}

	/**
	 * Show usage message
	 * @param msg
	 */
	@Override
	public void usage(String msg) {
		if (msg != null) {
			System.err.println("Error: " + msg);
			showCmd();
		}

		showVersion();

		System.err.println("Usage: java -jar " + SnpSift.class.getSimpleName() + ".jar annotateMem [options] database.vcf file.vcf > newFile.vcf\n"//
				+ "Options:\n" //
				+ "\t-id          : Only annotate ID field (do not add INFO field).\n" //
				+ "\t-info <list> : Annotate using a list of info fields (list is a comma separated list of fields). Default: ALL.\n" //
				+ "Note: It is assumed that the database file fits in memory." //
		);
		System.exit(1);
	}

}
