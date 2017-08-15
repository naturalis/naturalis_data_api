This folder the NBA Java client library: nl.naturalis.nba.client.jar

This library depends on three other NBA Java libraries, also present in
this folder:
o  nl.naturalis.nba.common.jar
o  nl.naturalis.nba.api.jar
o  nl.naturalis.nba.utils.jar

The "dependencies" subdirectory contains all 3rd party dependencies.

In case you are using Maven, Ivy or some other dependency management
tool, inspect the ivy.xml in this folder to see which dependencies you
need to declare in your own pom.xml or ivy.xml


EXAMPLES:

________________________________________________________________________
Here is a basic example of how you can use the NBA Java client:

NbaSession session = new NbaSession();
SpecimenClient client = session.getSpecimenClient();
Specimen specimen = client.findByUnitID("ZMA.RMNH.12345");
System.out.printf("Record basis for specimen ZMA.RMNH.12345: " + specimen.getRecordBasis());
 
 
________________________________________________________________________
Here is a more interesting example:

NbaSession session = new NbaSession();
TaxonClient client = session.getTaxonClient();
QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
condition.and("acceptedName.specificEpithet", "=", "Fuscus");
QuerySpec query = new QuerySpec();
query.addCondition(condition);
Taxon[] taxa = client.query(query);
for (Taxon taxon : taxa) {
	System.out.println("Taxon id: " + taxon.getId());
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
	FileOutputStream fos = new FileOutputStream("C:/tmp/" + dataset + ".zip");
	client.dwcaGetDataSet(dataset, fos);
	fos.close();
}

