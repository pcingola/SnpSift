package org.snpsift.annotate.mem.dataColumn;

public class IntColumn implements DataColumn<Integer> {
	int[] data;
	String name;

	public IntColumn(String name, int[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Integer get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Object value) {
		data[i] = (Integer) value;
	}
}
