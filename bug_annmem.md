# Bug

There are two bugs related to 'annmem' annotations

1. [FIXED] SNP: When annotating with a `Number=R` both the REF and the ALT values must be annotated. In the example, for T->A with CAF=0.01,1.0001, the annotation gives "CAF=1.0001" instead of "CAF=0.01,1.0001"
2. [FIXED] DEL: For TG -> T, the database has CAF=0.861 (Number=A) or CAF=0.861,0.139 (Number=R), yet the annotation output has no CAF at all

Example files in `$HOME/snpEff/zzz`

### Current state

Both bugs are fixed. All tests pass (TestCasesVariantDatabase: 15/15, TestCasesAnnotate: 43/43, DataFrame tests: all passing).

### Test fixes applied

`TestCasesDataFrameDel`, `TestCasesDataFrameIns`, and `TestCasesDataFrameMixed` all used `DataFrameMnp` instead of their actual DataFrame subclass (`DataFrameDel`, `DataFrameIns`, `DataFrameMixed`). The base class method `TestCasesDataFrame.testDataFrame()` also used `DataFrameMnp` for all categories. Fixed by adding a `createDataFrame()` factory method and using the correct subclass in each test.

### Root cause analysis (bug 2, Number=A)

The annmem system normalizes DEL variants: VCF `TG -> T` becomes `ref="G", alt=""` (SnpEff strips the common prefix and shifts the position). `DataFrameDel` (hasRefs=true, hasAlts=false) stores only the ref since all DELs have `alt=""`. The variant lookup works correctly (RS is annotated, proving the DEL is found in the database).

The problem is during database creation, when extracting per-allele field values. In `Fields.getFieldValue()` (Fields.java:72-75), for `Number=A` fields:
```java
valueStr = vcfEntry.getInfo(fieldName, varVcfEntry.getAlt());
```
This calls `VcfEntry.getInfo("CAF", "")` because `varVcfEntry.getAlt()` returns the normalized alt (empty string). But `VcfEntry.getInfo(key, allele)` matches against the original VCF ALTs array `["T"]`. The empty string does not match `"T"`, so null is returned. The null is stored in the DataFrame column, and during annotation null values are skipped.

For `Number=1` fields (like RS), no allele matching is needed, so they work fine. For `Number=R` fields, a different code path returns the raw INFO string without allele matching, so they also work.

### Fix (bug 2)

Changed `Fields.getFieldValue()` to use `VcfEntry.getInfo(String key, Variant var)` instead of `VcfEntry.getInfo(String key, String allele)`. The Variant-based overload matches by iterating through `vcfEntry.variants()` and using `var.equals(v)`, which correctly handles normalized DEL variants. The one-line change in `Fields.java`:
```java
// Before:
valueStr = vcfEntry.getInfo(fieldName, varVcfEntry.getAlt());
// After:
valueStr = vcfEntry.getInfo(fieldName, (Variant) varVcfEntry);
```

### Tests

- `TestCasesVariantDatabase#testCount14DelNumberA`: DEL + Number=A, PASSES
- `TestCasesVariantDatabase#testCount15DelNumberR`: DEL + Number=R, PASSES


### Databases

File: `dbSNP_small.number_A.vcf`
```
##INFO=<ID=RS,Number=1,Type=Integer,Description="dbsnp RSID">
##INFO=<ID=CAF,Number=A,Type=Float,Description="An ordered list of allele frequencies from 1000Genomes, starting with ">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr13	21172461	rs151272242	TG	T	.	.	RS=151272242;CAF=0.861
chr13	21172461	rs151272242	T	A	.	.	RS=151272242;CAF=0.01
```

File: `dbSNP_small.number_R.vcf`
```
##INFO=<ID=RS,Number=1,Type=Integer,Description="dbsnp RSID">
##INFO=<ID=CAF,Number=R,Type=Float,Description="An ordered list of allele frequencies from 1000Genomes, starting with ">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr13	21172461	rs151272242	TG	T	.	.	RS=151272242;CAF=0.861,0.139
chr13	21172461	rs151272242	T	A	.	.	RS=151272242;CAF=0.01,1.0001
```

Create database commands
```
snpsift annmem -create -dbfile dbSNP_small.number_R.vcf -fields 'RS,CAF'
snpsift annmem -create -dbfile dbSNP_small.number_A.vcf -fields 'RS,CAF'
```

### Annotations

Original file: `z.vcf`
```
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr13	21172461	.	TG	T	.	.	.
chr13	21172461	.	T	A	.	.	.
```

Here’s snpSift output when CAF is of type `Number=A`:
Command: `snpsift AnnotateMem -dbfile dbSNP_small.number_A.vcf -prefix DBSNP_ z.vcf`
Output (trimmed):
```
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr13	21172461	.	TG	T	.	.	DBSNP_RS=151272242
chr13	21172461	.	T	A	.	.	DBSNP_RS=151272242;DBSNP_CAF=0.01
```

Here’s snpSift output when CAF is of type `Number=R`:
Command: `snpsift AnnotateMem -dbfile dbSNP_small.number_R.vcf -prefix DBSNP_ z.vcf`
Output (trimmed):
```
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
chr13	21172461	.	TG	T	.	.	DBSNP_RS=151272242
chr13	21172461	.	T	A	.	.	DBSNP_RS=151272242;DBSNP_CAF=1.0001
```


### Commands

Here’s I test data and commands I used for create/annotate accordingly:
```
cd ~/snpEff/zzz

# Create database
snpsift annmem  -create -dbfile dbSNP_small.number_R.vcf -fields 'RS,CAF'
snpsift annmem  -create -dbfile dbSNP_small.number_A.vcf -fields 'RS,CAF'

# Annotate
snpsift AnnotateMem -dbfile dbSNP_small.number_R.vcf -prefix DBSNP_ z.vcf
snpsift AnnotateMem -dbfile dbSNP_small.number_A.vcf -prefix DBSNP_ z.vcf
```


