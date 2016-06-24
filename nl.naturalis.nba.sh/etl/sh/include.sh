#!/bin/bash

NBA_ETL_HOME=@etl.install.dir@

cnf_dir=${NBA_ETL_HOME}/conf
lib_dir=${NBA_ETL_HOME}/lib
log_dir=${NBA_ETL_HOME}/log

# The java package that is the root of all NBA ETL
# code. Used by the other scripts to specify full
# class names.
root_package=nl.naturalis.nba.etl

# Make ${confDir} the first entry on the classpath so
# that resource/config files will be found there first.
classpath="${cnf_dir}"

for file in `find ${lib_dir} -type f`
do
  classpath="${classpath}:${file}"
done

#echo CLASSPATH: $classpath

dt=$(date +%Y_%m_%d_%H_%m)
log_file="${log_dir}/${0:2}.${dt}.log"
echo "Log file: ${log_file}"

JAVA_OPTS="-Xms1536m -Xmx1536m -Dnba.v2.conf.dir=${cnf_dir}"
JAVA_OPTS="${JAVA_OPTS} -Dlog4j.configurationFile=${cnf_dir}/log4j2.xml"
JAVA_OPTS="${JAVA_OPTS} -Dnba.v2.etl.logfile=${log_file}"
