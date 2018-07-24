# NBA Configuration file. This file is used for the ETL module and
# the REST service. Some settings are only used by the ETL module, 
# some only by the REST service.

# ***************
# Shared settings
# ***************
elasticsearch.cluster.name=@elasticsearch.cluster.name@
elasticsearch.transportaddress.host=@elasticsearch.transportaddress.host@
elasticsearch.transportaddress.port=9300
elasticsearch.index.default.shards=@elasticsearch.index.default.shards@
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
crs.data.dir=@nba.etl.data.dir@/crs
brahms.data.dir=@nba.etl.data.dir@/brahms
nsr.data.dir=@nba.etl.data.dir@/nsr
col.data.dir=@nba.etl.data.dir@/col
# Needed to generate links to the CoL
col.year=2018
geo.data.dir=@nba.etl.data.dir@/geo
medialib.data.dir=@nba.etl.data.dir@/medialib
ndff.data.dir=@nba.etl.data.dir@/ndff
# Needed to generate values for the unitGUID field while
# importing specimens
purl.baseurl=http://data.biodiversitydata.nl


