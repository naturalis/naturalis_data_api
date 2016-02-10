# Netherlands Biodiversity API (NBA)

Scientists and other interested parties can use this to centrally request scientific biodiversity data from Dutch and foreign source systems and use these in their own software applications.

The first release of the NBA is planned for December 2014. From that moment onwards, basic data can be requested via the service from four large, centrally managed, Naturalis source systems. These are data from two collection systems, CRS and Brahms, and two systems for species registration and management, the Nederlands Soortenregister (Dutch species register) and the Catalogue of Life.

In principle, the following basic data will become available per source system.

| Naturalis source system  | Basic data made available |
| ------------- | ------------- |
| CRS  | Zoological specimens from the main collections: vertebrates, invertebrates, insects, fossils, stones and minerals  |
| Brahms  | Botanical specimens and botanical collections (groups of specimens)  |
| Nederlands Soortenregister and Catalogue of Life  | Species  |
| Brahms, CRS, NSR  | 	Multimedia (photos, videos, 3D)  |

The basic data for specimens can broadly speaking be divided into the ‘who-what-where-when-and how-data’: who identified the specimen with a unique code, what was the identification, and who found the specimen when, where and how or from whom did they receive it.

Basic data for species are taxonomic data supplemented with more detailed data such as species description, distribution area, biotope and habitat.

The data service will be in the form of a technical data API with which a data researcher will be able to request automated or ad hoc datasets per data type for himself and/or others, after he has received permission for this from Naturalis.

After the introduction of the first version of the service, the possibilities will be expanded step by step. These steps will lead to:

 - expansion of the number of affiliated source systems in both the Netherlands and abroad
 - refining of search possibilities and more application-oriented content of datasets
 - delivery of datasets in more file and data formats
 - expansion of the number of basic datasets from Naturalis and other source systems.


### Dependencies

The NBA needs the following dependencies to build and run:

* Java 7
* Eclipse
* JBoss Tools
* IvyDE
* Apache Ant
* Apache Ivy
* Elasticsearch 1.4.3
* Wildfly 8.1.0

### Installation

1. Copy the `nl.naturalis.nda.build/build.properties.tpl` file to `nl.naturalis.nda.build/build.properties` and edit the properties in the `nl.naturalis.nda.build/build.properties` file with the directory structure below.
  ```
   nda
   ├── export                       # Directory into which to install the export module.
     ├── conf                       # Top directory for user-editable configuration files.
     ├── output                     # Top directory for output from the export programs.
     └── log                        # Directory into which to write log files for the import module.
   ├── import                       # Directory to which to copy the shell scripts for the import programs.
     ├── conf                       # Directory containing the mimetype.cache.
       └── mimetype.cache  
     ├── data                       # Directory containing the CSV dumps, XML dumps, etc.
       ├── brahms  
       ├── col
       ├── crs
       ├── log
       └── nsr     
    └── service                     # Directory containing nda.properties.
      └── nda.properties  
  ```
  
2. In the `WILDFLY_HOME_DIR/standalone/configuration/standalone.xml` file you need to add the following lines of code after the `extensions` tag.
  ```
  <system-properties>
          <property name="nl.naturalis.nda.conf.dir" value="PATH_TO_NBA_DIR/nba/service"/>
  </system-properties>
  ```

3. Within Eclipse add a new WildFly 8.x server and add the `nl.naturalis.nda.ear` resource to the server.
4. Copy the `nl.natualis.nda.ear/.settings/org.eclipse.wst.common.project.facet.core.xml.__tmpl__` file to `nl.natualis.nda.ear/.settings/org.eclipse.wst.common.project.facet.core.xml`.
5. In the `PATH_TO_NBA_DIR/nba/service` directory, create a new file called `nba.properties` and add the following lines:
  ```
  elasticsearch.cluster.name=YOUR_CLUSTER_NAME
  elasticsearch.transportaddress.host=YOUR_HOST
  elasticsearch.transportaddress.port=YOUR_PORT
  elasticsearch.index.name=nda
  
  nda.export.output.dir=YOUR_EXPORT_OUTPUT_DIR
  ```
6. Done.
