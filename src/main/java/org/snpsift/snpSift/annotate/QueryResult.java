package org.snpsift.snpSift.annotate;

import java.util.Collection;

import org.snpeff.interval.Variant;
import org.snpeff.vcf.VariantVcfEntry;

/**
 * A database query and a result.
 * This is just a tuple with proper names
 *
 * @author pcingola
 */
public class QueryResult {
	public final Variant variant;
	public final Collection<VariantVcfEntry> results;

	public QueryResult(Variant variant, Collection<VariantVcfEntry> results) {
		this.variant = variant;
		this.results = results;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Variant: " + variant + "\n");
		for (VariantVcfEntry varVe : results)
			sb.append("\tVariantVcfEntry: " + varVe + "\n");

		return sb.toString();
	}
}