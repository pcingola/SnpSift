package ca.mcgill.mcb.pcingola.snpSift.hwe;

import ca.mcgill.mcb.pcingola.akka.vcfStr.WorkerVcfStr;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

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
