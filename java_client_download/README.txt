This folder contains the NBA Java client library: nba-java-client.jar

The "dependencies" subdirectory contains all 3rd party dependencies
you will need alongside the NBA java client library.

In case you are using Maven, Ivy or some other dependency management
tool, check the ivy.xml file in this folder to see which dependencies
you need to declare in your own pom.xml or ivy.xml

For the javadocs, see here:
http://naturalis.github.io/naturalis_data_api/javadoc/v2/client/


EXAMPLES:

________________________________________________________________________
Here is a basic example of how you can use the NBA Java client:

NbaSession session = new NbaSession();
SpecimenClient client = session.getSpecimenClient();
Specimen specimen = client.findByUnitID("ZMA.RMNH.12345");
System.out.println("Record basis for specimen ZMA.RMNH.12345: " + specimen.getRecordBasis());
 
 
________________________________________________________________________
Here is a more interesting example:

NbaSession session = new NbaSession();
TaxonClient client = session.getTaxonClient();
QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
condition.and("acceptedName.specificEpithet", "=", "fuscus");
QuerySpec query = new QuerySpec();
query.addCondition(condition);
Taxon[] taxa = client.query(query);
for (Taxon taxon : taxa) {
	System.out.println("Taxon ID: " + taxon.getId());
}


________________________________________________________________________
Here is how you can download that same query as a DarwinCore archive:

TaxonClient client = session.getTaxonClient();
QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
condition.and("acceptedName.specificEpithet", "=", "Fuscus");
QuerySpec query = new QuerySpec();
query.addCondition(condition);
FileOutputStream fos = new FileOutputStream("C:/tmp/my-dwca.zip");
client.dwcaQuery(query, fos);
fos.close();


________________________________________________________________________
And here is how you can download all pre-defined specimen datasets:

NbaSession session = new NbaSession();
SpecimenClient client = session.getSpecimenClient();
for(String dataset : client.dwcaGetDataSetNames()) {
	System.out.println("Downloading dataset " + dataset);
	FileOutputStream fos = new FileOutputStream("C:/tmp/" + dataset + ".zip");
	client.dwcaGetDataSet(dataset, fos);
	fos.close();
}

