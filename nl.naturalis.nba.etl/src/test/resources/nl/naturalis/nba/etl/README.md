Executing Unit Test cases :
------------------------------
1) To run each test in eclipse update Run As-> Run configuration -> VM Arguments add the following args based on their location in your local machine:

-Dnba.v2.conf.dir=user_home/user/source/nl.naturalis.nba.build/proto.conf.dir
-Dlog4j.configurationFile=user_home/user/source/nl.naturalis.nba.build/log4j2.xml
-Dnl.naturalis.nba.logFileBaseName=TESTS
-Dnl.naturalis.nba.etl.testGenera=malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis
-DsuppressErrors=false
-Xms256m 
-Xmx1536m

2)To make sure nba.properties files contains all these values under  proto.conf.dir has the correct values update build.v2.properties with these values (paths will be different based on the your local setup):

# Directories containing the CSV dumps, XML dumps, etc.
etl.crs.data.dir=/home/plabon/nba/data/nba-brondata-crs
etl.col.data.dir=/home/plabon/nba/data/col/col-2016
etl.brahms.data.dir=/home/plabon/nba/data/brahms
etl.nsr.data.dir=/home/plabon/nba/data/nsr/nsr-data
etl.geo.data.dir=/home/plabon/nba/data/nba-brondata-geo
etl.ndff.data.dir=<directory containing BRAHMS CSV dumps>
etl.medialib.data.dir=/home/plabon/nba/data/medialib


3) Makes sure EleasticSearch is up and running!

4) If you want to run all test at once using the Suite class AllTests.java makes sure you update the VM arguments to increase the virtual memory for Test VM, the tests will fail otherwise as it will run out of Heap space for 
e.g. set the Xms and Xmx values to :  
-Xms512m 
-Xmx4096m