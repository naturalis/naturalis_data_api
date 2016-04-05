#!/bin/sh

. ./include.sh

type="${1}"

java -cp ${classpath} $JAVA_OPTS ${etl_package}.PrintMapping ${type}
