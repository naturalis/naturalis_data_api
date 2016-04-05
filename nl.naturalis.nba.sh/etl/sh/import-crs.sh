#!/bin/sh

. ./include.sh

type="${1}"

suppress_errors=false
esBulkrequestSize=1000

JAVA_OPTS="$JAVA_OPTS -Dcrs.suppress-errors=${suppress_errors}"
JAVA_OPTS="$JAVA_OPTS -Des.bulk.request.size=${esBulkrequestSize}"

if [ "x${type}" = x ]
then
	java -cp ${classpath} $JAVA_OPTS ${etl_package}.crs.CrsImportAll
elif [ "${type}" = specimens ]
then
	java -cp ${classpath} $JAVA_OPTS ${etl_package}.crs.CrsSpecimenImportOffline
elif [ "${type}" = multimedia ]
then
	java -cp ${classpath} $JAVA_OPTS ${etl_package}.crs.CrsMultiMediaImportOffline
else
	echo "USAGE: ${0} [ specimens | multimedia ]"
fi





