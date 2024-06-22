package org.snpsift.annotate.mem.dataColumn;

import org.snpsift.annotate.mem.StringArray;

public class StringColumn extends DataColumn<String> {
	String[] dataOri;
	StringArray data;

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param size: Number of strings in the column
	 * @param dataSize: Total size of all strings (i.e. memory used)
	 */
	public StringColumn(String name, int size) {
		super(name, size);
		this.dataOri = new String[size];
	}

	@Override
	protected String getData(int i) {
		return data.get(i);
	}

	public void resize(int newSize) {
		// Find total data size
		long dataSize = 0;
		for(int i=0 ; i < dataOri.length ; i++) {
			var s = (isNull(i) ? null : dataOri[i]);
			dataSize += StringArray.sizeOf(s); // +1 for the '\0' character
		}
		// Create an oprimized data array
		if(dataSize > Integer.MAX_VALUE) throw new RuntimeException("Data size is too large: " + dataSize);
		data = new StringArray(dataOri.length, (int) dataSize);
		// Copy data
		for(int i=0 ; i < dataOri.length ; i++) {
			data.add(dataOri[i]);
		}
		// Clear original data
		dataOri = null;
	}

	@Override
	protected void setData(int i, Object value) {
		dataOri[i] = value.toString();
	}
}
