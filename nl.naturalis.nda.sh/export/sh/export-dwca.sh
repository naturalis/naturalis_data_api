#!/bin/sh

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${JAVA_ROOT_PACKAGE}.dwca.DwCAExporter "${@}"
