package org.snpsift.annotate.mem.dataColumn;

public class StringColumn implements DataColumn<String> {
	String[] data;
	String name;

	public StringColumn(String name, String[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Object value) {
		data[i] = (String) value;
	}
}
