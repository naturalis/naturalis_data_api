#!/bin/sh

ndaImportHome=@nda.import.install.dir@

confDir=${ndaImportHome}/conf
libDir=${ndaImportHome}/lib
loadPackage=nl.naturalis.nda.elasticsearch.load

# Make ${confDir} the first entry on the classpath so
# that libraries like logback will search here first for
# configuration files
classpath="${confDir}"

for file in `find ${libDir} -type f`
do
  classpath="${classpath}:${file}"
done
#echo CLASSPATH: $classpath

JAVA_OPTS="-server -Xms256m -Xmx1536m"

JAVA_OPTS="$JAVA_OPTS -Dnba.v2.conf.dir=${confDir}"
