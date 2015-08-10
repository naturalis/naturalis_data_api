#!/bin/sh

. ./include.sh

# Rename dump files after they have been processed?
# (Only applicable when importing both specimens and
# multimedia. Otherwise dump files are never renamed)
backup=true

# Number of ES index requests bundled together
esBatchSize=1000

JAVA_OPTS="$JAVA_OPTS -Dnl.naturalis.nda.elasticsearch.load.brahms.batchsize=${esBatchSize}"
JAVA_OPTS="$JAVA_OPTS -Dnl.naturalis.nda.elasticsearch.load.brahms.backup=${backup}"

action="${1}"

if [ "$action" = specimens ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsSpecimensImporter
	echo "WARNING: When loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = multimedia ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsMultiMediaImporter
	echo "WARNING: When not loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = reset ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsBackupExtensionRemover
elif [ "$action" = "" ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsImportAll
else
    echo "Don't know how to import/execute \"$action\"."
fi


