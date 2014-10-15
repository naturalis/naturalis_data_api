#!/bin/sh

confdir='/opt/nda-es-conf'
libdir='/opt/nda-es-lib'

classpath="${confdir}"
for file in `ls ${libdir}`
do
  classpath="${classpath}:${libdir}/${file}"
done
#echo $classpath

JAVA_OPTS="-server -Xms256m -Xmx1024m"

java -cp ${classpath} nl.naturalis.nda.elasticsearch.load.NDASchemaManager


