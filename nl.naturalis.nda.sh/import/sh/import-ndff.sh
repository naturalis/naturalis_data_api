#!/bin/sh

. ./include.sh

action="${1}"

if [ "x${action}" = x ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.ndff.NdffSpecimenImporter
elif [ "${action}" = reset ]
then
    echo "Backup / undo backup not implemented yet"
else
	echo "USAGE: ${0} [reset]"
fi




