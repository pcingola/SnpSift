package org.snpsift.annotate.mem.dataColumn;

public class IntColumn extends DataColumn<Integer> {
	int[] data;

	public IntColumn(String name, int size) {
		super(name, size);
		this.data = new int[size];
	}

	@Override
	protected Integer getData(int i) {
		return data[i];
	}

	@Override
	protected void setData(int i, Object value) {
		data[i] = (Integer) value;
	}

	@Override
	public int size() {
		return data.length;
	}

}
