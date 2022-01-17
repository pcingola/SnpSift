#!/bin/sh -e

grammar="SnpSift.g"
package="org.snpsift.antlr"
init="main"
testFile="../test/z.bds"
package_dir=$( echo "$package" | tr '.' '/')
dst="../src/main/java/$package_dir/"

# Programs
jar="antlr-4.9.3-complete.jar"
antlr4="java -Xmx1g -cp $jar org.antlr.v4.Tool"
grun="java -Xmx1g -cp .:$jar org.antlr.v4.runtime.misc.TestRig"

# Delete old files
echo Deleting old files
rm -vf *.class *.java

echo Compiling
# $antlr4 -visitor -package $package $grammar
$antlr4 -package "$package" $grammar

echo Copying files to $dst
cp -vf *.java "$dst"
