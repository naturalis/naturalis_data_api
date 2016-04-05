#!/bin/sh

. ./include.sh

JAVA_OPTS="$JAVA_OPTS -DshellScript=${0}"

java -cp ${classpath} $JAVA_OPTS ${etl_ackage}.crs.CrsFindInSource  "$@"

