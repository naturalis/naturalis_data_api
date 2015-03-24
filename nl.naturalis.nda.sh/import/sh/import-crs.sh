#!/bin/sh

. ./include.sh

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

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsImportAll
#java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsMultiMediaImporter




