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

elasticsearch.cluster.name=ayco-es2
elasticsearch.transportaddress.host=127.0.0.1
elasticsearch.transportaddress.port=9300
elasticsearch.index.0.name=nba
elasticsearch.index.0.shards=1
elasticsearch.index.0.replicas=0
elasticsearch.index.0.types=Specimen,MultiMediaObject,Taxon,GeoArea




##########################################################
# NBA service
##########################################################

# Directory containing nba.properties (service config).
# For now, you must maintain this variable both here and
# in Wildfly's standalone.xml. I will change the code
# soon such that you will only have to set it here.
nl.naturalis.nba.conf.dir=/home/ayco/projects/nba/v2/service

# Full path (including file name) of the destination of
# the ear file once it it has been built. 
ear.install.path=/home/ayco/programs/wildfly-10.0.0.Final/standalone/deployments/nl.naturalis.nba.ear.ear




##########################################################
# ETL module
##########################################################

# Directory to which to copy the shell scripts for the
# import programs. Will contain an sh directory (shell
# scripts), a conf directory (config files), and a lib
# directory (jar files).
etl.install.dir=/home/ayco/projects/nba/v2/import

# Directories containing the CSV dumps, XML dumps, etc.
etl.crs.data.dir=/home/ayco/projects/nba/data/crs
etl.col.data.dir=/home/ayco/projects/nba/data/col
etl.brahms.data.dir=/home/ayco/projects/nba/data/brahms
etl.nsr.data.dir=/home/ayco/projects/nba/data/nsr
etl.ndff.data.dir=/home/ayco/projects/nba/data/ndff

# Variable used to generate URLs for Catalogue of Life
etl.col.year=2015

# The base URL for PURLs. If you leave this blank, the
# base URL for the production environment is used
# (http://data.biodiversitydata.nl).
etl.purl.baseurl=




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