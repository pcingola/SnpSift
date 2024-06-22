package org.snpsift.annotate.mem.dataColumn;

public class CharColumn extends DataColumn<Character> {
	char[] data;

	public CharColumn(String name, int size) {
		super(name, size);
		this.data = new char[size];
	}

	@Override
	protected Character getData(int i) {
		return data[i];
	}

	@Override
	protected void setData(int i, Object value) {
		data[i] = (Character) value;
	}
}
