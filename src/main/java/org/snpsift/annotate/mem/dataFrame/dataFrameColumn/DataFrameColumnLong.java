package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

public class DataFrameColumnLong extends DataFrameColumn<Long> {
	
	long[] data;

	public DataFrameColumnLong(String name, int size) {
		super(name, size);
		this.data = new long[size];
	}

	@Override
	protected Long getData(int i) {
		return data[i];
	}

	@Override
	protected void setData(int i, Object value) {
		data[i] = (Long) value;
	}

	@Override
	public int size() {
		return data.length;
	}

	/**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return isNUllData.sizeBytes() + 8 * data.length;
    }

}
