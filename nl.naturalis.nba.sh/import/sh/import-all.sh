#!/bin/sh

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.NBAImportAll bootstrap import

