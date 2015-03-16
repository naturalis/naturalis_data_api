#!/bin/sh

. ./include.sh

# Rename CSV files after they have been processed?
rename="false"
# Number of ES index requests bundled together
bulkRequestSize=2000

JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.col.CoLImportAll



