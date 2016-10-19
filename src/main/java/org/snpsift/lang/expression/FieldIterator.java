package org.snpsift.lang.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.samtools.util.StringUtil;

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
	SimpleIterator gentype = new SimpleIterator();
	SimpleIterator effect = new SimpleIterator();
	SimpleIterator gentypeVar = new SimpleIterator();
	SimpleIterator lof = new SimpleIterator();
	SimpleIterator nmd = new SimpleIterator();
	SequencedIterator var = new SequencedIterator();

	public static FieldIterator get() {
		return fieldIterator;
	}

	public int getVar(String fieldName) {
	    SimpleIterator fieldIterator = var.get(fieldName);
	    if (fieldIterator == null) {
	        throw new RuntimeException("Unknown field '" + fieldName + "'");
	    }
	    return fieldIterator.current;
	}

	/**
	 * Set 'max' parameter for an iterator
	 * @param starType
	 * @param max
	 */
	public int get(IteratorType iterType) {
		switch (iterType) {

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

		case VAR:
		    throw new UnsupportedOperationException("Must specify field name associated with " + iterType);

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
		var.resetAll();
	}

	public void setVarMax(String fieldName, int max) {
	    var.setMax(fieldName, max);
	}

	/**
	 * Set 'max' parameter for an iterator
	 * @param starType
	 * @param max
	 */
	public void setMax(IteratorType iterType, int max) {
		switch (iterType) {

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

		case VAR:
		    throw new UnsupportedOperationException("Must specify field name associated with " + iterType);

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

		return typeStr + "[ var:" + var.toString() + " | eff:" + effect.current + " | gt:" + gentype.current + " | gtVar:" + gentypeVar.current + " ]";
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

/**
 * Iterate on a sequence of named variables
 */
class SequencedIterator {

    /* iteration order needs to be predictable */
    final LinkedHashMap<String, SimpleIterator> map = new LinkedHashMap<>();

    void setMax(String key, int max) {
        SimpleIterator keyIterator = get(key);
        if (keyIterator == null) {
            keyIterator = new SimpleIterator();
            map.put(key, keyIterator);
        }
        keyIterator.max = Math.max(max, keyIterator.max);
    }

    SimpleIterator get(String key) {
        return map.get(key);
    }

    boolean hasNext() {
        for (Map.Entry<String, SimpleIterator> entry : map.entrySet()) {
            SimpleIterator iterator = entry.getValue();
            if (iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    void next() {
        for (Map.Entry<String, SimpleIterator> entry : map.entrySet()) {
            String key = entry.getKey();
            SimpleIterator iterator = entry.getValue();
            if (iterator.hasNext()) {
                resetUntil(key);
                iterator.next();
                break;
            }
        }
    }

    void resetUntil(String terminationKey) {
        for (Map.Entry<String, SimpleIterator> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.equals(terminationKey)) {
                break;
            } else {
                entry.getValue().reset();
            }
        }
    }

    void resetAll() {
        for (SimpleIterator iterator : map.values()) {
            iterator.reset();
        }
    }

    @Override
    public String toString() {

        /* prints each key and its current element in reverse order, 
         * e.g. { k_n=v_n, ..., k2=v2,k1=v1 }*/

        List<String> list = new ArrayList<>(); 
        for (Map.Entry<String, SimpleIterator> entry : map.entrySet()) {
            String key = entry.getKey();
            SimpleIterator iterator = entry.getValue();
            list.add(String.format("%s=%d", key, iterator.current));
        }

        Collections.reverse(list);

        return String.format("{ %s }", StringUtil.join(",", list));
    }
}
