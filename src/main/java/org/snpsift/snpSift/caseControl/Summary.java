package org.snpsift.snpSift.caseControl;

import java.util.List;

import org.snpeff.snpEffect.VariantEffect;
import org.snpeff.stats.CountByType;
import org.snpeff.vcf.VcfEntry;

/**
 * Summarize a VCF annotated file
 *
 * @author pcingola
 */
public class Summary {

	CountByType countByType = new CountByType();

	public void count(String group, Boolean caseControl, VariantEffect.FunctionalClass functClass, String variantAf, int increment) {
		String key = group + "\t" + caseControl + "\t" + functClass + "\t" + variantAf;
		countByType.inc(key, increment);
	}

	public String toString(List<String> groupNames) {
		StringBuilder sb = new StringBuilder();

		for (String group : groupNames) {
			for (Boolean caseControl : SnpSiftCmdCaseControlSummary.CaseControl) {
				for (VariantEffect.FunctionalClass functClass : VariantEffect.FunctionalClass.values()) {
					if (functClass != VariantEffect.FunctionalClass.NONE) {
						for (VcfEntry.AlleleFrequencyType variantAf : VcfEntry.AlleleFrequencyType.values()) {
							// Get count
							String key = group + "\t" + caseControl + "\t" + functClass + "\t" + variantAf;
							long count = countByType.get(key);

							// Show
							sb.append((count > 0 ? count : "") + "\t");
						}
					}
				}
			}
		}

		return sb.toString();
	}

	public String toStringTitle(List<String> groupNames) {
		StringBuilder sb = new StringBuilder();

		for (String group : groupNames) {
			for (Boolean caseControl : SnpSiftCmdCaseControlSummary.CaseControl) {
				for (VariantEffect.FunctionalClass functClass : VariantEffect.FunctionalClass.values()) {
					if (functClass != VariantEffect.FunctionalClass.NONE) {
						for (VcfEntry.AlleleFrequencyType variantAf : VcfEntry.AlleleFrequencyType.values()) {
							String key = group + "," + (caseControl ? "case" : "control") + "," + functClass + "," + variantAf;
							sb.append(key + "\t");
						}
					}
				}
			}
		}

		return sb.toString();
	}
}
