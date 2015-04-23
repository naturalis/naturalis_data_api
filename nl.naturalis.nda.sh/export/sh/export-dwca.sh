#!/bin/sh

. ./include.sh

collection=${0}

if [ ${collection} != "" ]
then
	java -cp ${classpath} $JAVA_OPTS ${JAVA_ROOT_PACKAGE}.dwca.DwCAExporter ${collection}
else
	for file in `ls ${CONF_DIR}/*.properties`
	do
		collection=${file%.properties}
		case collection in
			botany|geology|zoology)
				;;
			*)
				java -cp ${classpath} $JAVA_OPTS ${JAVA_ROOT_PACKAGE}.dwca.DwCAExporter ${collection}
				;;
		esac
	done
fi
