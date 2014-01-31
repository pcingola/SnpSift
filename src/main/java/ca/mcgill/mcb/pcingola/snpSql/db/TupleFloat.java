package ca.mcgill.mcb.pcingola.snpSql.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TupleFloat extends Pojo<TupleFloat> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String name;
	double value;

	@ManyToOne
	Entry entry;

	/**
	 * Load (do it right now, not lazy)
	 *
	 * @param id
	 * @return
	 */
	public static TupleFloat get(long id) {
		return (TupleFloat) DbUtil.getCurrentSession().get(TupleFloat.class, id);
	}

	public TupleFloat() {
		id = null;
	}

	public TupleFloat(String name, double value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void copySimpleValues(Pojo c) {
		TupleFloat t = (TupleFloat) c;
		name = t.name;
		value = t.value;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public String getValueStr() {
		return value + "";
	}

	public Entry getVcfEntryDb() {
		return entry;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setVcfEntryDb(Entry vcfEntryDb) {
		this.entry = vcfEntryDb;
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}
}
