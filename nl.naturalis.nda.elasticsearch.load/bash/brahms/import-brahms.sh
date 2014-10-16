#!/bin/sh

# Rebuild Specimen and MultiMediaObject document type?
rebuild='false'
if [ "${1}" == 'rebuild' ]
then
  rebuild='true'
fi

# Rename CSV files after they have been processed?
rename='false'
# Number of ES index requests bundled together
bulkRequestSize=1000

# Directory containing all required config files. Must be
# passed as system property, and must also be on classpath
confdir='/opt/nda-es-conf'
libdir='/opt/nda-es-lib'

classpath="${confdir}"
for file in `ls ${libdir}`
do
  classpath="${classpath}:${libdir}/${file}"
done
#echo $classpath

JAVA_OPTS="-server -Xms256m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS -DndaConfDir=${confdir}"
JAVA_OPTS="$JAVA_OPTS -DcsvDir=/opt/nda-es-data/brahms"
JAVA_OPTS="$JAVA_OPTS -Drebuild=${rebuild}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportAll



