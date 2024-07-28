package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

import org.snpsift.annotate.mem.arrays.StringArray;

public class DataFrameColumnString extends DataFrameColumn<String> {
	StringArray data;

	/**
	 * Create a StringColumn from an array of strings
	 */
	public static DataFrameColumnString of(String name, String[] strings) {
		var size = StringArray.sizeOf(strings);
		DataFrameColumnString col = new DataFrameColumnString(name, strings.length, size);
		for (int i=0 ; i < strings.length ; i++)
			col.set(i, strings[i]);
		return col;
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param size: Number of strings in the column
	 * @param dataSize: Total size of all strings (i.e. memory used)
	 */
	public DataFrameColumnString(String name, int length, int size) {
		super(name, length);
		data = new StringArray(length, size);
	}

	@Override
	protected String getData(int i) {
		return data.get(i);
	}

	@Override
	protected void setData(int i, Object value) {
		data.set(i, value.toString());
	}

	@Override
	public int size() {
		return data.length();
	}

}
