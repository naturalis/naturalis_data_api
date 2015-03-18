# Configuration file driving various Ant targets.
# You must make a copy of this file named build.properties
# before you can start executing Ant targets. Adjust the
# properties in this file as appropriate before executing
# Ant targets.

# ES configuration (read by all modules)
elasticsearch.cluster.name=14110822-6ec5-487c-92b7-66402521ceb1
elasticsearch.transportaddress.host=172.16.14.12
elasticsearch.transportaddress.port=9300
elasticsearch.index.name=nda

# ES configuration (read by import module only; see
# ${nda.import.install.dir}/conf/es-settings.json)
elasticsearch.index.numshards=9
elasticsearch.index.numreplicas=1

# Directory to which to copy the shell scripts. This directory
# will contain an sh directory (shell scripts), a conf
# directory (config files), and a lib directory (jar files)
nda.import.install.dir=C:/test/nda-import-home-test

# Directories containing the CSV dumps, XML dumps, etc.
nda.import.crs.datadir=/data/nda-import/data/crs
nda.import.col.datadir=/data/nda-import/data/col
nda.import.brahms.datadir=/data/nda-import/data/col
nda.import.nsr.datadir=/data/nda-import/data/nsr

# Variable used to generate URLs for Catalogue of Life
nda.import.col.year=2014

# Directory into which to write log files for the import
# scripts. If you want to finetune logging, you will have to
# manually edit ${nda.import.install.dir}/conf/logback.xml
nda.import.log.dir=/data/nda-import/log

