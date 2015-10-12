#!/bin/sh

. ./include.sh

action="${1}"

if [ "${action}" = taxa ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter taxa
elif [ "${action}" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter multimedia
elif [ "${action}" = backup ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter backup
elif [ "${action}" = reset ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter reset
elif [ "x${action}" = x ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter
else
	echo "USAGE: ${0} [taxa|multimedia|backup|reset]"
fi




