# Bug

There are two bugs related to 'annmem' annotations

1. SNP: When annotating with a `Number=R` both the REF and the ALT values must be annotated. In the example, for T->A with CAF=0.01,1.0001, the annotation gives "CAF=1.0001" instead of "CAF=0.01,1.0001"
2. DEL: For TG -> T, the database has CAF=0.861 (Number=A) or CAF=0.861,0.139 (Number=R), yet the annotation output has no CAF at all

Example files in `$HOME/snpEff/zzz`

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
