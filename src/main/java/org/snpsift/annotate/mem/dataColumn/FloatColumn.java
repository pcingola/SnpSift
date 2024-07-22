package org.snpsift.annotate.mem.dataColumn;

public class FloatColumn extends DataColumn<Double> {
	double[] data;

	public FloatColumn(String name, int size) {
		super(name, size);
		this.data = new double[size];
	}

	@Override
	protected Double getData(int i) {
		return data[i];
	}

	@Override
	protected void setData(int i, Object value) {
		data[i] = (Double) value;
	}

	@Override
	public int size() {
		return data.length;
	}

}
