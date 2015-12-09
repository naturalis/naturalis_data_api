#!/bin/sh

. ./include.sh

action="${1}"

if [ "x${action}" = x ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.ndff.NdffImporter
elif [ "${action}" = reset ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter reset
else
	echo "USAGE: ${0} [reset]"
fi




