#!/bin/sh

. ./include.sh

type="${1}"

suppressErrors=false
esBulkrequestSize=1000

JAVA_OPTS="$JAVA_OPTS -Dcrs.suppres.errors=${suppressErrors}"
JAVA_OPTS="$JAVA_OPTS -Des.bulk.request.size=${esBulkrequestSize}"

if [ "x${type}" = x ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsImportAll
elif [ "${type}" = specimens ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsSpecimenImportOffline
elif [ "${type}" = multimedia ]
then
	java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsMultiMediaImportOffline
else
	echo "USAGE: ${0} [ specimens | multimedia ]"
fi





