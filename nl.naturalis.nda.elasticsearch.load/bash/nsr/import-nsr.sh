#!/bin/sh

# Rebuild Taxon document type?
rebuild='false'
if [ "${1}" == 'rebuild' ]
then
  rebuild='true'
fi

# Rename XML files after they have been processed?
rename='false'
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
JAVA_OPTS="$JAVA_OPTS -DxmlDir=/opt/nda-es-data/nsr"
JAVA_OPTS="$JAVA_OPTS -Drebuild=${rebuild}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.nsr.NsrTaxonImporter
java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.nsr.NsrMultiMediaImporter



