package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

import org.snpsift.annotate.mem.arrays.BoolArray;

/**
 * A column of boolean values, that can also be null
 * The bollean values are stored in a BoolArray
 */
public class DataFrameColumnBool extends DataFrameColumn<Boolean> {
	BoolArray data;

	public DataFrameColumnBool(String name, int size) {
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

    /**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return isNUllData.sizeBytes() + data.sizeBytes();
    }

}
