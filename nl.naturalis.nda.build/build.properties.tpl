##########################################################
##########################################################
##                                                      ##
##         Configuration file for Ant builds            ##
##                                                      ##
##########################################################
##########################################################

# This is a master configuration file that is read during
# Ant builds to generate application configuration files
# like nda-import.properties, nda-export.properties, 
# logback.xml, and various other files (e.g. unix shell
# scripts).
#
# You must make a copy of this file named build.properties
# before you can start executing Ant targets. Adjust the
# properties in this file as appropriate before executing
# Ant targets.




##########################################################
# General settings
##########################################################

# ElasticSearch configuration (all modules)
elasticsearch.cluster.name=87f9c32f-84d6-4d9f-b6e3-b9b80bace7cb
elasticsearch.transportaddress.host=172.16.14.2
elasticsearch.transportaddress.port=9300
elasticsearch.index.name=nda

# ElasticSearch configuration (load module only)
elasticsearch.index.numshards=9
elasticsearch.index.numreplicas=1




##########################################################
# NBA service
##########################################################

# The context root a.k.a. base URL for this installation
# of the NBA REST service
nl.naturalis.nda.baseurl=v0

# Directory containing nda.properties and logback.xml
# For now, you must maintain this variable both here and
# in Wildfly's standalone.xml. I will change the code
# soon such that you will only have to set it here.
nl.naturalis.nda.conf.dir=/etc/nba

# Directory to which to copy ear file such that gets
# picked up automatically by wildfly
ear.install.dir=/opt/wildfly_deployments




##########################################################
# Import module
##########################################################

# Directory to which to copy the shell scripts for the
# import programs. Will contain an sh directory (shell
# scripts), a conf directory (config files), and a lib
# directory (jar files).
nda.import.install.dir=/data/nda-import

# Directories containing the CSV dumps, XML dumps, etc.
nda.import.crs.datadir=/data/nda-import/data/crs
nda.import.col.datadir=/data/nda-import/data/col
nda.import.brahms.datadir=/data/nda-import/data/brahms
nda.import.nsr.datadir=/data/nda-import/data/nsr

# Variable used to generate URLs for Catalogue of Life
nda.import.col.year=2014

# Directory into which to write log files for the import
# module. If you want to finetune logging, you will have to
# manually edit ${nda.import.install.dir}/conf/logback.xml
nda.import.log.dir=/data/nda-import/log





##########################################################
# Export module
##########################################################

# Directory into which to install the export module
nda.export.install.dir=/data/nda-export

# Top directory for output from the export programs.
# The DwCA export program will create and write to
# a subdirectory named "dwca".
nda.export.output.dir=/data/nda-export/output

# Top directory for user-editable configuration files.
# The DwCA export program expects the eml files to reside
# in ${nda.export.user.conf.dir}/dwca.
nda.export.user.conf.dir=/data/nda-export/conf

nda.export.log.dir=/data/nda-export/log




##########################################################
# Remote deployment (from development PC/laptop)
##########################################################

ear.install.scp.keyfile=C:/Users/admin.ayco.holleman/ayco.ppk
ear.install.scp.user=ayco.holleman
# You can specify at most 4 remote hosts to which to
# deploy the ear file. Install dir, ssh key file and
# user are assumed to be the same for all hosts
ear.install.scp.host0=10.42.1.146
ear.install.scp.host1=
ear.install.scp.host2=
ear.install.scp.host3=
