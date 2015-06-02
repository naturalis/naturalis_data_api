#!/bin/sh

. ./include.sh

# Number of ES index requests bundled together
esBatchSize=1000

JAVA_OPTS="$JAVA_OPTS -Dnl.naturalis.nda.elasticsearch.load.col.batchsize=${esBatchSize}"

java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.col.CoLImportAll



