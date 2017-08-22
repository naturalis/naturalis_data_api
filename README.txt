This is the source code for the Netherlands Biodoversity API (NBA). The
NBA is a REST API exposing specimen collection data as well as species
information data from Naturalis. The REST API sits on top of an
Elasticsearch search engine.

For the user manual, see here: XXXXXXXXXXXX
For the javadocs, see here: http://naturalis.github.io/naturalis_data_api/javadoc/v2/all

The source code contains three major modules:

o  The REST API itself, which runs in a JEE container
o  A Java REST client
o  An ETL module responsible for importing the Naturalis data sources
   into Elasticsearch.
   

