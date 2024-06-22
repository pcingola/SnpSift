package org.snpsift.annotate.mem.dataColumn;

public class LongColumn extends DataColumn<Long> {
	long[] data;

	public LongColumn(String name, int size) {
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
}
