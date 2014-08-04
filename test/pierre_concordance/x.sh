#!/bin/sh

REF_VCF="ref_21.vcf"
MY_VCF="myvcf.vcf"

# Reference using only chr 21
cat ref.vcf | grep -e "^#" -e "^21" > ref_21.vcf

# Concordance
java -jar ~/snpEff/SnpSift.jar concordance -v $REF_VCF $MY_VCF | tee concordance.txt

# Count lines
cat $REF_VCF | grep -v "^#" | wc -l
cat $MY_VCF  | grep -v "^#" | wc -l

# Compare
cat $MY_VCF | grep -v "^#" | cut -f 1,2 | tr "\t" "_" | sort | tee myvcf.txt 
cat $REF_VCF   | grep -v "^#" | cut -f 1,2 | tr "\t" "_" | sort | tee ref.txt 
join ref.txt myvcf.txt > ref_my.txt
wc -l ref.txt myvcf.txt ref_my.txt


