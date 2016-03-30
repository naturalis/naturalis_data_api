#!/bin/sh

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsHarvester "$@"




