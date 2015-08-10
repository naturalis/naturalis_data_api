#!/bin/sh

. ./include.sh

action="${1}"

if [ "${action}" = taxa ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrTaxonImporter
elif [ "${action}" = multimedia ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrMultiMediaImporter
elif [ "${action}" = reset ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrBackupExtensionRemover
elif [ "${action}" = "" ]
then
    java -cp ${classpath} $JAVA_OPTS ${loadPackage}.nsr.NsrImportAll
else
    echo "Don't know how to import/execute \"$action\""
fi




