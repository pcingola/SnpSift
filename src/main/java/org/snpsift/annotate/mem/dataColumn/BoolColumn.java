package org.snpsift.annotate.mem.dataColumn;

public class BoolColumn extends DataColumn<Boolean> {
	boolean[] data;

	public BoolColumn(String name, int size) {
		super(name, size);
		this.data = new boolean[size];
	}

	@Override
	protected Boolean getData(int i) {
		return data[i];
	}

	@Override
	protected void setData(int i, Object value) {
		data[i] = (Boolean) value;
	}
}
