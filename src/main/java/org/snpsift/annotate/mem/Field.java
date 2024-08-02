package org.snpsift.annotate.mem;

import java.io.Serializable;

import org.snpeff.vcf.VcfHeaderInfo;
import org.snpeff.vcf.VcfHeaderInfo.VcfInfoNumber;
import org.snpeff.vcf.VcfInfoType;

public class Field implements Serializable{
    
    protected String name; // Field name
    protected VcfInfoType type; // Field type
    protected VcfInfoNumber vcfInfoNumber; // Field vcfInfoNumber
    protected int number; // Field number, when NUMBER is used

    public Field(VcfHeaderInfo vcfInfo) {
        this.name = vcfInfo.getId();
        this.vcfInfoNumber = vcfInfo.getVcfInfoNumber();
        this.type = fieldType(vcfInfo);
        this.number = vcfInfo.getNumber();
    }

    public Field(String name, VcfInfoType type, VcfInfoNumber vcfInfoNumber) {
        this.name = name;
        this.type = type;
        this.vcfInfoNumber = vcfInfoNumber;
    }

    /**
	 * Decide which column data type we'll use for a VcfField
	 * If it's a single value we can use a "primitive" type DataColumn, otherwise we'll use a "StrignColumn"
	 */
	VcfInfoType fieldType(VcfHeaderInfo vcfInfo) {
		VcfInfoType vcfFieldType = vcfInfo.getVcfInfoType();
		switch (vcfInfoNumber) {
			case NUMBER:
				if(vcfInfo.getNumber() > 1) return VcfInfoType.String;
				return vcfFieldType;

			case ALLELE:
			case ALL_ALLELES:
				return vcfFieldType;

			case GENOTYPE:
			case UNLIMITED:
				return VcfInfoType.String;

			default:
				return VcfInfoType.String;
		}
	}


    public String getName() {
        return name;
    }

    public VcfInfoNumber getvcfInfoNumber() {
        return vcfInfoNumber;
    }

    public VcfInfoType getType() {
        return type;
    }

    public String toString() {
        return name + " : " + type + " : " + vcfInfoNumber + (number > 0 ? " : " + number : "");
    }

}
