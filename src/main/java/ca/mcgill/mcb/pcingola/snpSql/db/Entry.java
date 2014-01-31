package ca.mcgill.mcb.pcingola.snpSql.db;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import ca.mcgill.mcb.pcingola.vcf.VcfEntry;

@Entity
public class Entry extends Pojo<Entry> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String chrom;
	int pos;
	String vcfId;
	String ref, alt;
	double qual;
	String filter;

	@OneToMany(mappedBy = "entry")
	Set<Tuple> tuples;

	@OneToMany(mappedBy = "entry")
	Set<TupleInt> tuplesInt;

	@OneToMany(mappedBy = "entry")
	Set<TupleFloat> tuplesDouble;

	@OneToMany(mappedBy = "entry")
	Set<Effect> effects;

	/**
	 * Load (do it right now, not lazy)
	 *
	 * @param id
	 * @return
	 */
	public static Entry get(long id) {
		return (Entry) DbUtil.getCurrentSession().get(Entry.class, id);
	}

	public Entry() {
		id = null;
		tuples = new HashSet<Tuple>();
		tuplesInt = new HashSet<TupleInt>();
		tuplesDouble = new HashSet<TupleFloat>();
		effects = new HashSet<Effect>();
	}

	public Entry(VcfEntry vcfEntry) {
		chrom = vcfEntry.getChromosomeName();
		pos = vcfEntry.getStart();
		vcfId = vcfEntry.getId();
		ref = vcfEntry.getRef();
		alt = vcfEntry.getAltsStr();
		qual = vcfEntry.getQuality();
		filter = vcfEntry.getFilterPass();
		tuples = new HashSet<Tuple>();
		tuplesInt = new HashSet<TupleInt>();
		tuplesDouble = new HashSet<TupleFloat>();
		effects = new HashSet<Effect>();
	}

	public void add(Effect e) {
		if (!effects.contains(e)) effects.add(e);
		e.setVcfEntryDb(this);
	}

	/**
	 * Add a tuple
	 * @param t
	 */
	public void add(Tuple t) {
		if (!tuples.contains(t)) tuples.add(t);
		t.setVcfEntryDb(this);
	}

	/**
	 * Add a tupleDouble
	 * @param t
	 */
	public void add(TupleFloat t) {
		if (!tuplesDouble.contains(t)) tuplesDouble.add(t);
		t.setVcfEntryDb(this);
	}

	/**
	 * Add a tupleInt
	 * @param t
	 */
	public void add(TupleInt t) {
		if (!tuplesInt.contains(t)) tuplesInt.add(t);
		t.setVcfEntryDb(this);
	}

	@Override
	public void copySimpleValues(Pojo c) {
		throw new RuntimeException("Unimplemented!");
	}

	public String getAlt() {
		return alt;
	}

	public String getChrom() {
		return chrom;
	}

	public Set<Effect> getEffects() {
		return effects;
	}

	public String getFilter() {
		return filter;
	}

	@Override
	public Long getId() {
		return null;
	}

	public int getPos() {
		return pos;
	}

	public double getQual() {
		return qual;
	}

	public String getRef() {
		return ref;
	}

	public Set<Tuple> getTuples() {
		return tuples;
	}

	public Set<TupleFloat> getTuplesDouble() {
		return tuplesDouble;
	}

	public Set<TupleInt> getTuplesInt() {
		return tuplesInt;
	}

	public String getVcfId() {
		return vcfId;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public void setEffects(Set<Effect> effects) {
		this.effects = effects;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public void setQual(double qual) {
		this.qual = qual;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setTuples(Set<Tuple> tuples) {
		this.tuples = tuples;
	}

	public void setTuplesDouble(Set<TupleFloat> tuplesDouble) {
		this.tuplesDouble = tuplesDouble;
	}

	public void setTuplesInt(Set<TupleInt> tuplesInt) {
		this.tuplesInt = tuplesInt;
	}

	public void setVcfId(String vcfId) {
		this.vcfId = vcfId;
	}

	@Override
	public String toString() {
		return chrom + "\t" + pos + "\t" + vcfId + "\t" + ref + "\t" + alt + "\t" + filter;
	}
}
