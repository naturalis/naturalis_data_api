#!/bin/sh

. ./include.sh

# Directory containing the Brahms export files.
csvDir=${dataDir}/brahms
# Rename export files after they have been processed?
rename='false'
# Number of ES index requests bundled together
bulkRequestSize=1000

JAVA_OPTS="$JAVA_OPTS -DcsvDir=${csvDir}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.brahms.BrahmsImportAll

