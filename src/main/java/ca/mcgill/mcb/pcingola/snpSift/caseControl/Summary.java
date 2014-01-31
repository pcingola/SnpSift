package ca.mcgill.mcb.pcingola.snpSift.caseControl;

import java.util.List;

import ca.mcgill.mcb.pcingola.snpEffect.ChangeEffect;
import ca.mcgill.mcb.pcingola.stats.CountByType;
import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

/**
 * Summarize a VCF annotated file
 *  
 * @author pcingola
 */
public class Summary {

	CountByType countByType = new CountByType();

	public void count(String group, Boolean caseControl, ChangeEffect.FunctionalClass functClass, String variantAf, int increment) {
		String key = group + "\t" + caseControl + "\t" + functClass + "\t" + variantAf;
		countByType.inc(key, increment);
	}

	public String toString(List<String> groupNames) {
		StringBuilder sb = new StringBuilder();

		for (String group : groupNames) {
			for (Boolean caseControl : SnpSiftCmdCaseControlSummary.CaseControl) {
				for (ChangeEffect.FunctionalClass functClass : ChangeEffect.FunctionalClass.values()) {
					if (functClass != ChangeEffect.FunctionalClass.NONE) {
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
				for (ChangeEffect.FunctionalClass functClass : ChangeEffect.FunctionalClass.values()) {
					if (functClass != ChangeEffect.FunctionalClass.NONE) {
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
