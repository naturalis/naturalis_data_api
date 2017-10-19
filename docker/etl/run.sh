#!/bin/sh

#PATCH nba.properties

if [ "$PATCH_PROPERTIES" = "TRUE" ]
then
echo patching nba.properties 
cat > /payload/software/conf/nba.properties  <<EOF
elasticsearch.cluster.name=nba-cluster
elasticsearch.transportaddress.host=$ES_DNS
elasticsearch.transportaddress.port=9300
elasticsearch.index.default.shards=$DEFAULT_SHARDS
elasticsearch.index.default.replicas=$NUM_REPLICAS
elasticsearch.index.0.name=specimen
elasticsearch.index.0.types=Specimen
elasticsearch.index.1.name=taxon
elasticsearch.index.1.types=Taxon
elasticsearch.index.2.name=multimedia
elasticsearch.index.2.types=MultiMediaObject
elasticsearch.index.3.name=geoareas
elasticsearch.index.3.types=GeoArea
elasticsearch.index.3.shards=1
install.dir=/payload/software
crs.data.dir=/payload/data/crs
col.data.dir=/payload/data/col
brahms.data.dir=/payload/data/brahms
nsr.data.dir=/payload/data/nsr
geo.data.dir=/payload/data/geo
ndff.data.dir=/payload/data/ndff
medialib.data.dir=/payload/data/medialib
col.year=$COL_YEAR
purl.baseurl=$PURL_BASE_URL
EOF
fi

# patch tesset
echo patch for testset
sed -i "s/#test_genera.*/$TEST_GENERA/g" /payload/software/sh/include.sh

# enable/disable truncate (default true)
# only available for BRAHMS at the moment

if [ "$DISABLE_TRUNCATE" = "TRUE" ]
then
    echo "disableing truncate (truncate=false in include.sh)"
    sed -i "s/truncate=true/truncate=false/g" /payload/software/sh/include.sh
fi

# patch log4j.xml
if [ "$CONSOLE_LOG" = "TRUE" ]
then
    echo Enabling console-log 
    sed -i 's#<!-- AppenderRef ref="CONSOLE" / -->#<AppenderRef ref="CONSOLE" />#' /payload/software/conf/log4j2.xml
fi


if [ "$ENABLE_FILEBEAT" = "TRUE" ]
then
    echo Enabling filebeat to ship log to elasticearch
    sed -i 's#<!-- AppenderRef ref="JSON" / -->#<AppenderRef ref="JSON" />#' /payload/software/conf/log4j2.xml
    sed -i 's#<AppenderRef ref="CONSOLE" />#<!-- AppenderRef ref="CONSOLE" / -->#' /payload/software/conf/log4j2.xml
    sed -i 's#<AppenderRef ref="FILE" />#<!-- AppenderRef ref="FILE" / -->#' /payload/software/conf/log4j2.xml
    /filebeat/filebeat -v -e -c /filebeat/etl.yml &
fi

if [ "$AUTO_IMPORT" = "FALSE" ]
then
    echo No autoimport. Ending bootstrap. Going to sleep
    while true
    do 
        echo This is the etl module. Run docker exec $(hostname) ./import-all 
        echo To interact directly with container run docker exec -it $(hostname) bash
        echo From inside the container run exit to exit the container again
        sleep 60000 
    done
fi

URL_PRE=$GIT_URL_PREFIX
#AUTO Download repo's
if [ -z "${REPOS}" ]
then
    echo "NO REPOS defined"
    exit 1
fi

### check strings
if [ ! -z "${REPOS}" ]
then
    OLDIFS=$IFS
    IFS=','
    for repo in ${REPOS}; do
        count_double_p=$(echo $repo | tr -cd ':' | wc -c)
        if [ ! "$count_double_p" = "1" ]
        then
            echo invalid format $repo
            echo found $count_double_p ':', should be 1
            echo format should be: $URL_PRE"<repo-name>:<branch>"
            echo so without the $URL_PRE
            exit 1
        fi
   done
   IFS=$OLDIFS
fi

# do stuff
if [ ! -z "${REPOS}" ]
then
    OLDIFS=$IFS
    IFS=','
    for repo in ${REPOS}; do
        echo handeling $repo
        url=$URL_PRE$(echo $repo|awk -F ':' '{print  $1}')
        branch=$(echo $repo|awk -F ':' '{print $2}')
        clonedir=$IMPORT_DATA_DIR/$(echo $repo| awk -F ':' '{print  $1}'|awk -F '-' '{print $NF}')
        echo repo: $url
        echo branch: $branch 
        echo clonedir: $clonedir
        rm -fr $clonedir
        echo command: git clone --single-branch $url $clonedir -b $branch
        git clone --single-branch $url $clonedir -b $branch
        (cd $clonedir ; sh uncompress.sh)
   done
   IFS=$OLDIFS
fi



echo Now going to import
$IMPORT_COMMAND

