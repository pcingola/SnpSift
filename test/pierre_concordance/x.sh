#!/bin/sh

SNPSIFT="$HOME/snpEff/SnpSift.jar"

echo
echo
echo MYVCF 1
cat myvcf1.vcf | java -jar $SNPSIFT concordance ref.vcf - >res2.txt
echo
echo
echo MYVCF 2
cat myvcf2.vcf | java -jar $SNPSIFT concordance ref.vcf - >res1.txt

head -n1 res1.txt | awk '{for(i=1;i<=NF;i++){print(i,$i);}}'
awk '$20==1{print ($1,$2);}' < res1.txt
awk '$20==1{print ($1,$2);}' < res2.txt

VR="2621998"
echo
echo
echo VCF
grep $VR myvcf?.vcf ref.vcf
echo
echo
echo TXT: res 1
grep $VR res1.txt 
echo
echo TXT: res 1
grep $VR res2.txt 
