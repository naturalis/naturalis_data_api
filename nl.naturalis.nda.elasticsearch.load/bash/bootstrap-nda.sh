#!/bin/sh

# WATCH OUT! Running this script will delete the entire NDA ElasticSearch
# index and all its contents. Afterwards an empty index is created with the
# mappings for the Taxon, Specimen and MultiMediaObject document types.

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.NDASchemaManager


