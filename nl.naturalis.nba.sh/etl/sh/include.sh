#!/bin/sh

NBA_ETL_HOME=@etl.install.dir@

conf_dir=${NBA_ETL_HOME}/conf
lib_dir=${NBA_ETL_HOME}/lib
etl_package=nl.naturalis.nba.etl

# Make ${confDir} the first entry on the classpath so
# that libraries like logback will search here first for
# configuration files
classpath="${conf_dir}"

for file in `find ${lib_dir} -type f`
do
  classpath="${classpath}:${file}"
done
#echo CLASSPATH: $classpath

JAVA_OPTS="-Xms1536m -Xmx1536m -Dnba.v2.etl.conf.dir=${conf_dir}"
