package org.snpsift.annotate.mem.dataColumn;

import org.snpsift.annotate.mem.BoolArray;

/**
 * A column of boolean values, that can also be null
 * The bollean values are stored in a BoolArray
 */
public class BoolColumn extends DataColumn<Boolean> {
	BoolArray data;

	public BoolColumn(String name, int size) {
		super(name, size);
		this.data = new BoolArray(size);
	}

	@Override
	protected Boolean getData(int i) {
		return data.is(i);
	}

	@Override
	protected void setData(int i, Object value) {
		if( (Boolean) value) {
			data.set(i);
		} else {
			data.clear(i);
		}
	}

	@Override
	public int size() {
		return data.size();
	}
}
