========================
NBA Service descriptions
========================

-------------------------
Document management
-------------------------

.. list-table:: 
   :widths: 20 100
   :header-rows: 0
   
   * - Author
     - Naturalis ICT sector, Software Development department
   * - Document version
     - c1.1.
   * - Document publication date
     - 11-06-2015
   * - Content
     - Description of data retrieval services in NBA API v0.12.2

-------------------------
Summary
-------------------------
The Netherlands Biodiversity API offers web services for free text search and specific indexed field(s) search in multiple taxonomy and specimen occurrence data sources. Searches can be done on either taxa, specimen occurrences, and, multimedia linked to either a taxon or a specimen occurrence. The searchable data is stored as text documents in Elasticsearch document store. In nearly all cases an API request returns data as a JSON-formatted document. 

For each type of data, i.e. taxon, specimen occurrence and multimedia, one or more http GET services - data retrieval operations - are available. 
Each service is described in this document. Per service the following information is available. 

Taxonomic data services
========================

.. list-table:: 
   :widths: 50 100
   :header-rows: 1

   * - TAXON search
     - 
   * - **Description**
     - | This direct search service returns TAXON documents. Available direct search types are a. free text search, and b. indexed field search.
   * - | **Request**
       |
       |
       |
       |
       |
       |
       |
       |
       |
       | 
       |
       |
       | More details in
       | `taxon request section`_       
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *HTTP support*
            - GET
            - GET
          * - *Searchable fields*
            - all taxon document fields are queried
            - `list of selected taxon document fields <http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#taxon-search>`_ are queried
          * - *Geospatial search*
            - Not available
            - Not available
          * - *Meta parameters*
            - Paging and sorting parameters are available.
            - | Paging and sorting parameters are available. 
              |
              | Additional logical operator _andOr for indexed field search.  
          * - *Data format*
            - JSON
            - JSON
          * - *Details*
            - | A response example can be viewed after calling a request example in the `free text taxon search examples section`_
            - | A response example can be viewed after calling a request example in the `indexed field taxon search examples section`_
   * - | **Response**
       |
       |
       | More details in
       | `taxon response section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *Data format*
            - JSON
            - JSON
          * - *Details*
            - | A response example can be viewed after calling a request example in the `free text taxon search examples section`_
            - | A response example can be viewed after calling a request example in the `indexed field taxon search examples section`_
   * - | **Examples**
     - | Examples of both free text and indexed field taxon searches can be found in the `specimen examples section`_
        

Specimen Occurrence data services
=================================

.. list-table:: 
   :widths: 50 100
   :header-rows: 1

   * - SPECIMEN search
     - 
   * - **Description**
     - | This direct search service returns SPECIMEN documents. Available direct search types are a. free text search, and b. indexed field search.
   * - | **Request**
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       | More details in
       | `specimen request section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *HTTP support*
            - GET
            - GET
          * - *Searchable fields*
            - all specimen document fields are queried
            - `list of selected specimen document fields`_ are queried
          * - *Geospatial search*
            - Geosearch is available. Combined geo - free text search uses default the boolean operator AND. Sec geosearch is also possible.
            - Geosearch is available. Combined geo - indexed field search uses default the boolean operator AND. Sec geosearch is also possible.
          * - *Meta parameters*
            - Paging and sorting parameters are available.
            - | Paging and sorting parameters are available. 
              |
              | Additional logical operator _andOr for indexed field search.  
   * - | **Response**
       |
       |
       |
       | More details in
       | `specimen response section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *Data format*
            - JSON
            - JSON
          * - *Details*
            - | A response example can be viewed after calling a request example in the `free text specimen search examples section`_
            - | A response example can be viewed after calling a request example in the `indexed field specimen search examples section`_

   * - | **Examples**
     - | Examples of both free text and indexed field taxon searches can be found in the `specimen examples section`_

|

.. list-table:: 
   :widths: 50 100
   :header-rows: 1

   * - SPECIMEN NAME search
     - 
   * - **Description**
     - | This combined direct and indirect search service returns SPECIMEN documents. Available direct search types are a. free text search, and b. indexed field search. The indirect search service is an in-built name resolution service which is default active. Name resolution can not be deactivated. 
   * - | **Request**
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       | More details in
       | `specimen name request section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *HTTP support*
            - GET
            - GET
          * - *Searchable fields*
            - all specimen document fields are queried
            - `list of selected specimen document fields for specimen name search`_ are queried. There is no overlap between this list and the list of selected fields for specimen seaerch. 
          * - *Geospatial search*
            - Geosearch is available. Combined geo - free text search uses default the boolean operator AND. Sec geosearch is also possible.
            - Geosearch is available. Combined geo - indexed field search uses default the boolean operator AND. Sec geosearch is also possible.
          * - *Meta parameters*
            - Paging and sorting parameters are available.
            - | Paging and sorting parameters are available. 
              |
              | Additional logical operator _andOr for indexed field search.  
   * - | **Response**
       |
       |
       |
       | More details in
       | `specimen name response section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *Data format*
            - JSON
            - JSON
          * - *Details*
            - | A response example can be viewed after calling a request example in the `free text specimen name search examples section`_
            - | A response example can be viewed after calling a request example in the `indexed field specimen name search examples section`_
   * - | **Examples**
     - | Examples of both free text and indexed field taxon searches can be found in the `specimen name examples section`_

|

.. list-table:: 
   :widths: 50 100
   :header-rows: 1

   * - SPECIMEN COLLECTION search
     - 
   * - **Description**
     - | This direct search service returns a DwCA zip file containing all available SPECIMEN occurrences of a Naturalis Collection. All DwCA zip files have a similar internal file structure and comply to multiple data standards. 

   * - | **Request**
       |
       |
       | 
       |
       | More details in
       | `specimen collection request section`_
     - .. list-table:: 
          :widths: 50 150
          :header-rows: 1

          * - 
            - 
          * - *HTTP support*
            - GET
          * - *Downloadable collections*
            - `list of Naturalis collections`_ which are available for download. The list is living document. 
          * - *Geospatial search*
            - Not available
          * - *Meta parameters*
            - No specific meta parameters are available
   * - | **Response**
       |
       | More details in
       | `specimen collection response section`_
     - .. list-table:: 
          :widths: 50 150
          :header-rows: 1

          * - 
            - 
          * - *Data format*
            - DwCA
          * - *Details*
            - The basic structure of a DwCA file can be viewed after calling a request example in the `specimen collection examples section`_
   * - | **Examples**
     - | Examples of DwCA file downloads can be found in the `specimen collection examples section`_


Multimedia data services
========================

.. list-table:: 
   :widths: 50 100
   :header-rows: 1

   * - MULTIMEDIA search
     - 
   * - **Description**
     - | This combined direct and indirect search service returns MULTIMEDIA documents. Available direct search types are a. free text search, and b. indexed field search. The indirect search service is an in-built name resolution service which is default active. Name resolution can not be deactivated. 
   * - | **Request**
       |
       |
       |
       |
       |
       |
       |
       |
       |
       |
       | More details in
       | `multimedia request section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *HTTP support*
            - GET
            - GET
          * - *Searchable fields*
            - all specimen document fields are queried
            - `list of selected multimedia document fields`_ are queried
          * - *Geospatial search*
            - Geosearch is available. Combined geo - free text search uses default the boolean operator AND. Sec geosearch is also possible.
            - Geosearch is available. Combined geo - indexed field search uses default the boolean operator AND. Sec geosearch is also possible.
          * - *Meta parameters*
            - Paging and sorting parameters are available.
            - | Paging and sorting parameters are available. 
              |
              | Additional logical operator _andOr for indexed field search.  
   * - | **Response**
       |
       |
       | More details in
       | `multimedia response section`_
     - .. list-table:: 
          :widths: 60 100 100
          :header-rows: 1

          * - 
            - Free text search
            - Indexed field search 
          * - *Data format*
            - JSON
            - JSON
          * - *Details*
            - | The basic structure of the responses can be viewed in `free text multimedia search examples section`_
            - | The basic structure of the responses can be viewed in `indexed field multimedia search examples section`_
   * - | **Examples**
     - | Examples of both free text and indexed field taxon searches can be found in the `multimedia examples section`_

	 
Base url
========

The base url for each service is: 

.. code:: html

  http://api.biodiversitydata.nl/v0
  
For more information about the API version you are working with click here_. 

.. _here: http://api.biodiversitydata.nl/v0/version

.. _list of selected taxon document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#taxon-search

.. _list of selected specimen document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#specimen-search

.. _list of selected specimen document fields for specimen name search: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#specimen-name-search

.. _list of selected multimedia document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#multimedia-search

-----------
Quick start
-----------
The Netherlands Biodiversity API endpoints follows the general form:

.. code:: html

  http://api.biodiversitydata.nl/{api_version}{endpoint}{optional parameters}

In nearly all cases an API request returns data as a JSON-formatted document.

For Quick start purposes you can consult per service a set of examples which can be accessed through the example links in the service information overview. Information on requests, searchable fields and responses can also be accessed via this overview in the previous paragraph. 

----------
Versioning
----------
The API's current version = v0.

Version v0 is a stable version, ready for public use. The documentation related to this version is still under construction and might therefore grow or change in the near future. 

Bugfixes or other changes on version v0 will lead be processed in small version updates. Backward compatibility with previous v0 versions is guaranteed. The version number in the url of the API, currently v0, will not change as a result of these small changes. 

Version v0 does not yet contain all core data services and documentation relevant for optimal data gathering.
Version v1 which will replace version v0 should largely fill this gap. Naturalis strives to make this higher version backwards compatible with version v0. If backwards compatiblity can not be reached version v0 will be serviced at least half a year after version v1 has been released. 

Naturalis reserves the right to deviate from the above text and/or to limit access to the API in case security issues arise or incorrect usage of the API. 

------
Access
------
This API is a public API which can be accessed directly. 

-----------------------
Taxonomic data services
-----------------------

1.TAXON SEARCH
==============

.. _Taxon-search:

Description
-----------
This direct search service returns TAXON documents. It comes in two search type flavours, i.e. 1. Free text search, and 2. Indexed field search. 
 
Both search types execute searches through taxonomic data harvested from - currently - two data sources, i.e. a. Catalogue_of_life_, b. Nederlands_Soortenregister_. It searches a `predefined subset of indexed taxon document fields`_ and returns taxon multilingual taxon documents in JSON responses from basically species and subspecies.

.. _Catalogue_of_Life: http://www.catalogueoflife.org/
.. _Nederlands_Soortenregister: http://www.nederlandsesoorten.nl

.. _predefined subset of indexed taxon document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#taxon-search

.. _taxon request section:

Requests
--------
url
```
The basic request url for free text search is:

.. code:: html

   <base url>/taxon/search/?_search=[term], e.g. http://api.biodiversitydata.nl/v0/taxon/search/?_search=Abies

The basic request url for indexed field search is:

.. code:: html

   <base url>/taxon/search/?indexedField1=[term]&indexedField2=[term], e.g. http://api.biodiversitydata.nl/v0/taxon/search/?genusOrMonomial=Parus

indexed field name(s) in an url
```````````````````````````````
A taxon document incorporates an extensive set of fields. A subset of this set is searchable and is listed in this document_. Stating a field name in an indexed field search request can be done by either,

a. field path(s), e.g. <base url>/taxon/search/?genusOrMonomial=Abies
b. field aliases, e.g. <base url>/taxon/search/?genus=Abies

.. _document: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html

general meta parameters in an url
`````````````````````````````````
For both types of searches optional extra request parameters – so called meta parameters - can be added to the request. These meta-parameters instruct the NBA on how to execute a query.

Paging parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _maxResults
     - | • _maxResults instructs NBA to return maximum amount of search results per page
       | • default NBA setting maxResults: 10 (for requests without _maxResults parameter)                  
       | • example: request results in a list of maximum 60 documents
        
       .. code:: html

          <base url>/taxon/search/?_search=Abies&_maxResults=20,
   * - _offset
     - | • _offset instructs NBA to start filling first response from search result no = offset value + 1
       | • default NBA setting offset: 0 (for requests without _offset parameter)
       | • example: request results in a list with search result no 21 as first document in list

       .. code:: html

          <base url>/taxon/search/?_search=Abies&_offset=20. 

Sorting parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _sort
     - | • _sort instructs NBA to return responses sorted on a single specified indexed field included in taxon documents          
       | • default NBA setting sort: _score (for requests without _score parameter)
       | • remarks: _sort parameter can be used for all fields in a taxon document. Sort parameter values should be fieldpaths, e.g. gatheringEvent.locality
       | • example: request results in list sorted on unitID (in ascending order)
                   
       .. code:: html
 
          <base url>/taxon/search/?genus=Abies&_sort=defaultClassification.specificEpithet

   * - _sortDirection
     - | • _sortDirection instructs NBA on sorting direction, i.e. either descending (DESC) or ascending (ASC)
       | • default NBA setting sortDirection: ASC (for requests without _sortDirection parameter)
       | • example: request results in list sorted on unitID in descending order
     
       .. code:: html

          <base url>/taxon/search/?genus=Abies&_sort=defaultClassification.specificEpithet&_sortDirection=DESC


specific meta parameters for indexed field search
`````````````````````````````````````````````````
For indexed field search extra meta parameters are available.

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _andOr
     - | • _andOr instructs NBA to use logical operator AND (conjunction) for multiple indexed field search
       | • default NBA setting andOr: AND (for requests without _andOR parameter)
       | • example: request results in list with result of OR search on genus = Parus and species = major

       .. code:: html
               
          <base url>/taxon/search/?genus=Parus&species=major&_andOr=OR

.. _taxon response section:

Responses
---------
basic response structure
````````````````````````
The basic structure of the responses can be viewed in the subsequent example section. 
  
.. _taxon examples section:

Examples
--------

.. _free text taxon search examples section:

1. Free text search
```````````````````
Search for value Parus in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=Parus

Search for value paardenbloem (lower case term  search- english term = Dandelion) in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=paardenbloem

Search for value PAARDENBLOEM (upper case term search - english term = Dandelion) in any taxon document 
 
.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=PAARDENBLOEM

Search for value Bloem in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=Bloem

Search for value Bloem in any taxon document, maximum of 20 documents in result, sort = ascending (ASC)
   
.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=Bloem&_maxResults=20&_sort=_score&_sortDirection=ASC

Search for value Bloem in any taxon document, start from document 21

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?_search=Bloem&_offset=20

.. _indexed field taxon search examples section:

2. Indexed field search
```````````````````````
Search for key:value pair genus=Parus in any taxon document
 
.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?genus=Parus

Search for key:value pairs genus=Parus and species=major in any taxon document (default=AND)

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?genus=Parus&species=major

Search for key:value pair genus=Parus or species=major in any taxon document
   
.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?genus=Parus&species=major&_andOr=OR

Search for key:value pair paardenbloem in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?vernacularNames.name=paardenbloem

Search for key:value pair PAARDENBLOEM in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?vernacularNames.name=PAARDENBLOEM

Search for key:value pair Bloem in any taxon document

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?vernacularNames.name=Bloem

Search for key:value pair Bloem in any taxon document, 20 documents in result, sort = ascending (ASC)

.. code:: html
 
   http://api.biodiversitydata.nl/v0/taxon/search/?vernacularNames.name=Bloem&_maxResults=20&_sort=_score&_sortDirection=ASC

Search for value Bloem pair in any taxon document, start from document 21
 
.. code:: html
  
   http://api.biodiversitydata.nl/v0/taxon/search/?vernacularNames.name=Bloem&_offset=20

---------------------------------
Specimen Occurrence data services
---------------------------------

.. _Specimen-search:

1.SPECIMEN SEARCH
=================

Description
-----------
This direct search service returns TAXON documents. It comes in two search type flavours, i.e. 1. Free text search, and 2. Indexed field search. 
 
Both search types execute searches through specimen occurrence data harvested from - currently - two voluminous, Naturalis data sources, i.e. a. CRS (Collection Registration System for zoological and geological specimen) and b. Brahms for botanical specimen. It searches a `predefined subset of indexed specimen occurrence document fields`_ and returns multilingual specimen documents in JSON responses. This subset contains only fields that are not taxonomic, e.g. unitID and locality. Searches on specimen taxonomic fields can be done with the NBA service Specimen-name-search_.

.. _predefined subset of indexed specimen occurrence document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#specimen-search

.. _specimen request section:

Requests
--------
url
```
The basic request url for free text search is:

.. code:: html
 
   <base url>/specimen/search/?_search=[term], e.g. http:/api.biodiversitydata.nl/v0/specimen/search/?_search=male

The basic request url for indexed field search is:

.. code:: html
 
   <base url>/specimen/search/?indexedField1=[term]&indexedField2=[term], e.g. http://api.biodiversitydata.nl/v0/specimen/search/?typeStatus=holotype

geospatial search option in an url
``````````````````````````````````
Geospatial search can be combined with either a free text search or an indexed field search. This combined search uses default the boolean operator AND. Geosearch can also be done without additional free text or indexed field search.

Geospatial parameter

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _geoShape
     - | • _geoshape instructs NBA to return specimen documents which are  gathered by collectors during field research in a specific area
       | • default NBA setting geoshape: not applicable
       | • remarks: use lat/long coordinates.
       | • example: request results in list of specimen gathered in Jordan
                 
       .. code:: html
 
          <base url>/specimen/search/?_geoshape=list of decoded coordinates of Jordan

indexed field name(s) in an url
```````````````````````````````
A specimen document incorporates an extensive set of fields. A subset of this set is searchable and is listed in this document_. Stating a field name in a indexed field search request can be done by either,

a. field path(s), e.g. <base url>/specimen/search/?genusOrMonomial=Abies
b. field aliases, e.g. <base url>/specimen/search/?genus=Abies

.. _document: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html

general meta parameters in an url
`````````````````````````````````
For both types of searches optional extra request parameters – so called meta parameters - can be added to the request. These meta-parameters instruct the NBA on how to execute a query.

Paging parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _maxResults
     - | • _maxResults instructs NBA to return maximum amount of search results per page
       | • default NBA setting maxResults: 10 (for requests without _maxResults parameter)
       | • example: request results in a list of maximum 60 documents
        
       .. code:: html
               
          <base url>/specimen/search/?_search=male&_maxResults=20
   * - _offset
     - | • _offset instructs NBA to start filling first response from search result no = offset value + 1
       | • default NBA setting offset: 0 (for requests without _offset parameter)
       | • example: request results in a list with search result in which first document is search result no 21

       .. code:: html

          <base url>/specimen/search/?_search=allotype&_offset=20. 

Sorting parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _sort
     - | • _sort instructs NBA to return responses sorted on a single specified indexed field included in specimen documents          
       | • default NBA setting sort: _score (for requests without _score parameter)
       | • remarks: _sort parameter can be used for all fields in a specimen document. Sort parameter values should be fieldpaths, e.g. gatheringEvent.locality
       | • example: request results in list sorted on unitID (sortDirection is default Ascending) 
                   
       .. code:: html

          <base url>/specimen/search/?typeStatus=holotype&_sort=unitID

   * - _sortDirection
     - | • _sortDirection instructs NBA on sorting direction, i.e. either descending (DESC) or ascending (ASC)
       | • default NBA setting sortDirection: ASC (for requests without _sortDirection parameter)
       | • example: request results in list sorted on unitID and sortDirection is descending

       .. code:: html

          <base url>/specimen/search/?typeStatus=holotype&_sort=unitID&_sortDirection=DESC

specific meta parameters for indexed field search
`````````````````````````````````````````````````
For indexed field search extra meta parameters are available.

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _andOr
     - | • _andOr instructs NBA to use logical operator AND (conjunction) for multiple indexed field search
       | • default NBA setting andOr: AND (for requests without _andOR parameter)
       | • example: request results in list based on OR search

       .. code:: html
                  
          <base url>/specimen/search/?typeStatus=holotype&unitID=RMNH.MAM.50017&_andOr=OR

.. _specimen response section:

Responses
---------
basic response structure
````````````````````````
The basic structure of the responses can be viewed in the subsequent example section. 

.. _specimen examples section:

Examples
--------

.. _free text specimen search examples section:

1. Free text search
```````````````````
Search for RMNH.MAM.50017 in CRS in any specimen document
  
.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?_search=RMNH.MAM.50017

Search for synotype in any specimen document

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?_search=synotype

Search for juvenile in any specimen document

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?_search=juvenile

Search for mees in any specimen document

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?_search=mees

.. _indexed field specimen search examples section:

2. Indexed field search
```````````````````````
Search for key:value pair unitID=RMNH.MAM.50017 in CRS in any specimen document
   
.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?unitID=RMNH.MAM.50017

Search for key:value pair sex=male in any specimen document

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/?sex=male

Search for key:value pair collectorsFieldNumber=add a fieldnumber from a Brahms specimen

.. code:: html
   
   http://api.biodiversitydata.nl/v0/specimen/search/?collectorsFieldNumber=[Brahms fieldnumber]
    
.. _Specimen-name-search:

2.SPECIMEN-NAME SEARCH
======================

Description
-----------
This combined direct and indirect search service returns SPECIMEN occurrence documents. It comes in two search varieties, i.e. 1. Free text search, and 2. Indexed field search. In the responses it combines the outcomes of two document search processes, a. direct search on specimen occurrence documents, and, b. specimen occurrence document search based on the outcome of the name-resolution process (indirect search). This preceding process is executed on taxonomic data from all available taxonomic data sources.

Both search types execute searches though specimen occurrence data harvested from - currently - two voluminous, Naturalis data sources, i.e. a. CRS (Collection Registration System) for zoological and geological specimen, and b. Brahms for botanical specimen. It searches a `subset of indexed specimen occurrence document fields`_ and returns multilingual specimen documents in JSON responses. The list of searchable fields for this service contains only specimen taxonomic fields where-as a search on specimen not taxonomic fields can be done with the NBA service Specimen-search_.

.. _subset of indexed specimen occurrence document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#specimen-name-search

.. _specimen name request section:

Requests
--------
url
```
The basic request url for free text search is:

.. code:: html
  
   <base url>/specimen/name-search/?_search=[term], e.g. http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=Larus

The basic request url for indexed field search is:

.. code:: html
  
   <base url>/specimen/name-search/?indexedField1=[term]&indexedField2, e.g. http://api.biodiversitydata.nl/specimen/name-search/?typeStatus=holotype

geospatial search option in an url
``````````````````````````````````
Geospatial search can be combined with either a free text search or an indexed field search. This combined search uses default the boolean operator AND. Geosearch can also be done without additional free text or indexed field search.

Geospatial parameter

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _geoShape
     - | • _geoshape instructs NBA to return specimen documents which are  gathered by collectors during field research in a specific area
       | • default NBA setting geoshape: not applicable
       | • remarks: use lat/long coordinates.
       | • example: request results in list of specimen gathered in Jordan
                 
       .. code:: html
 
          <base url>/specimen/name-search/?_geoshape=decoded lat. and long coordinates of Jordan

indexed field name(s) in an url
```````````````````````````````
A specimen document incorporates an extensive set of fields. A subset of this set is searchable and is listed in this document_. Stating a field name in a indexed field search request can be done by either,

a. field path(s), e.g. <base url>/specimen/name-search/?genusOrMonomial=Abies
b. field aliases, e.g. <base url>/specimen/name-search/?genus=Abies

.. _document: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html

.. _process:

additional integrated search capabilities
`````````````````````````````````````````
Name resolution is a search process on taxonomic data from the available taxonomic data sources. This process carries out a Taxon search of the indexed field type. The basic request url is:

.. code:: html

   <basic url>/taxon/search/?vernacularNames.name=[terms out specimen name search, either simple or indexed field search].

The full scientific names of the taxon documents in the responses are input for an additional search on specimen documents.

general meta parameters
```````````````````````
For both free text and indexed field search requests optional extra request parameters – so called meta parameters - can be added. These parameters instruct the NBA on how to execute a query.

Paging parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _maxResults
     - | • _maxResults instructs NBA to return maximum amount of search results per page
       | • default NBA setting maxResults: 10 (for requests without _maxResults parameter)
       | • example: request results in list of maximum 50 documents included.

       .. code:: html                 

          <base url>/specimen/name-search/?_search=bloem&_maxResults=50
   * - _offset
     - | • _offset instructs NBA to start filling first response from search result no = offset value + 1
       | • default NBA setting offset: 0 (for requests without _offset parameter)
       | • example: request results in list in which first document is search result no 21. 
                
       .. code:: html

          <base url>/specimen/name-search/?_search=paardenbloem&_offset=20

Sorting parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _sort
     - | • _sort instructs NBA to return responses sorted on a single specified indexed field included in Specimen documents          
       | • default NBA setting sort: _score (for requests without _score parameter)
       | • remarks: _sort parameter can be used for all fields in a specimen document. Sort parameter values should be fieldpaths
       | • example: request results in list sorted on genus

       .. code:: html

          <base url>/specimen/name-search/?genus=Larus&_sort=identifications.scientificName.genusOrMonomial.

   * - _sortDirection
     - | • _sortDirection instructs NBA on sorting direction, i.e. either descending (DESC) or ascending (ASC)
       | • default NBA setting sortDirection: ASC (for requests without _sortDirection parameter)
       | • example: request results in list sorted on genus and with sort direction descending. 

       .. code:: html

          <base url>/specimen/name-search/?genus=Larus&_sort=..&_sortDirection=DESC

specific meta parameters for indexed field search
`````````````````````````````````````````````````
For indexed fields search specific search meta parameters are available.

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _andOr
     - | • _andOr instructs NBA to use logical operator AND (conjunction) for multiple indexed field search
       | • default NBA setting andOr: AND (for requests without _andOR parameter)
       | • example: request results in list based on OR search

       .. code:: html

          <base url>/specimen/name-search/?genus=Abies&species=alba&_andOr=OR

.. _specimen name response section:

Responses
---------
basic response structure
````````````````````````
The basic structure of the responses can be viewed in the subsequent example section. 

.. _specimen name examples section:

Examples
--------

.. _free text specimen name search examples section:

1. Free text search
```````````````````
Search with name resolution for Melicertus kerathurus in any specimen document
 
.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=Melicertus kerathurus

Search with name resolution for koolmees in any specimen document
  
.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=koolmees

Search with name resolution for vroedmeesterpad in any specimen document

.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=vroedmeesterpad

Search with name resolution for mees in any specimen document

.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=mees

Search with name resolution for paardenbloem in any specimen document
   
.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=paardenbloem

Search with name resolution for Parus major in any specimen document

.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?_search=Parus major

.. _indexed field specimen name search examples section:

2. Indexed field search
````````````````````````
Search with name resolution for key:value pairs order=Decapoda, family=Dynomenidae and genus=Dynomene in any specimen document

.. code:: html 

   http://api.biodiversitydata.nl/v0/specimen/name-search/?order=Decapoda&family=Dynomenidae&genus=Dynomene

Search with name resolution for key:value pair vernacularName=koolmees in any specimen document
  
.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?vernacularName=koolmees

Search with name resolution for key:value pair vernacularName=paardenbloem in any specimen document
   
.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?vernacularName=paardenbloem

Search with name resolution for key:value pair vernacularName=bloem in any specimen document

.. code:: html

   http://api.biodiversitydata.nl/v0/specimen/name-search/?vernacularName=bloem

.. _Specimen-collection-download:

3.SPECIMEN COLLECTION SEARCH
============================

Description
-----------
This direct search service returns a DwCA file containing all available SPECIMEN occurrences of a specific Naturalis Collection, Currently this service serves a DwCA file for each member of this group of Naturalis collections. The collections are categorized by the science domain for which they were gathered: animal science or Zoology, plant science or Botany and earth science or Geology. 

The creation of DwCA files is done batchwise once a month. 

.. _list of Naturalis collections:

.. list-table:: 
   :widths: 100 100 100
   :header-rows: 1

   * - Botany
     - Geology
     - Zoology
   * - Botany
     - Cainozoic mollusca
     - Amphibia and Reptilia
   * - 
     - Micropaleontology
     - Chelicerata and Myriapoda
   * - 
     - Paleontology invertebrates
     - Colembola
   * - 
     - 
     - Coleoptera
   * - 
     - 
     - Crustacea
   * - 
     - 
     - Hymenoptera
   * - 
     - 
     - Lepidoptera
   * - 
     - 
     - Mammalia
   * - 
     - 
     - Mollusca

.. _specimen collection request section:

Requests
--------
url
```
The basic request url for specimen collection search is:

.. code:: html
 
   <base url>/specimen/search/dwca/?collection=[collection name], e.g. http:/api.biodiversitydata.nl/v0/specimen/search/dwca/?collection=mammalia

.. _specimen collection response section:

Responses
---------
basic response structure
````````````````````````

All DwCA zip files have a similar internal file structure and comply to multiple data standards. 

DwCA zip file features

.. list-table:: 
   :widths: 30 70 70
   :header-rows: 1

   * - Internal file
     - Description
     - Compliancy
   * - occurrence.txt file
     - | • Core data file with specimen occurrence data
       | • Required file in zip file
       | • csv text file, tab delimiter, use of stable identifier = occurrence id (= sourceSystem.Id) per row
     - | Occurrence file complies to
       | • Mandatory DwC terms standard as stated in the `Annex of this GBIF guideline`_
       | • `DwC terms guideliness`_
   * - eml.xml file
     - | • EML profile
       | • Required file in zip file
     - | EML profile complies to 
       | • `GBIF metadata profile`_. 
       | • Minimum set of five mandatory elements as mentiond in the appendix of this `GBIF guideline`_. 
   * - meta.xml file
     - | • Descriptor metafile
       | • Required file in zip file
     - | Descriptor metafile complies to
       | • `GBIF reference guide to the XML descriptor file`_ 
       | • `DwC text guideliness`_ 
   * - extension file
     - | • Extension files are *not* included in this DwCA zip file
       | • Optional file in zip file
     -     

.. _GBIF reference guide to the XML descriptor file: http://www.gbif.org/resource/80639

.. _Annex of this GBIF guideline: http://www.gbif.org/resource/80636

.. _GBIF metadata profile: http://rs.gbif.org/schema/eml-gbif-profile/1.0.2/eml-gbif-profile.xsd

.. _GBIF guideline: http://www.gbif.org/resource/80641

.. _DwC text guideliness: http://rs.tdwg.org/dwc/terms/guides/text/index.htm

.. _DwC terms guideliness: http://rs.tdwg.org/dwc/terms/

Naturalis has validated the DwCA files by the use of the GBIF tool `DwCA Validator`_.

.. _DwCA Validator: http://tools.gbif.org/dwca-validator/

occurrence.txt file
```````````````````
All collections have a similar occurrence.txt file structure, i.e. similar list of fields. Because each science domain has a (slightly) different preference for relevant DwC occurrence values, Naturalis tries to populate DwC fields in occurrence.txt files as mentioned in the list below. 

.. warning:: ..tries to populate, because the data sources do not always supply sufficient data!

.. list-table:: 
   :widths: 70 50 50 50
   :header-rows: 1

   * - DwC term in occurrence.txt
     - Botany collection
     - Geology collection
     - Zoology collection
   * - basisOfRecord
     - Yes
     - Yes
     - Yes
   * - catalogNumber
     - Yes
     - Yes
     - Yes
   * - className
     - Yes
     - No
     - Yes
   * - collectionCode
     - Yes
     - Yes
     - Yes
   * - continent
     - Yes
     - Yes
     - Yes
   * - country
     - Yes
     - Yes
     - Yes
   * - dateIdentified
     - Yes
     - Yes
     - Yes
   * - decimalLatitude
     - Yes
     - Yes
     - Yes
   * - decimalLongitude
     - Yes
     - Yes
     - Yes
   * - eventDate
     - Yes
     - Yes
     - Yes
   * - family
     - Yes
     - No
     - No
   * - genus
     - Yes
     - Yes
     - Yes
   * - geodeticDatum
     - No
     - Yes
     - Yes
   * - habitat
     - Yes
     - Yes
     - Yes
   * - higherClassification
     - Yes
     - No
     - No
   * - identifiedBy
     - Yes
     - Yes
     - Yes
   * - individualCount
     - No
     - Yes
     - Yes
   * - informationWithHeld
     - Yes
     - Yes
     - Yes
   * - infraspecificEpithet
     - Yes
     - Yes
     - Yes
   * - island
     - No
     - No
     - No
   * - institutionCode
     - Yes
     - Yes
     - Yes
   * - kingdom
     - Yes
     - Yes
     - Yes
   * - lifeStage
     - No
     - Yes
     - Yes
   * - locality
     - Yes
     - Yes
     - Yes
   * - maximumElevationInMeters
     - Yes
     - No
     - No
   * - minimumElevationInMeters
     - Yes
     - No
     - No
   * - nomenClaturalCode
     - Yes
     - Yes
     - Yes
   * - occurrenceID
     - Yes
     - Yes
     - Yes
   * - order
     - Yes
     - No
     - No
   * - phylum
     - Yes
     - No
     - No
   * - preparations
     - No
     - Yes
     - Yes
   * - recordedBy
     - Yes
     - Yes
     - Yes
   * - scientificName
     - Yes
     - Yes
     - Yes
   * - scientificNameAuthorship
     - Yes
     - Yes
     - Yes
   * - sex
     - Yes
     - Yes
     - Yes
   * - specificEpithet
     - Yes
     - Yes
     - Yes
   * - stateProvince
     - Yes
     - Yes
     - Yes
   * - subgenus
     - Yes
     - Yes
     - Yes
   * - taxonRank
     - Yes
     - No
     - No
   * - taxonRemarks
     - Yes
     - No
     - No
   * - typeStatus
     - Yes
     - Yes
     - Yes
   * - verbatimCoordinates
     - Yes
     - Yes
     - Yes
   * - verbatimDepth
     - No
     - Yes
     - Yes
   * - verbatimElevation
     - Yes
     - Yes
     - Yes
   * - verbatimEventDate
     - Yes
     - Yes
     - Yes
   * - verbatimTaxonRank
     - Yes
     - Yes
     - Yes

Details on the DwC terms used can be found in the `BIS TDWG official DwC terms list`_.

.. _BIS TDWG official DwC terms list: http://tdwg.github.io/dwc/terms/

.. _specimen collection examples section:

Examples
--------

Download all available specimen occurrences in Naturalis Mammalia collection (part of Zoology science domain)
  
.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/dwca/?collection=mammalia

Download all available specimen occurrences in Naturalis Botany collection (Botany domain as a whole)

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/dwca/?collection=botany

Download all available specimen occurrences in Naturalis Cainozoic mollusca collection (part of Geology domain)

.. code:: html
  
   http://api.biodiversitydata.nl/v0/specimen/search/dwca/?collection=cainozoic_mollusca

------------------------
Multimedia data services
------------------------

.. _Multimedia-search:

1.MULTIMEDIA SEARCH
===================

Description
-----------
This combined direct and indirect search service returns SPECIMEN occurrence documents. It comes in two varieties, i.e. 1. Free text search, and 2. Indexed field search. In the responses it combines the outcomes of two document search processes, a. direct search on multimedia documents, and, b. multimedia document search based on the outcome of the name-resolution process (indirect search). This preceding process is executed on taxonomic data from all available taxonomic data sources.

Both search types execute searches through both specimen occurrence data and taxonomic data which include multimedia references. This data is harvested from three data sources. The excluded source is Catalogue of Life. The service searches a `predefined subset of indexed multimedia document fields`_ and returns multilingual specimen documents in JSON responses. The contents of these multimedia documents depend on the type of data source. They always include taxomic information. Geospatial and temporal information are present in specimen-derivded multimedia documents.

Due to the nature of it's sources the list of searchable fields for this service contains both taxonomic fields (taxonomic data) and not-taxonomic fields, e.g. geographic and temporal fields 

.. _predefined subset of indexed multimedia document fields: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html#specimen-name-search

.. _multimedia request section:

Requests
--------
url
```
The basic request url for free text search is:

.. code:: html

   <base url>/multimedia/search/?_search=[term], e.g. http://api.biodiversitydata.nl/v0/multimedia/search/?_search=paardenbloem

The basic request url for indexed field search is:

.. code:: html

   <base url>/multimedia/search/?indexedField1=[term]&indexedField2[term], e.g. http://api.biodiversitydata.nl/v0/multimedia/search/?genusOrMonomial=Parus

geospatial search option in an url
``````````````````````````````````
Geospatial search can be combined with either a free text search or an indexed field search. This combined search uses default the boolean operator AND. Geosearch can also be done without additional free text or indexed field search.

Geospatial parameter

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _geoShape
     - | • _geoshape instructs NBA to return specimen documents which are  gathered by collectors during field research in a specific area
       | • default NBA setting geoshape: not applicable
       | • remarks: use lat/long coordinates.
       | • example: request results in list of specimen gathered in Jordan 

       .. code:: html
           
          <base url>/multimedia/search/?_geoshape=decoded coordinates of Jordan

indexed field name(s) in an url
```````````````````````````````
A specimen document incorporates an extensive set of fields. A subset of this set is searchable and is listed in this document_. Stating a field name in a indexed field search request can be done by either,

a. field path(s), e.g. <base url>/multimedia/search/?genusOrMonomial=Larus
b. field aliases, e.g. <base url>/multimedia/search/?genus=Larus

.. _document: http://docs.biodiversitydata.nl/en/latest/Searchable%20fields%20per%20NBA%20service.html

additional integrated search capabilities
`````````````````````````````````````````
Name resolution is a search process on taxonomic data from the available taxonomic data sources. This process carries out a Taxon search of the indexed field type. The basic request url is:

.. code:: html

   <basic url>/taxon/search/?vernacularNames.name=[terms out specimen name search, either simple or indexed field search].

The full scientific names of the taxon documents in the responses are input for an additional search on specimen documents.

general meta parameters
```````````````````````
For both free text and indexed field search requests optional extra request parameters – so called meta parameters - can be added. These parameters instruct the NBA on how to execute a query.

Paging parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _maxResults
     - | • _maxResults instructs NBA to return maximum amount of search results per page
       | • default NBA setting maxResults: 10 (for requests without _maxResults parameter)
       | • example: request results in list of maximum 20 documents

       .. code:: html

          <base url>/multimedia/search/?_search=Larus&_maxResults=20
   * - _offset
     - | • _offset instructs NBA to start filling first response from search result no = offset value + 1
       | • default NBA setting offset: 0 (for requests without _offset parameter)
       | • example: request results in list in which first document is search result no 21.

       .. code:: html

          <base url>/multimedia/search/?_search=Larus&_offset=20

Sorting parameters

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _sort
     - | • _sort instructs NBA to return responses sorted on a single specified indexed field included in multimedia documents          
       | • default NBA setting sort: _score (for requests without _score parameter)
       | • remarks: _sort parameter can be used for all fields in a multimedia document. Sort parameter values should be a fieldpath, e.g. identifications.scientificName.subgenus
       | •  example: request results in list sorted on unitID

       .. code:: html

          <base url>/multimedia/search/?genus=Larus&_sort=unitID

   * - _sortDirection
     - | • _sortDirection instructs NBA on sorting direction, i.e. either descending (DESC) or ascending (ASC)
       | • default NBA setting sortDirection: ASC (for requests without _sortDirection parameter)
       | • example: request results in list sorted on unitID and sort direction is descending

       .. code:: html

          <base url>/multimedia/search/?genus=Larus&_sort=unitID&_sortDirection=DESC

specific meta parameters for indexed field search
`````````````````````````````````````````````````
For indexed field search extra meta parameters are available.

.. list-table:: 
   :widths: 15 100
   :header-rows: 1

   * - Name
     - Description
   * - _andOr
     - | • _andOr instructs NBA to use logical operator AND (conjunction) for multiple indexed field search
       | • default NBA setting andOr: AND (for requests without _andOR parameter)
       | • example: request results in list based on OR search

       .. code:: html

          <base url>/multimedia/search/?genus=Larus&species=argentatus&_andOr=OR

.. _multimedia response section:

Responses
---------
basic response structure
````````````````````````
The basic structure of the responses can be viewed in the subsequent example section. 

.. _multimedia examples section:

Examples
--------

.. _free text multimedia search examples section:

1. Free text search
```````````````````

Search for koolmees in any multimedia document

.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?_search=koolmees

Search for mees in any multimedia document

.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?_search=mees

Search for Parus major in any multimedia document

.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?_search=Parus major

Search for Melicertus kerathurus in any multimedia document

.. code:: html 

   http://api.biodiversitydata.nl/v0/multimedia/search/?_search=Abies abies
    
Search for L.4275659 in Brahms in any multimedia document

.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?_search=L.4275659

.. _indexed field multimedia search examples section:

2. Indexed field search
```````````````````````

Search for key:value pair unitID=AHCYFCKPYRK:1560587210 in NSR in any multimedia document
  
.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?unitID=AHCYFCKPYRK:1560587210

Search for key:value pair vernacularName=paardenbloem in any multimedia document
   
.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?vernacularName=paardenbloem

Search for key:value pairs genus=Parus and species=major in any multimedia document

.. code:: html

   http://api.biodiversitydata.nl/v0/multimedia/search/?genus=Parus&species=major
