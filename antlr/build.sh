#!/bin/sh -e

grammar="SnpSift.g"
package="ca.mcgill.mcb.pcingola.snpSift.antlr"
init="main"
testFile="../test/z.bds"

# Programs
jar=$HOME/tools/antlr/antlr-4.4-complete.jar
antlr4="java -Xmx1g -cp $jar org.antlr.v4.Tool"
grun="java -Xmx1g -cp .:$jar org.antlr.v4.runtime.misc.TestRig"

# Delete old files
echo Deleting old files
touch tmp.java tmp.class
rm *.class *.java

echo Compiling
$antlr4 -visitor -package $package $grammar

echo Copying files
cp -vf *.java ../src/main/java/ca/mcgill/mcb/pcingola/snpSift/antlr/
