package ca.mcgill.mcb.pcingola.snpSql.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TupleInt extends Pojo<TupleInt> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String name;
	long value;

	@ManyToOne
	Entry entry;

	/**
	 * Load (do it right now, not lazy)
	 *
	 * @param id
	 * @return
	 */
	public static TupleInt get(long id) {
		return (TupleInt) DbUtil.getCurrentSession().get(TupleInt.class, id);
	}

	public TupleInt() {
		id = null;
	}

	public TupleInt(String name, long value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public void copySimpleValues(Pojo c) {
		TupleInt t = (TupleInt) c;
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

	public long getValue() {
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

	public void setValue(long value) {
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
