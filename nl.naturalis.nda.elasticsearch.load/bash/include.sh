#!/bin/sh

conf_dir=/opt/nda-import/conf
lib_dir=/opt/nda-import/lib
load_package=nl.naturalis.nda.elasticsearch.load

# Make ${conf_dir} the first entry on the classpath so
# that libraries like logback will search here first for
# configuration files
classpath="${conf_dir}"

for file in `find ${lib_dir} -type f`
do
  classpath="${classpath}:${file}"
done

JAVA_OPTS="-server -Xms256m -Xmx1024m"
# Also pass ${conf_dir} as a system property to the
# program
JAVA_OPTS="$JAVA_OPTS -DndaConfDir=${conf_dir}"

