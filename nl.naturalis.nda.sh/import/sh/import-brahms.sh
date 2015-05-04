#!/bin/sh

. ./include.sh

# Rename dump files after they have been processed?
# (Only applicable when importing both specimens and
# multimedia. Otherwise dump files are never renamed)
rename=true

# Number of ES index requests bundled together
bulkRequestSize=1000

JAVA_OPTS="$JAVA_OPTS -Dnl.naturalis.nda.elasticsearch.load.brahms.batchsize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Dnl.naturalis.nda.elasticsearch.load.brahms.backup=${rename}"

type="${1}"

if [ "$type" = specimens ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsSpecimensImporter
	echo "WARNING: When not loading only specimens or only multimedia file backup is disabled"
elif [ "$type" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsMultiMediaImporter
	echo "WARNING: When not loading only specimens or only multimedia file backup is disabled"
elif [ "$type" = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsImportAll
else
    echo "Don't know how to import \"$type\""
fi


