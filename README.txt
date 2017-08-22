This is the source code for the Netherlands Biodoversity API (NBA). The
NBA is a REST API exposing specimen collection data as well as species
information data from Naturalis. The REST API sits on top of an
Elasticsearch search engine.

For the user manual, see here: ....
For the javadocs, see here: http://naturalis.github.io/naturalis_data_api/javadoc/v2/all

The source code contains three major modules:
o  The REST API itself, which runs in a JEE container
o  A Java REST client
o  An ETL module responsible for importing the Naturalis data sources
   into Elasticsearch.

________________________________________________________________________
REQUIREMENTS  
To build the source code you will need:
o  Apache Ant 9.x
o  Wildfly 10.x
o  A running instance of Elasticsearch 5.1.2

________________________________________________________________________
To deploy the REST API into the Wildfly container:
o  Navigate to the nl.naturalis.nba.build directory
o  Copy nba.properties.tpl to nba.properties
o  Modify nba.properties as appropriate for your environment
o  Run ./ant install-service

________________________________________________________________________
To build the Java client
o  Navigate to the nl.naturalis.nba.build directory
o  Run ./ant install-java-client

________________________________________________________________________
Although in theory you could also build and run the ETL programs, that
doesn't make much sense, because it would require Naturalis-specific
data files. Nevertheless, this is how you can build the ETL module:
o  Navigate to the nl.naturalis.nba.build directory
o  Copy nba.properties.tpl to nba.properties
o  Modify nba.properties as appropriate for your environment
o  Run ./ant install-etl-module


