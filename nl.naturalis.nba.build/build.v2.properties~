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

elasticsearch.cluster.name=<cluster-name>
elasticsearch.transportaddress.host=127.0.0.1
elasticsearch.transportaddress.port=9300


##########################################################
# NBA service
##########################################################

# NBA configuration directory. Will contain nba.properties
# and other configuration artefacts (e.g. for the DwCA
# services).
nl.naturalis.nba.conf.dir=<NBA_CONF_DIR>

# Full path (including file name !) of the NBA war file
war.install.path=/path/to/wildfly/standalone/deployments/nba.war


##########################################################
# Import programs (ETL module)
##########################################################

# Top directory for the import programs.  Will contain an
# sh directory (shell scripts), a conf directory (config 
# files), and a lib directory (jar files).
etl.install.dir=<ETL_TOP_DIR>

# Directories containing the CSV dumps, XML dumps, etc.
etl.crs.data.dir=<directory containing CRS OAIPMH XML file>
etl.col.data.dir=<directory containing an unzip COL DwC archive>
etl.brahms.data.dir=<directory containing BRAHMS CSV dumps>
etl.nsr.data.dir=<directory containing NSR XML dumps>
etl.geo.data.dir=<directory containing GEO areas CSV file>
etl.ndff.data.dir=<directory containing BRAHMS CSV dumps>
etl.medialib.data.dir=<directory containing mimetypes.zip>

# Variable used to generate URLs for Catalogue of Life
etl.col.year=2016

# The base URL for PURLs. If you leave this blank, the
# base URL for the production environment is used
# (http://data.biodiversitydata.nl).
etl.purl.baseurl=
