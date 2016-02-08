package org.snpsift.snpSift.gwasCatalog;

import org.snpeff.fileIterator.LineClassFileIterator;

/**
 * Iterate on each line of a GWAS catalog (TXT, tab separated format)
 * 
 * @author pcingola
 */
public class GwasCatalogFileIterator extends LineClassFileIterator<GwasCatalogEntry> {

	public GwasCatalogFileIterator(String fileName) {
		super(fileName, GwasCatalogEntry.class, "dateAddedtoCatalog;pubmedId;firstAuthor;date;journal;link;study;trait;initialSampleSize;replicationSampleSize;region;chrId;chrPos;reportedGene;mappedGene;upstreamGeneId;downstreamGeneId;snpGeneIds;upstreamGeneDistance;downstreamGeneDistance;riskAllele;snps;merged;snpIdCurrent;context;intergenic;riskAlleleFrequency;pValue;pValueMlog;pValueText;orBeta;ci95;platform;cnv;mappedTrait;mappedTraitUri;");
	}
}
