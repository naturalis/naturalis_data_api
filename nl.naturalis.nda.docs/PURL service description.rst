=========================
PURL Service description
=========================

-------------------------
Document management
-------------------------

.. list-table:: 
   :widths: 20 100
   :header-rows: 0
   
   * - Author
     - Naturalis ICT sector, Software Development department
   * - Document version
     - c1.0.
   * - Document publication date
     - 26-10-2015
   * - Content
     - Description of purl specimen retrieval services in PURL v.1.0.


-------------------------
Introduction
-------------------------
With the specimen PURL service Naturalis introduces a new type of webservice in the Naturalis webservice portfolio. The PURL service is primarly intended for computers to communicate with other computers.

When referencing Naturalis specimens (e.g. in scientific articles), PURLs should be used in preference to more transient URLs like those from the Naturalis Bioportal.

Each PURL is a kind of Persistent Identifier that relies on HTTP.

A specimen PURL example:

.. code:: html

  http://data.biodiversitydata.nl/naturalis/specimen/RMNH.INS.389961

PURL description 1
   PURLs (Persistent Uniform Resource Locators) are Web addresses that act as permanent identifiers in the face of a 
   dynamic and changing Web infrastructure. Instead of resolving directly to Web resources, PURLs provide a level of 
   indirection that allows the underlying Web addresses of resources to change over time without negatively affecting 
   systems that depend on them. This capability provides continuity of references to network resources that may migrate 
   from machine to machine for business, social or technical reasons. 
   
   Source: `wiki`_

PURL description 2
   PURL identifiers are based on URIs and use the HTTP redirect mechanism to avoid broken links.
    a. e.g., http://purl.oclc.org/example/specimen/12921 and http://purl.org/dc/terms/contributor
    b. web-based identifier using standard HTTP and HTTP redirect. Can be resolved through use of a common PURL resolver.
    c. promoted by the OCLC (Online Computer Library Center) [OCLC]
    d. independent generation is enabled by the use of domain names.
    e. may not be opaque due to the use of domain names, context names and sometimes descriptive object identifiers.  
    f. authority, context and object identifier components can be defined using the path portion of the PURL (as shown in the example above).
   
  Source: `A Beginner's Guide to Persistent Identifiers`_
  
With the publication of the specimen PURL service Naturalis takes a first step in it's planned contribution to 
the need of persistent identifiers for Life Sciences. Naturalis has decided to use PURL as a persistent identifier mechanism because of their ease-of-use, relative implementation-ease and strong technical community support. 

Every Naturalis specimen PURL refers to a physical object in the our botanical, geological and zoological collections. Because Naturalis cannot (yet) serve a physicial object via the http protocol, Naturalis will instead serve one or more digital representations of the requested physical object, e.g. a specimen document in text/html format.  

The specimen PURL service returns each specific specimen document default as text/html. Each text/html document is default shown in `a specimen detail page of the Naturalis Bioportal`_. 

Through 'HTTP content negotiation'_ three other document content types can - depending on their availability - also be served, i.e. application/json, image/jpeg and video/mp4. Currently about 7,3 million Naturalis' specimens can be directly accessed through this PURL service.  

.. _wiki : https://en.wikipedia.org/wiki/Persistent_uniform_resource_locator

.. _A Beginner's Guide to Persistent Identifiers : http://www.gbif.org/resource/80575

.. _a detail page of the Naturalis Bioportal : http://data.biodiversitydata.nl/naturalis/specimen/ZMA.MOLL.228360
------
Access
------
The specimen PURL service is a public service.

---------
Base url
---------

The base url for the specimen PURL service is: 

.. code:: html

  http://data.biodiversitydata.nl/

-------------------
Recommended tooling
-------------------

For exploration or testing of the PURL service and/or NBA services we recommend using a command-line tool 
for transferring data like e.g. `curl`_ or a REST client browser plugin, e.g. the chrome rest client, in case
you want to work with a browser.
 
.. _curl : http://curl.haxx.se/

-----------
Quick start
-----------

Each specimen PURL follows this general form:

.. code:: html

  http://data.biodiversitydata.nl/{name of data owner organisation or institute}/{object type}/{unitID}

Currently, the PURL specimen service serves solely Naturalis specimen data, therefore
the available {name of data owner organisation or institute name} = naturalis and the available 
{object type} = specimen resulting in the Naturalis specimen PURL general form:

.. code:: html

  http://data.biodiversitydata.nl/naturalis/specimen/{unitID}

Each specimen PURL request returns specimen data default in a text/html formatted document on `a 
Naturalis Bioportal detail page`_. 

Content type: *text/html*, an example

.. code:: html

  http://data.biodiversitydata.nl/naturalis/specimen/ZMA.AVES.39215

HTTP content negotiation can be used to request alternative media types besides HTML. Content negotiation through HTTP Accept headers can be mimicked by including an "__accept" query parameter in the URL (see example below).

.. list-table:: 
   :widths: 50 80
   :header-rows: 1

   * - Requested content types
     - PURL resolves to ..
   * - application/json
     - a Netherlands Biodiversity API response
   * - image/jpeg
     - a Naturalis Media Library response
   * - video/mp4
     - a Naturalis Media Library response
   * - text/html (default)
     - a Naturalis Bioportal Detail Page response

Content type: *application/json*, an example

.. code:: html
       
      http://data.biodiversitydata.nl/naturalis/specimen/ZMA.AVES.39215?__accept=application/json
       
Content type: *image/jpeg*, an example

.. code:: html

      http://data.biodiversitydata.nl/naturalis/specimen/AMD.118855?__accept=image/jpeg

Content type: *video/mp4*, an example

.. code:: html

      http://data.biodiversitydata.nl/naturalis/specimen/RMNH.AVES.110091?__accept=video/mp4

.. _a Naturalis Bioportal detail page : http://data.biodiversitydata.nl/naturalis/specimen/ZMA.MOLL.228360

-------------------------
PURL processing in detail
-------------------------
All purl requests are subjected to two basic checks.

1. specimen unitID presence check, if no, purl service responds with HTTP 404 (Not Found)
2. content type presence check, i.e.

a. purl service forwards to `Bioportal`_ (303 See Other). A text/html document is available for all specimens if accept header value(s) is either
    - absent, e,g, empty, or
    - solely text/html, or
    - text/html followed by other content types, e.g. text/html,application/json,image/jpeg, or
    - other content types followed by text/html, e.g. image/jpeg,video/mp4,text/html, if all the other content types are not available.
		
b. purl service forwards to `NBA`_ (303 See Other). An application/json document is available for all specimens if accept header value(s) is either
    - solely application/json, or,
    - application/json followed by other content types, e.g. application/json,text/hrml,image/jpeg etc, or
    - other content types followed by application/json, e.g. image/jpeg,video/mp4,application/json, if all the other
      content types, or are not available.
	
c. purl service forwards to `Naturalis Medialib`_ (303 See Other) if
    - one of the multimedia content types is available, i.e. image/jpeg or video/mp4, and
    - text/html and application/json are not requested in the accept header.
		
d. purl service responds with HTTP 406 (Not Acceptable) if non of the requested multimedia content types is available. Along with the response comes a list of alternative media types that are servicable.

.. _Bioportal : http://bioportal.naturalis.nl

.. _NBA : http://api.biodiversitydata.nl/v0/version

.. _Naturalis Medialib : http://medialib.naturalis.nl

----------
Versioning
----------
The specimen PURL version = v1.

-------------------------------------------
Persistent identifier compliancy
-------------------------------------------
As said, each PURL is a kind of Persistent Identifier that relies on HTTP.
But how does this service comply to the general characteristics of a Persistent Identifier mentioned in 
`A Beginner's Guide to Persistent Identifiers`_ published by GBIF_? 
We've tried to 'score' the service and/or comment on it's feature.

**A PID is globally unique**
Naturalis beliefs the chosen url structure guarantees global uniqueness for specimens.
Especially the combination of the data owner institution or organisation name, and, a 
unitID which abides to [complies with] international bio information standards is key in guaranteeing the uniqueness.

**A PID exists indefinitely**
Naturalis will try to assure the permanent character of its PURLs.
Naturalis makes a dedicated effort to assure the permanent character of its PURLs.

**A PID is unambigiously applied**
The specimen PURL service serves digital representations of physical specimens in our collection catalogues. 
Multiple content types per physical specimen can be requested. 
Specimen representations are being served based on their availability. 

**A PID is opaque**
One of the most important qualities of a good Persistent identifier is opacity. 
That is, the identifier itself should not contain any readable information. 
Underlying argument is that Persistent identifiers are intended for computers to communicate with other computers. 
Obviously the Naturalis PURL service does not comply to this rule. Non-opacity is a rather common feature of a purl service.
In this case Naturalis chose for non-opacity so a researcher searching for relevant biodiversity data 
can quickly understand the nature of the data on offer.

**A PID is permantly assigned to an object**
Naturalis will try to assure the permanent assignment to an object.
Naturalis makes a dedicated effort to assure the permanent assignment to an object.


**A PID is actionable**
Naturalis PURL specimen service is highly actionable. 
It's actionability is mainly restricted by the availability of the requested specimen representations.

**A PID allows for universal cross linking of information**
Cross linking through PIDs is not yet implemented in the specimen PURL service.

.. _GBIF: http://www.gbif.org/resource/80575

-----------------------------
Background information PURLs
-----------------------------
If you want to know more about PURLs please consult this documentation. 
http://www.paradigm.ac.uk/workbook/metadata/pids-purl.html

.. _content negotiation: https://en.wikipedia.org/wiki/Content_negotiation
