#!/bin/sh

. ./include.sh

action="${1}"

if [ "${action}" = taxa ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter taxa
elif [ "${action}" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter multimedia
elif [ "${action}" = reset ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrBackupExtensionRemover
elif [ "${action}" = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImporter
else
    echo "Don't know how to import/execute \"$action\""
fi




