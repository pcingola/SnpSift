package org.snpsift.annotate.mem.dataColumn;

public class FloatColumn implements DataColumn<Double> {
	double[] data;
	String name;

	public FloatColumn(String name, double[] data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public Double get(int i) {
		return data[i];
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void set(int i, Object value) {
		data[i] = (Double) value;
	}
}
