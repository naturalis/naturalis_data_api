# Rebuild Specimen and MultiMediaObject document types?
rebuild='false'
if [ "${1}" == 'rebuild' ]
then
  rebuild='true'
fi

# Start from scratch even if a temp file containing the most
# recently processed resumption token was found by the
# program? Set to false if you want to recover from an
# aborted run of the program and you want to start where
# you left.
forceRestart=true
# Number of ES index requests bundled together
bulkRequestSize=1000

libdir='/opt/nda-es-loaders/lib'

# Put load.jar ahead of classpath, so logback settings are
# picked up from there
classpath="${libdir}/nl.naturalis.nda.elasticsearch.load.jar"
for file in `ls ${libdir}`
do
  if [ "${file}" == "nl.naturalis.nda.elasticsearch.load.jar" ]
  then
    continue
  fi
  classpath="${classpath}:${libdir}/${file}"
done
#echo $classpath

JAVA_OPTS="-server -Xms256m -Xmx1024m"
JAVA_OPTS="$JAVA_OPTS -DxmlDir=/opt/nda-es-data/nsr"
JAVA_OPTS="$JAVA_OPTS -Drebuild=${rebuild}"
JAVA_OPTS="$JAVA_OPTS -DbulkRequestSize=${bulkRequestSize}"
JAVA_OPTS="$JAVA_OPTS -Drename=${rename}"

java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.crs.CrsSpecimenImporter
java -cp ${classpath} $JAVA_OPTS nl.naturalis.nda.elasticsearch.load.crs.CrsMultiMediaImporter



