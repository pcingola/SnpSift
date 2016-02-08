package org.snpsift.snpSift.gwasCatalog;

/**
 * Entry from a GWAS-catalog
 *
 * References:
 * 			http://www.genome.gov/gwastudies/#download
 *  		http://www.genome.gov/Pages/About/OD/OPG/GWAS%20Catalog/Tab_delimited_column_descriptions_09_27.pdf
 *
 * @author pablocingolani
 *
 */
public class GwasCatalogEntry {

	public String dateAddedtoCatalog;
	public String pubmedId;
	public String firstAuthor;
	public String date;
	public String journal;
	public String link;
	public String study;
	public String trait;
	public String initialSampleSize;
	public String replicationSampleSize;
	public String region;
	public String chrId;
	public int chrPos;
	public String reportedGene;
	public String mappedGene;
	public String upstreamGeneId;
	public String downstreamGeneId;
	public String snpGeneIds;
	public String upstreamGeneDistance;
	public String downstreamGeneDistance;
	public String riskAllele;
	public String snps;
	public String merged;
	public String snpIdCurrent;
	public String context;
	public String intergenic;
	public String riskAlleleFrequency;
	public double pValue;
	public String pValueMlog;
	public String pValueText;
	public String orBeta;
	public String ci95;
	public String platform;
	public String cnv;
	public String mappedTrait;
	public String mappedTraitUri;

	/**
	 * Trait as a 'code' (only alpha-num chars)
	 */
	public String getTraitCode() {
		String traitCode = trait.trim().replaceAll("\\W+", "_");

		// Remove leading and trailing '_'
		while (traitCode.startsWith("_"))
			traitCode = traitCode.substring(1);

		while (traitCode.endsWith("_"))
			traitCode = traitCode.substring(0, traitCode.length() - 1);

		return traitCode;
	}

	@Override
	public String toString() {
		return "dateAddedtoCatalog     : " + dateAddedtoCatalog + "\n" //
				+ "pubmedId               : " + pubmedId + "\n" //
				+ "firstAuthor            : " + firstAuthor + "\n" //
				+ "date                   : " + date + "\n" //
				+ "journal                : " + journal + "\n" //
				+ "link                   : " + link + "\n" //
				+ "study                  : " + study + "\n" //
				+ "trait                  : " + trait + "\n" //
				+ "initialSampleSize      : " + initialSampleSize + "\n" //
				+ "replicationSampleSize  : " + replicationSampleSize + "\n" //
				+ "region                 : " + region + "\n" //
				+ "chrId                  : " + chrId + "\n" //
				+ "chrPos                 : " + chrPos + "\n" //
				+ "reportedGene           : " + reportedGene + "\n" //
				+ "mappedGene             : " + mappedGene + "\n" //
				+ "upstreamGeneId         : " + upstreamGeneId + "\n" //
				+ "downstreamGeneId       : " + downstreamGeneId + "\n" //
				+ "snpGeneIds             : " + snpGeneIds + "\n" //
				+ "upstreamGeneDistance   : " + upstreamGeneDistance + "\n" //
				+ "downstreamGeneDistance : " + downstreamGeneDistance + "\n" //
				+ "riskAllele             : " + riskAllele + "\n" //
				+ "snps                   : " + snps + "\n" //
				+ "merged                 : " + merged + "\n" //
				+ "snpIdCurrent           : " + snpIdCurrent + "\n" //
				+ "context                : " + context + "\n" //
				+ "intergenic             : " + intergenic + "\n" //
				+ "riskAlleleFrequency    : " + riskAlleleFrequency + "\n" //
				+ "pValue                 : " + pValue + "\n" //
				+ "PvalueMlog             : " + pValueMlog + "\n" //
				+ "pValueText             : " + pValueText + "\n" //
				+ "orBeta                 : " + orBeta + "\n" //
				+ "ci95                   : " + ci95 + "\n" //
				+ "platform               : " + platform + "\n" //
				+ "cnv                    : " + cnv + "\n" //
				+ "mappedTrait            : " + mappedTrait + "\n" //
				+ "mappedTraitUri         : " + mappedTraitUri + "\n" //
				;
	}

}
