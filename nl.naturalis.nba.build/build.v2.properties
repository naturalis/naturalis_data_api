##########################################################
##########################################################
##                                                      ##
##         Configuration file for NBA builds            ##
##                                                      ##
##########################################################
##########################################################

# This is the template for the master configuration file
# for NBA builds. Make a copy named build.v2.properties
# and edit that file as appropriate for your environment.



##########################################################
# General settings
##########################################################

# ElasticSearch configuration (all modules)
elasticsearch.cluster.name=ayco-01
elasticsearch.transportaddress.host=127.0.0.1
elasticsearch.transportaddress.port=9300
elasticsearch.index.name=nda

# ElasticSearch configuration (ETL module only)
elasticsearch.index.numshards=1
elasticsearch.index.numreplicas=0




##########################################################
# NBA service
##########################################################

# The context root a.k.a. base URL for this installation
# of the NBA REST service
nl.naturalis.nba.baseurl=v2

# Directory containing nba.properties (service config).
# For now, you must maintain this variable both here and
# in Wildfly's standalone.xml. I will change the code
# soon such that you will only have to set it here.
nl.naturalis.nba.conf.dir=/home/ayco/projects/nba/v1/service

# Directory to which to copy ear file such that gets
# picked up automatically by wildfly
ear.install.dir=/opt/wildfly-8.2.1.Final/standalone/deployments




##########################################################
# Import module
##########################################################

# Directory to which to copy the shell scripts for the
# import programs. Will contain an sh directory (shell
# scripts), a conf directory (config files), and a lib
# directory (jar files).
nba.import.install.dir=/home/ayco/projects/nba/v1/import

# Directories containing the CSV dumps, XML dumps, etc.
nba.import.crs.datadir=/home/ayco/projects/nba/data/crs
nba.import.col.datadir=/home/ayco/projects/nba/data/col
nba.import.brahms.datadir=/home/ayco/projects/nba/data/brahms
nba.import.nsr.datadir=/home/ayco/projects/nba/data/nsr
nba.import.ndff.datadir=/home/ayco/projects/nba/data/ndff

# Variable used to generate URLs for Catalogue of Life
nba.import.col.year=2015

# Directory into which to write log files for the import
# module. If you want to finetune logging, you will have to
# manually edit ${nba.import.install.dir}/conf/logback.xml
nba.import.log.dir=/home/ayco/projects/nba/v0/import/log

# The base URL for PURLs. If you leave this blank, the
# base URL for the production environment is used
# (http://data.biodiversitydata.nl).
nba.import.purl.baseurl=




##########################################################
# Export module
##########################################################

# Directory into which to install the export module
nba.export.install.dir=/data/nda-export

# Top directory for output from the export programs.
# The DwCA export program will create and write to
# a subdirectory named "dwca".
nba.export.output.dir=/data/nda-export/output

# Top directory for user-editable configuration files.
# The DwCA export program expects the eml files to reside
# in ${nba.export.user.conf.dir}/dwca.
nba.export.user.conf.dir=/data/nda-export/conf

nba.export.log.dir=/data/nda-export/log
