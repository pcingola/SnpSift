package org.snpsift.util;

import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;

public class ShowProgress {
	public static final int SHOW_EVERY = 10 * 1000;
	public static final int SHOW_EVERY_LINE = 100 * SHOW_EVERY;

	int showEvery = 100 * 1000;
	int showEveryLine = 100 * showEvery;
	long timeStart;

	public ShowProgress() {
		this(SHOW_EVERY, SHOW_EVERY_LINE);
		timeStart = System.currentTimeMillis();
	}

	public ShowProgress(int showEvery, int showEveryLine) {
		this.showEvery = showEvery;
		this.showEveryLine = showEveryLine;
	}

    public void tick(int count) {
        tick(count, (VcfEntry) null);

    }

	public void tick(int count, VariantVcfEntry varVcf) {
		tick(count, varVcf.getVcfEntry());
	}

	/**
	 * Call this method every iteration
	 */
	public void tick(int count, VcfEntry vcfEntry) {
		if (count % showEvery == 0) {
			if (count % showEveryLine == 0) {
				System.err.println(" " + String.format("%,d", count) + ", " 
                                    + (vcfEntry != null ? vcfEntry.getChromosomeName() + ":" + vcfEntry.getStart() + "\t" : "")
                );
			} else {
				System.err.print('.');
				System.err.flush();
			}
		}
	}

	public double elapsedSec() {
		return ((System.currentTimeMillis() - timeStart) / 1000.0);
	}
}
