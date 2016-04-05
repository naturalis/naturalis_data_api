#!/bin/sh

. ./include.sh

suppress_errors=false

JAVA_OPTS="$JAVA_OPTS -Dcol.suppress-errors=${suppress_errors}"

java -cp ${classpath} ${JAVA_OPTS} ${etl_package}.col.CoLImportAll



