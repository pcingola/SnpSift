package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

public class DataFrameColumnDouble extends DataColumn<Double> {
	double[] data;

	public DataFrameColumnDouble(String name, int size) {
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
