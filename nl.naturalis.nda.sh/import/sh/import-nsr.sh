#!/bin/sh

. ./include.sh

type="${1}"

if [ "$type" = taxa ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrTaxonImporter
elif [ "$type" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrMultiMediaImporter
elif [ "$type" = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImportAll
else
    echo "Don't know how to import \"$type\""
fi




