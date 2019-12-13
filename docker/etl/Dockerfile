FROM openjdk:11
MAINTAINER tom.gilissen@naturalis.nl

ENV ES_DNS=es DEFAULT_SHARDS=12 NUM_REPLICAS=0 NBA_INDEX_NAME=nba COL_YEAR=2019 PURL_BASE_URL=https://data.biodiversitydata.nl TRUNCATE=true BULK_QUEUE_SIZE=1000 OUTPUT=es ENRICH=true
ENV TEST_GENERA=#test_genera=malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis
ENV AUTO_IMPORT=FALSE GIT_URL_PREFIX=https://github.com/naturalis/ IMPORT_DATA_DIR=/payload/data IMPORT_COMMAND=./import-all CONSOLE_LOG=FALSE DISABLE_TRUNCATE=FALSE
ENV REPOS="nba-brondata-nsr:master,nba-brondata-medialib:master,nba-brondata-crs:master,nba-brondata-col:master,nba-brondata-brahms:master,nba-brondata-geo:master"
ENV FILEBEAT_VERSION=7.5.0 LOG_LEVEL=INFO ENABLE_FILEBEAT=FALSE LANG=en_US.UTF-8 PATCH_PROPERTIES=TRUE

RUN apt-get update && apt-get install -y git
RUN mkdir -p /payload/data
RUN mkdir -p /payload/software

WORKDIR /payload

ADD software software
ADD log4j2.xml /payload/software/conf/log4j2.xml

WORKDIR /payload/software/sh
ADD run.sh run.sh
RUN chmod +x run.sh
#CMD ./run.sh
