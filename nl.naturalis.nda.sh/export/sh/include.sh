#!/bin/sh

NDA_EXPORT_HOME=@nda.export.install.dir@
CONF_DIR=${NDA_EXPORT_HOME}/conf
LIB_DIR=${NDA_EXPORT_HOME}/lib
JAVA_ROOT_PACKAGE=nl.naturalis.nda.export

# Make ${CONF_DIR} the first entry on the classpath so
# that libraries like logback will search here first for
# configuration files
classpath="${CONF_DIR}"

for file in `find ${LIB_DIR} -type f`
do
  classpath="${classpath}:${file}"
done
#echo CLASSPATH: $classpath

JAVA_OPTS="-Xms128m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS"
