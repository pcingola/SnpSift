package org.snpsift.annotate.mem;

import org.snpeff.interval.Variant;

public enum VariantCategory {
	SNP_A, SNP_C, SNP_G, SNP_T, INS, DEL, MNP, MIXED, OTHER;

	public static VariantCategory of(Variant variant) {
		if(variant.isSnp()) {
			char alt = variant.getAlt().toUpperCase().charAt(0);
			switch (alt) {
				case 'A':
					return SNP_A;
				case 'C':
					return SNP_C;
				case 'G':
					return SNP_G;
				case 'T':
					return SNP_T;
				default:
					throw new RuntimeException("Unknown SNP: " + variant.getAlt() + "\t" + variant.toString());
			} 
		} else if(variant.isIns()) {
			return INS;
		} else if(variant.isDel()) {
			return DEL;
		} else if(variant.isMnp()) {
			return MNP;
		} else if(variant.isMixed()) {
			return MIXED;
		} else {
			return OTHER;
		}
	}

	public static int size() {
		return VariantCategory.values().length;
	}
}