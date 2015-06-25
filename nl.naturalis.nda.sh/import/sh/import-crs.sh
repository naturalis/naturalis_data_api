#!/bin/sh

. ./include.sh

type="${1}"

# Start from scratch even if a temp file containing the most
# recently processed resumption token was found by the
# program? Set to false if you want to recover from an
# aborted run of the program and you want to start where
# you left.
forceRestart=true

# Number of ES index requests bundled together
bulkRequestSize=1000

JAVA_OPTS="$JAVA_OPTS -DforceRestart=${forceRestart}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"

if [ "x${type}" = x ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsImportAll
elif [ "${type}" = specimens ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsSpecimenImporter
elif [ "${type}" = multimedia ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsMultiMediaImporter
else
	echo "USAGE: ${0} [ specimens | multimedia ]"
fi





