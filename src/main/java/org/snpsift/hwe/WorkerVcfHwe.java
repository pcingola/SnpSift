package org.snpsift.hwe;

import org.snpeff.akka.vcfStr.WorkerVcfStr;
import org.snpeff.vcf.VcfEntry;

public class WorkerVcfHwe extends WorkerVcfStr {

	VcfHwe vcfHwe;

	public WorkerVcfHwe() {
		super();
		vcfHwe = new VcfHwe();
	}

	@Override
	public String calculate(VcfEntry vcfEntry) {
		if (vcfEntry == null) return null;
		vcfHwe.hwe(vcfEntry, true);
		return vcfEntry.toString();
	}

}
