package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

public class DataFrameColumnLong extends DataColumn<Long> {
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

}
