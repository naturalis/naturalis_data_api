#!/bin/sh

ndaImportHome=@nda.import.install.dir@

ndaConfDir=${ndaImportHome}/conf
libDir=${ndaImportHome}/lib
loadPackage=nl.naturalis.nda.elasticsearch.load

# Make ${ndaConfDir} the first entry on the classpath so
# that libraries like logback will search here first for
# configuration files
classpath="${ndaConfDir}"

for file in `find ${libDir} -type f`
do
  classpath="${classpath}:${file}"
done
#echo CLASSPATH: $classpath

JAVA_OPTS="-server -Xms128m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS -DndaConfDir=${ndaConfDir}"
