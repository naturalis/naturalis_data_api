#!/bin/sh

. ./include.sh

# Rename export files after they have been processed?
rename='false'
# Number of ES index requests bundled together
bulkRequestSize=1000

JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

type="${1}"

if [ "$type" = specimens ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsSpecimensImporter
	echo "WARNING: When not loading both specimens and multimedia file backup is disabled"
elif [ "$type" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsMultiMediaImporter
	echo "WARNING: When not loading both specimens and multimedia file backup is disabled"
elif [ "$type" = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsImportAll
else
    echo "Don't know how to import \"$type\""
fi


