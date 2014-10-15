#!/bin/sh

# Rebuild Specimen and MultiMediaObject document types?
rebuild='false'
if [ "${1}" == 'rebuild' ]
then
  rebuild='true'
fi

configFile=/opt/nda-es-loaders/crs/import-crs.properties

# Start from scratch even if a temp file containing the most
# recently processed resumption token was found by the
# program? Set to false if you want to recover from an
# aborted run of the program and you want to start where
# you left.
forceRestart=true

# Number of ES index requests bundled together
bulkRequestSize=1000

confdir='/opt/nda-es-conf'
libdir='/opt/nda-es-lib'

classpath="${confdir}"
for file in `ls ${libdir}`
do
  classpath="${classpath}:${libdir}/${file}"
done
#echo $classpath

JAVA_OPTS="-server -Xms256m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS -Drebuild=${rebuild}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -DconfigFile=${configFile}"

java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.crs.CrsSpecimenImporter
java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.crs.CrsMultiMediaImporter



