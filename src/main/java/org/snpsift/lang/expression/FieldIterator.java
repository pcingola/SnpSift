package org.snpsift.lang.expression;

/**
 * Iterates on fields / sub-fields
 * It's a singleton
 * 
 * @author pcingola
 */
public class FieldIterator {

	public enum IteratorType {
		VAR, EFFECT, GENOTYPE, GENOTYPE_VAR, LOF, NMD
	}

	private static final FieldIterator fieldIterator = new FieldIterator();

	int type = 0;
	SimpleIterator var = new SimpleIterator();
	SimpleIterator gentype = new SimpleIterator();
	SimpleIterator effect = new SimpleIterator();
	SimpleIterator gentypeVar = new SimpleIterator();
	SimpleIterator lof = new SimpleIterator();
	SimpleIterator nmd = new SimpleIterator();

	public static FieldIterator get() {
		return fieldIterator;
	}

	/**
	 * Set 'max' parameter for an iterator
	 * @param starType
	 * @param max
	 */
	public int get(IteratorType iterType) {
		switch (iterType) {
		case VAR:
			return var.current;

		case GENOTYPE:
			return gentype.current;

		case GENOTYPE_VAR:
			return gentypeVar.current;

		case EFFECT:
			return effect.current;

		case LOF:
			return lof.current;

		case NMD:
			return nmd.current;

		default:
			throw new RuntimeException("Unknown iterator type '" + iterType + "'");
		}
	}

	public int getType() {
		return type;
	}

	/**
	 * Is there a 'next'
	 * @return
	 */
	public boolean hasNext() {
		return var.hasNext() || effect.hasNext() || gentype.hasNext() || gentypeVar.hasNext();
	}

	/**
	 * Next in iteration
	 */
	public void next() {
		if (gentypeVar.hasNext()) {
			gentypeVar.next();
			return;
		}

		if (gentype.hasNext()) {
			gentypeVar.reset();
			gentype.next();
			return;
		}

		if (nmd.hasNext()) {
			gentypeVar.reset();
			gentype.reset();
			nmd.next();
			return;
		}

		if (lof.hasNext()) {
			gentypeVar.reset();
			gentype.reset();
			nmd.reset();
			lof.next();
			return;
		}

		if (effect.hasNext()) {
			gentypeVar.reset();
			gentype.reset();
			nmd.reset();
			lof.reset();
			effect.next();
			return;
		}

		if (var.hasNext()) {
			gentypeVar.reset();
			gentype.reset();
			nmd.reset();
			lof.reset();
			effect.reset();
			var.next();
			return;
		}

		throw new RuntimeException("Cannot go beyond this count: " + this);
	}

	/**
	 * Reset all counters
	 */
	public void reset() {
		type = 0;
		gentypeVar.reset();
		gentype.reset();
		nmd.reset();
		lof.reset();
		effect.reset();
		var.reset();
	}

	/**
	 * Set 'max' parameter for an iterator
	 * @param starType
	 * @param max
	 */
	public void setMax(IteratorType iterType, int max) {
		switch (iterType) {
		case VAR:
			var.max = Math.max(max, var.max);
			break;

		case GENOTYPE:
			gentype.max = Math.max(max, gentype.max);
			break;

		case GENOTYPE_VAR:
			gentypeVar.max = Math.max(max, gentypeVar.max);
			break;

		case EFFECT:
			effect.max = Math.max(max, effect.max);
			break;

		case LOF:
			lof.max = Math.max(max, lof.max);
			break;

		case NMD:
			nmd.max = Math.max(max, nmd.max);
			break;

		default:
			throw new RuntimeException("Unknown iterator type '" + iterType + "'");
		}
	}

	public void setType(int type) {
		if ((this.type != 0) && (this.type != type)) throw new RuntimeException("Mixing 'ANY' and 'ALL' (or '*' and '?') is not supported!");
		this.type = type;
	}

	@Override
	public String toString() {
		String typeStr = "";

		if (type == Field.TYPE_ALL) typeStr = "ALL ";
		else if (type == Field.TYPE_ANY) typeStr = "ANY ";

		return typeStr + "[ var:" + var.current + " | eff:" + effect.current + " | gt:" + gentype.current + " | gtVar:" + gentypeVar.current + " ]";
	}
}

/**
 * Iterate on one variable
 * @author pcingola
 *
 */
class SimpleIterator {

	int min = 0, max = 0, current = 0;

	boolean hasNext() {
		return current < max;
	}

	void next() {
		current++;
	}

	void reset() {
		current = min;
		max = 0;
	}
}
