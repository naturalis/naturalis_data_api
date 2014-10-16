#!/bin/sh

# Rename XML files after they have been processed?
rename='false'
# Number of ES index requests bundled together
bulkRequestSize=1000

. ../include.sh

JAVA_OPTS="$JAVA_OPTS -DxmlDir=/opt/nda-import/data/nsr"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrTaxonImporter
java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrMultiMediaImporter



