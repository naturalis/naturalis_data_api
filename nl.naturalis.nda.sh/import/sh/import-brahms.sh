#!/bin/sh

. ./include.sh

suppress_errors=true

JAVA_OPTS="$JAVA_OPTS -Dbrahms.suppres-errors=${suppress_errors}"

action="${1}"

if [ "$action" = specimens ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsSpecimensImporter
	echo "WARNING: When loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = multimedia ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsMultiMediaImporter
	echo "WARNING: When not loading only specimens or only multimedia file backup is disabled"
elif [ "$action" = reset ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsBackupExtensionRemover
elif [ "$action" = "" ]
then
    java -cp ${classpath} ${JAVA_OPTS} ${loadPackage}.brahms.BrahmsImportAll
else
    echo "Don't know how to import/execute \"$action\"."
fi


