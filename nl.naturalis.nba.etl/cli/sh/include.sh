#!/bin/bash
#
NBA_ETL_HOME=@nba.etl.install.dir@

cnf_dir=${NBA_ETL_HOME}/conf
lib_dir=${NBA_ETL_HOME}/lib
log_dir=${NBA_ETL_HOME}/log

# The java package that is the root of all NBA ETL code. Used by the
# other scripts to specify fully qualified class names.
root_package=nl.naturalis.nba.etl

# Whether or not to enable error suppression (causes the suppression of
# ERROR and WARN messages while still letting through INFO messages).
suppress_errors=false

# The number of documents to index at once
queue_size=1000

# Whether or not to delete all documents from a particular source
# system and document type before importing the data for that source
# system and document type (currently only picked up by Brahms
# importers; other importers just do it).
truncate=true

# Whether or not to do a dry run (transform/validate the source data
# but not index it)
dry_run=false

# Provide a comma-separated list of genera to import. This will create a
# test set with only the specified genera from COL, NSR, CRS and BRAHMS.
# test_genera=malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis

# Make ${confDir} the first entry on the classpath so that resource/config
# files will be found there first.
classpath="${cnf_dir}"

for lib in `find ${lib_dir} -type f`
do
  classpath="${classpath}:${lib}"
done
echo CLASSPATH: $classpath

# Generate path for log file
log_file="${0:2}"
if [ "x$1" != "x" ]
then
  log_file="${log_file}__${1}"
fi
if [ "x$2" != "x" ]
then
  log_file="${log_file}__${2}"
fi
dt=$(date +%Y%m%d%H%M%S)
log_file="${log_dir}/${log_file}.${dt}"
echo "Log file: ${log_file}.log"

JAVA_OPTS="-Xms2048m -Xmx2048m"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"
JAVA_OPTS="${JAVA_OPTS} -Dlog4j.configurationFile=${cnf_dir}/log4j2.xml"
JAVA_OPTS="${JAVA_OPTS} -Dnba.conf.file=${cnf_dir}/nba.properties"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.logFileBaseName=${log_file}"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.etl.suppressErrors=${suppress_errors}"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.etl.queueSize=${queue_size}"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.etl.truncate=${truncate}"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.etl.dry=${dry_run}"
JAVA_OPTS="${JAVA_OPTS} -Dnl.naturalis.nba.etl.testGenera=${test_genera}"
