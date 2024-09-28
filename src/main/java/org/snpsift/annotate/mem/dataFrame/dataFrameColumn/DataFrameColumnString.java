package org.snpsift.annotate.mem.dataFrame.dataFrameColumn;

import java.util.HashSet;
import java.util.Set;

import org.snpeff.util.Log;
import org.snpsift.annotate.mem.arrays.EnumArray;
import org.snpsift.annotate.mem.arrays.StringArray;
import org.snpsift.annotate.mem.arrays.StringArrayBase;
import org.snpsift.util.FormatUtil;

public class DataFrameColumnString extends DataFrameColumn<String> {
	
	StringArrayBase data;

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

	/**
	 * Get the data, used for testing
	 */
	public StringArrayBase getData() {
		return data;
	}

	/**
	 * Resize and memory optimize the data
	 */
	public void resize() {
		// If the number of different strings is small, we could use an EnumArray
		// Count the number of different strings
		Set<String> set = new HashSet<>();		
		boolean useEnumArray = true;
		for (int i = 0; i < data.length(); i++) {
			if( set.add(data.get(i)) ) {
				// Too many different strings
				if(set.size() > EnumArray.MAX_NUMBER_OF_ENUM_VALUES) {
					useEnumArray = false;
					break;
				}
			}
		}
		
		if(useEnumArray) {
			// Create an EnumArray
			EnumArray ea = new EnumArray(data.size());
			for (String str : data)
				ea.add(str);
			Log.debug("Converting column '" + name + "' to EnumArray." //
						+ " Found " + ea.numEnums() + " enums found." // 
						+ " Size before: " + FormatUtil.formatBytes(data.sizeBytes()) //
						+ ", after: " + FormatUtil.formatBytes(ea.sizeBytes()) //
						);
			data = ea;
		}
	}

	@Override
	protected void setData(int i, Object value) {
		data.set(i, value.toString());
	}

	@Override
	public int size() {
		return data.length();
	}

	/**
	 * Memory size of this object (approximate size in bytes)
	 */
	public long sizeBytes() {
        return isNUllData.sizeBytes() + data.sizeBytes();
    }

}
