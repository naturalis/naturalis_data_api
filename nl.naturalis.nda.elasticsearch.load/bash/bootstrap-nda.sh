pwd=$(pwd)
classpath=''
for file in `ls lib/*`
do
classpath="${classpath}:${pwd}/${file}"
done;
echo $classpath
java -cp ${classpath} nl.naturalis.nda.elasticsearch.load.NDASchemaManager


