package ca.mcgill.mcb.pcingola.snpSql.db;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;

/**
 * Simple database interface with some defaults for 'usual' methods
 * 
 * @author Pablo Cingolani
 */
@SuppressWarnings("unchecked")
public abstract class Pojo<T extends Pojo> implements Serializable, Comparable<Pojo> {

	private static final long serialVersionUID = 1L;

	public Pojo() {
	}

	/**
	 * Compare IDs
	 * @param o
	 * @return
	 */
	public int compareId(Pojo o) {
		Integer c = compareNull(this, o);
		if (c != null) return c;

		// Now compare IDs
		Long id1 = getId();
		Long id2 = o.getId();
		c = compareNull(id1, id2);
		if (c != null) return c;

		// Both IDs are non-null => We can compare them
		return id1.compareTo(id2);
	}

	/**
	 * Compare if 2 objects are non-null
	 * @param o1
	 * @param o2
	 * @return Returns 'null' if both objects are non-null
	 * WARNING: This is extremely counter-intuitive!!!
	 */
	public Integer compareNull(Object o1, Object o2) {
		if ((o1 == null) && (o2 == null)) return 0;
		if ((o1 != null) && (o2 == null)) return -1;
		if ((o1 == null) && (o2 != null)) return +1;
		return null;
	}

	/**
	 * Compare by Id
	 */
	@Override
	public int compareTo(Pojo o) {
		return compareId(o);
	}

	/**
	 * Copy another Crud object
	 *  
	 * Note: Must be implemented using setters and getters
	 * It is very annoying to do it with setters and getters, but it's the 
	 * best/safest way it works (if we are in a hibernate session)
	 * 
	 * @param c
	 */
	public void copy(Pojo c) {
		copySimpleValues(c);
	}

	/**
	 * Copy another Crud object  (only simple values, not collections or objects)
	 * @param c
	 */
	public abstract void copySimpleValues(Pojo c);

	/**
	 * Delete from database and delete dependent objects too (if needed) 
	 */
	public void delete() {
		Session s = DbUtil.getCurrentSession();
		s.delete(this);
	}

	/**
	 * Are these Pojo the same?
	 */
	public boolean equals(Pojo o) {
		return compareTo(o) == 0;
	}

	/**
	 * List all objects
	 */
	public List findAll() {
		Criteria criteria = DbUtil.getCurrentSession().createCriteria(this.getClass());
		List lr = criteria.list();
		Collections.sort(lr);
		return lr;
	}

	/**
	 * Why is this method abstract? => Because you need to add the following annotations to your own method 
	 * (in order for hibernate to work properly)
	 * 
	 * @Override
	 * @Id
	 * @GeneratedValue(strategy = GenerationType.AUTO)
	 */
	public abstract Long getId();

	/**
	 * Is this element stored in the database? (i.e. does it have a non-null, positive, ID?)
	 * @return
	 */
	public boolean isDb() {
		if ((getId() != null) && (getId() > 0)) return true;
		return false;
	}

	/**
	 * Save to database
	 */
	public Long save() {
		return (Long) DbUtil.getCurrentSession().save(this);
	}
}
