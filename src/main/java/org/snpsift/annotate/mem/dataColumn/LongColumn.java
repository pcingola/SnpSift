package org.snpsift.annotate.mem.dataColumn;

public class LongColumn implements DataColumn<Long> {
	long[] data;
	String name;

	public LongColumn(String name, long[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Long get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Object value) {
		data[i] = (Long) value;
	}
}
