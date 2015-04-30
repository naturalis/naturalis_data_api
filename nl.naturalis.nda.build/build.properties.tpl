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

# Directory to which to copy ear file such that gets
# picked up automatically by wildfly
ear.install.dir=/opt/wildfly_deployments




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

# Directory into which to install the export module.
nda.export.install.dir=/data/nda-export

# Top directory below which the export programs are
# assumed to write their output. Under this directory the
# installation procedure will create and populate an
# sh directory for the shell scripts, a conf directory
# for configuration files, and a lib directory for Java
# libraries (jar files).
nda.export.output.dir=/data/nda-export/output

# Top directory for user-editable configuration files
# and other files picked up or read by the export programs
# (e.g. the eml and properties files for the DwCA export
# module). Ordinarily this directory will be
# "${nda.export.install.dir}/conf". However, sysadmins
# may want to keep "low-level", sysadmin-related config
# files (es-settings.json, nda-export.properties and
# logback.xml) separate from "high-level" user-editable
# files (e.g. the eml files for the DwCA export). In that
# case nda.export.user.conf.dir must be set to the 
# directory to which the sysadmin has moved those
# user-editable files. Note that nda.export.user.conf.dir
# is a TOP directory. Indivual programs will use program-
# specific subdirectories, relative to
# ${nda.export.user.conf.dir}. For example, the DwCA
# export module expects the eml files to reside in
# ${nda.export.user.conf.dir}/dwca.
nda.export.user.conf.dir=/data/nda-export/conf

# Directory into which to write log files for the import
# module. If you want to finetune logging, you will have to
# manually edit ${nda.export.install.dir}/conf/logback.xml
nda.export.log.dir=/data/nda-export/log
