package org.snpsift.annotate.mem;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.vcf.VcfEntry;



/**
 * This is a class that reads a VCF file and returns the variants in sorted order.
 * It uses a min heap to keep track of the next variant to be returned.
 * The min heap is populated with the first variant from each chromosome.
 * The next variant to be returned is the one with the smallest position.
 * When a variant is returned, the min heap is updated with the next variant from the same chromosome.
 * The process continues until all variants have been returned.
 */
public class SortedVariantsIterator implements Iterator<Variant>, Iterable<Variant> {

	String vcfFileName;
	VcfFileIterator vcfFileIterator;
	PriorityQueue<Variant> variantsMinHeap; // Min heap to keep track of the next variant to be returned
	String vcfChr; // Chromosome of the last VcfEntry read from the VCF file
	int vcfPos; // Position of the last VcfEntry read from the VCF file
	VcfEntry vcfEntryPending; // A 'pending' vcf entry (i.e. we did not process it beecause it belongs to a different chromosome)

	public SortedVariantsIterator(String vcfFileName) {
		this.vcfFileName = vcfFileName;
		vcfFileIterator = new VcfFileIterator(vcfFileName);
		variantsMinHeap = new PriorityQueue<>(Comparator.comparingInt(variant -> variant.getStart()));
		vcfChr = "";
		vcfPos = -1;
	}

	/**
	 * Add the variants from a VcfEntry to the minHeap
	 */
	private void addVariantsToMinHeap(VcfEntry vcfEntry) {
		for(Variant variant : vcfEntry.variants()) {
			variantsMinHeap.add(variant);
		}
		vcfChr = vcfEntry.getChromosomeName();
		vcfPos = vcfEntry.getStart();
	}

	@Override
	public Iterator<Variant> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !variantsMinHeap.isEmpty() || vcfFileIterator.hasNext();
	}

	@Override
	public Variant next() {
		// If the variant from the minHeap has the same position as the last variant read from the VCF file, return the variant from the minHeap
		if (!variantsMinHeap.isEmpty() && (variantsMinHeap.peek().getStart() <= vcfPos)) {
			return variantsMinHeap.poll();
		}
		// Do we have a pending VcfEntry? This means that the remaining variants in the minHeap are from the previous chromosome, so we need to return them
		if (vcfEntryPending != null) {
			// Return the variants from the minHeap before processing the next chromosome
			if( !variantsMinHeap.isEmpty() ) return variantsMinHeap.poll();
			// No more variants in the heap, we can start processing the next chromosome
			// Add variants from vcfEntryPending to the minHeap
			addVariantsToMinHeap(vcfEntryPending);
			vcfEntryPending = null;
		}
		// If we run out of VcfEntries, then return the variants from the minHeap
		if (!vcfFileIterator.hasNext()) {
			return variantsMinHeap.poll();
		}
		// At this point all edge cases have been handled.
		// Read a VcfEntry from the VCF file, add the variants to the minHeap
		var vcfEntry = vcfFileIterator.next();
		if (!vcfEntry.getChromosomeName().equals(vcfChr)) {
			vcfEntryPending = vcfEntry;
		} else {
			addVariantsToMinHeap(vcfEntry);
		}
		return next(); // Recursively call next() to return the next variant
	}

}
