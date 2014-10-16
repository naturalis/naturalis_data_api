#!/bin/sh

confdir=/opt/nda-import/conf
libdir=/opt/nda-import/lib

classpath="${confdir}"

for file in `find ${libdir} -type f`
do
  classpath="${classpath}:${file}"
done

JAVA_OPTS="-server -Xms256m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS -DndaConfDir=${confdir}"

