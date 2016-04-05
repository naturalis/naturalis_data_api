#!/bin/sh

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${etl_ackage}.NBAImportAll bootstrap import

