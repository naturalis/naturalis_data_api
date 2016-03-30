#!/bin/sh

. ./include.sh

type="${1}"

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.PrintMapping ${type}
