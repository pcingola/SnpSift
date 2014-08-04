#!/bin/sh

java -jar $HOME/snpEff/SnpSift.jar concordance myref.bare.vcf myvcf.bare.vcf > res.txt
#java -jar $HOME/snpEff/SnpSift.jar concordance ref1.vcf vcf1.vcf > res.txt

echo
echo
echo SnpSift 
( head -n 1 res.txt ; tail -n 1 res.txt ) \
	| $HOME/snpEff/scripts/transpose.pl \
	| tr "/" "\t" \
	| tee res.summary.txt

echo
echo
echo Pierre
awk 'BEGIN{cv["0/0"]=0;cv["0|0"]=0;cv["0/1"]=1;cv["1/0"]=1;cv["0|1"]=1;cv["1|0"]=1;cv["1/1"]=2;cv["1|1"]=2; \
    cv["."]="M";cv["./."]="M";cv[".|."]="M";cv["0|."]="M";cv["1|."]="M";cv[".|0"]="M";cv[".|1"]="M";cnt=0;} \
    FNR==NR{if($0!~/^#/){id=$1":"$2;split($10,a,":");x1[id]=cv[a[1]]; if(id in y){pp=0;} else {cnt++;y[id]=cnt;z[cnt]=id;}} next} \
    {if($0!~/^#/){id=$1":"$2;split($10,a,":");x2[id]=cv[a[1]]; if(id in y){pp=0;} else {cnt++;y[id]=cnt;z[cnt]=id;}}} \
    END{cnt2=0;for(i=1;i<=cnt;i++){b1=b2="A";idd=z[i];if(idd in x1){b1=x1[idd];}if(idd in x2){b2=x2[idd];} \
    gg=b1"_"b2; if(gg in f){f[gg]++;} else {f[gg]=1;cnt2++;h[cnt2]=gg;}} \
    for(i=1;i<=cnt2;i++){print(h[i], f[h[i]]);}} ' \
    myref.vcf myvcf.vcf
