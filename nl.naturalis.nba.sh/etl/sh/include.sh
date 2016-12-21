#!/bin/bash

NBA_ETL_HOME=@etl.install.dir@

cnf_dir=${NBA_ETL_HOME}/conf
lib_dir=${NBA_ETL_HOME}/lib
log_dir=${NBA_ETL_HOME}/log

# The java package that is the root of all NBA ETL
# code. Used by the other scripts to specify full
# class names.
root_package=nl.naturalis.nba.etl

# Whether or not to enable error suppression
# (causes the suppression of ERROR and WARN messages
# while still allowing INFO messages.
suppress_errors=false

# The number of documents to index at once
queue_size=1000

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

JAVA_OPTS="-Xms1536m -Xmx1536m"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
JAVA_OPTS="${JAVA_OPTS} -DsuppressErrors=${suppress_errors}"
JAVA_OPTS="${JAVA_OPTS} -DqueueSize=${queue_size}"
JAVA_OPTS="${JAVA_OPTS} -Dnba.v2.conf.dir=${cnf_dir}"
JAVA_OPTS="${JAVA_OPTS} -Dlog4j.configurationFile=${cnf_dir}/log4j2.xml"
JAVA_OPTS="${JAVA_OPTS} -Dnba.v2.etl.logfile=${log_file}"
