#!/bin/sh

. ./include.sh

suppress_errors="${NBA_IMPORT_SUPPRESS_ERRORS}"

JAVA_OPTS="$JAVA_OPTS -Dbrahms.suppress-errors=${suppress_errors}"

action="${1}"

if [ "$action" = specimens ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsSpecimenImporter
	echo "WARNING: When loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = multimedia ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsMultiMediaImporter
	echo "WARNING: When not loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = backup ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsImportAll backup
elif [ "$action" = reset ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsImportAll reset
elif [ "x$action" = x ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsImportAll
else
    echo "Don't know how to import/execute \"$action\"."
fi

