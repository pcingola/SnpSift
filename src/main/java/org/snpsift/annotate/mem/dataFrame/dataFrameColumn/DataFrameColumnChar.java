package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

public class DataFrameColumnChar extends DataColumn<Character> {
	char[] data;

	public DataFrameColumnChar(String name, int size) {
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

	@Override
	public int size() {
		return data.length;
	}

}
