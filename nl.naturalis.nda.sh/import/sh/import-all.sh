#!/bin/sh

. ./include.sh

java -cp ${classpath} $JAVA_OPTS ${loadPackage}.NDAIndexManager bootstrap import

