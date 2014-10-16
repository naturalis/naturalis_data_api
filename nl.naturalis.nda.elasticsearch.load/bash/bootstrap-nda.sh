#!/bin/sh

confdir='/opt/nda-es-conf'
libdir='/opt/nda-es-lib'

classpath="${confdir}"
for file in `ls ${libdir}`
do
  classpath="${classpath}:${libdir}/${file}"
done
#echo $classpath

java -cp ${classpath} nl.naturalis.nda.elasticsearch.load.NDASchemaManager


