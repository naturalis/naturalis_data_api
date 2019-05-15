# NBA Configuration file. This file is used for the ETL module and
# the REST service. Some settings are only used by the ETL module, 
# some only by the REST service.

# You must make 2 copies of this file: nba.properties and
# nba-test.properties. Then modify the settings as appropriate
# for production and testing respectively.

# IMPORTANT: For nba-test.properties you MUST modify the index
# names. Otherwise you will erase data from the production indexes
# when maven starts the integration tests. Choose any name you
# like, e.g. specimen_integration_test.

# The top directory for configuration files and other assets used
# by the NBA REST service. This directory will contain the "dwca"
# "metadata" subdirectories required by the REST service.
nba.api.install.dir=/etc/nba
# The top directory for configuration files and other assets used
# by the ETL software. This directory will contain the "sh", "conf"
# and "lib" subdirectories required to run the ETL software.
nba.etl.install.dir=/path/to/nba/import/dir


# ***************
# Shared settings
# ***************
elasticsearch.cluster.name=<es-cluster>
elasticsearch.transportaddress.host=127.0.0.1
elasticsearch.transportaddress.port=9300
elasticsearch.index.default.shards=4
elasticsearch.index.default.replicas=0
elasticsearch.index.0.name=specimen
elasticsearch.index.0.types=Specimen
elasticsearch.index.1.name=taxon
elasticsearch.index.1.types=Taxon
elasticsearch.index.2.name=multimedia
elasticsearch.index.2.types=MultiMediaObject
elasticsearch.index.3.name=geoareas
elasticsearch.index.3.types=GeoArea


# **************************
# REST service-only seetings
# **************************

# The maximum number of groups that Elasticsearch can collect when 
# aggregating a set of documents
nl.naturalis.nba.aggregations.maxNumGroups=10000
# The maximum number of buckets (unique scientific names) that
# Elasticsearch must collect for the groupByScientificName service.
# Elasticsearch will stop aggregating over the result set the moment
# it has found this many buckets.
nl.naturalis.nba.specimen.groupByScientificName.maxNumBuckets=10000
# The size of the query cache, which maps queries to their results.
nl.naturalis.nba.specimen.groupByScientificName.queryCacheSize=1000
# The number of milliseconds a query must at least take to be cached.
nl.naturalis.nba.specimen.groupByScientificName.cacheTreshold=3000
nl.naturalis.nba.taxon.groupByScientificName.maxNumBuckets=10000
nl.naturalis.nba.taxon.groupByScientificName.queryCacheSize=1000
nl.naturalis.nba.taxon.groupByScientificName.cacheTreshold=3000


# **************************
# ETL module-only seetings
# **************************

# The ETL can either push the documents it creates directly into 
# the document store (etl.output=es) or write them to the file 
# system (etl.output=file).
etl.output=es

crs.specimens.url.initial=http\://crs.naturalis.nl/atlantispubliek/oai.axd?verb\=ListRecords&metadataprefix\=oai_crs_object
crs.specimens.url.resume=http\://crs.naturalis.nl/atlantispubliek/oai.axd?verb\=ListRecords&resumptionToken\=%s
crs.multimedia.url.initial=http\://crs.naturalis.nl/atlantispubliek/oai.axd?verb\=ListRecords&metadataprefix\=oai_crs
crs.multimedia.url.resume=http\://crs.naturalis.nl/atlantispubliek/oai.axd?verb\=ListRecords&resumptionToken\=%s
# Whether or not to use the pre-harvested, locally stored XML files
# in stead of making live calls to the CRS OAIPMH service
crs.offline=true
# The maximum age in hours of the records to harvest. Zero (0) means
# no maximum (full harvest). Only applicable when using OAI service.
crs.harvest.max.age=0
crs.data.dir=/path/to/datadir/crs
brahms.data.dir=/path/to/datadir/brahms
nsr.data.dir=/path/to/datadir/nsr
col.data.dir=/path/to/datadir/col
# Needed to generate links to the CoL
col.year=2018
geo.data.dir=/path/to/datadir/geo
medialib.data.dir=/path/to/datadir/medialib
ndff.data.dir=/path/to/datadir/ndff
# Needed to generate values for the unitGUID field while
# importing specimens
purl.baseurl=https://data.biodiversitydata.nl


