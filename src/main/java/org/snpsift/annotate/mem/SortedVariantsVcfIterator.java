package org.snpsift.annotate.mem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.vcf.VariantVcfEntry;
import org.snpeff.vcf.VcfEntry;


/**
 * This is a class that reads a VCF file and returns the variants in sorted order.
 * 
 * The SortedVariantsVcfIterator class provides an iterator to traverse through
 * VCF (Variant Call Format) entries in a sorted manner. It implements both
 * Iterator and Iterable interfaces for VariantVcfEntry objects.
 *
 * This class reads VCF entries from a file or a string containing VCF lines,
 * and ensures that the variants are returned in a sorted order based on their
 * positions. It uses a priority queue (min-heap) to keep track of the next
 * variant to be returned.
 *
 * How it works:
 * It uses a min heap to keep track of the next variant to be returned.
 * The min heap is populated with the first variant from each chromosome.
 * The next variant to be returned is the one with the smallest position.
 * When a variant is returned, the min heap is updated with the next variant from the same chromosome.
 * The process continues until all variants have been returned.
 * 
 * The class handles edge cases such as:
 * - Variants from different chromosomes.
 * - Ensuring that the VCF file is sorted.
 * - Handling pending VcfEntries that belong to a different chromosome.
 *
 * Limitations:
 * - The VCF file must be sorted by chromosome and position. If the file is not
 *   sorted, a RuntimeException will be thrown.
 * - The class assumes that the input VCF lines or file are in a valid format.
 * - The class does not handle multi-threaded access and should be used in a
 *   single-threaded context.
 */
public class SortedVariantsVcfIterator implements Iterator<VariantVcfEntry>, Iterable<VariantVcfEntry> {

	/**
     * Create a VcfFileIterator from a string containig VCF lines
     */
    public static SortedVariantsVcfIterator lines2SortedVariantsVcfIterator(String vcfLines) {
        try (var bais = new ByteArrayInputStream(vcfLines.getBytes("UTF-8"))) {
            InputStreamReader isr = new InputStreamReader(bais);
            BufferedReader br = new BufferedReader(isr);
            return new SortedVariantsVcfIterator(br);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	protected String vcfFileName;
	protected VcfFileIterator vcfFileIterator;
	protected PriorityQueue<VariantVcfEntry> variantsMinHeap; // Min heap to keep track of the next variant to be returned
	protected String vcfChr; // Chromosome of the last VcfEntry read from the VCF file
	protected int vcfPos; // Position of the last VcfEntry read from the VCF file
	protected VcfEntry vcfEntryPending; // A 'pending' vcf entry (i.e. we did not process it beecause it belongs to a different chromosome)

	public SortedVariantsVcfIterator(String vcfFileName) {
		this.vcfFileName = vcfFileName;
		vcfFileIterator = new VcfFileIterator(vcfFileName);
		variantsMinHeap = new PriorityQueue<>(Comparator.comparingInt(varvcf -> varvcf.getStart()));
		vcfChr = "";
		vcfPos = -1;
	}

	public SortedVariantsVcfIterator(BufferedReader reader) {
		vcfFileIterator = new VcfFileIterator(reader);
		variantsMinHeap = new PriorityQueue<>(Comparator.comparingInt(varvcf -> varvcf.getStart()));
		vcfChr = "";
		vcfPos = -1;
	}

	/**
	 * Add the variants from a VcfEntry to the minHeap
	 */
	private void addVariantsVcfToMinHeap(VcfEntry vcfEntry) {
		variantsMinHeap.addAll(VariantVcfEntry.factory(vcfEntry));	
		vcfChr = vcfEntry.getChromosomeName();
		vcfPos = vcfEntry.getStart();
	}

	public void close() {
		vcfFileIterator.close();
	}

	@Override
	public Iterator<VariantVcfEntry> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !variantsMinHeap.isEmpty() || vcfFileIterator.hasNext();
	}

	@Override
	public VariantVcfEntry next() {
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
			addVariantsVcfToMinHeap(vcfEntryPending);
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
			if( vcfEntry.getStart() < vcfPos ) throw new RuntimeException("VCF file is not sorted. Position: " + vcfEntry.getStart() + " is before " + vcfPos + "\n" + vcfEntry);
			addVariantsVcfToMinHeap(vcfEntry);
		}
		return next(); // Recursively call next() to return the next variant
	}

}
