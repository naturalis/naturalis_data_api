#!/bin/sh

. ./include.sh

JAVA_OPTS="$JAVA_OPTS -DshellScript=${0}"

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.crs.CrsFindInSource  "$@"

