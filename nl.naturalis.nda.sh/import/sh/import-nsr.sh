#!/bin/sh

. ./include.sh

# Rename XML files after they have been processed?
rename='false'
# Number of ES index requests bundled together
bulkRequestSize=1000

JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

type="${1}"

if [ $type = taxa ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrTaxonImporter
elif [ $type = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrMultiMediaImporter
elif [ $type = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImportAll
else
    echo "Don't know how to import \"$type\""
fi




